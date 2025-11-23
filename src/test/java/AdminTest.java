import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminTest {
    private BankServer bankServer;
    private AdminMenu adminMenu;
    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    public void setUp() throws Exception {
        bankServer = new BankServer(0);
        adminMenu = new AdminMenu(bankServer);
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);

        if (bankServer != null) {
            bankServer.stop();
        }
    }

    @Test
    public void viewNoOnlineUsers() throws Exception {
        bankServer.onlineUsers = null;

        outContent.reset();
        adminMenu.viewOnlineUsers();

        String output = outContent.toString();
        assertTrue(output.contains("No users currently online."));
    }

    @Test
    public void viewOnlineUsers() throws Exception {
        outContent.reset();

        ClientHandler user1 = new ClientHandler(new Socket(), bankServer);
        ClientHandler user2 = new ClientHandler(new Socket(), bankServer);

        bankServer.onlineUsers.put("alice", user1);
        bankServer.onlineUsers.put("bob", user2);

        adminMenu.viewOnlineUsers();
        String output = outContent.toString();
        assertTrue(output.contains("alice"));
        assertTrue(output.contains("bob"));
    }

    @Test
    public void adjustUserBalance() throws Exception {
        bankServer.accounts.put("alice", new Account("alice", "pass"));
        String fakeInput = "alice\nadd\n200.0\n";
        System.setIn(new java.io.ByteArrayInputStream(fakeInput.getBytes()));
        AdminMenu adminMenu = new AdminMenu(bankServer);
        adminMenu.adjustUserBalance();
        assertEquals(1200.0, bankServer.accounts.get("alice").getBalance());
    }

    @Test
    public void transferBetweenUsers() throws Exception {
        bankServer.accounts.put("alice", new Account("alice", "pass"));
        bankServer.accounts.put("bob", new Account("bob", "word"));
        String fakeInput = "alice\nbob\n300.0\n";
        System.setIn(new java.io.ByteArrayInputStream(fakeInput.getBytes()));
        AdminMenu adminMenu = new AdminMenu(bankServer);
        adminMenu.transferBetweenUsers();
        assertEquals(700.0, bankServer.accounts.get("alice").getBalance());
        assertEquals(1300.0, bankServer.accounts.get("bob").getBalance());
    }

    @Test
    public void setInterestRate() throws Exception {
        String fakeInput = 2.5 + "\n";
        System.setIn(new java.io.ByteArrayInputStream(fakeInput.getBytes()));
        AdminMenu adminMenu = new AdminMenu(bankServer);
        adminMenu.changeInterestRate();
        assertEquals(2.5, bankServer.interestRate);
    }

    @Test
    public void setInterestPeriod() throws Exception {
        String fakeInput = 30 + "\n";
        System.setIn(new java.io.ByteArrayInputStream(fakeInput.getBytes()));
        AdminMenu adminMenu = new AdminMenu(bankServer);
        adminMenu.changeInterestPeriod();
        assertEquals(30, bankServer.interestPeriod);
    }

    @Test
    public void showLogs() throws Exception {
        outContent.reset();
        adminMenu.showLogs();
        String output = outContent.toString();
        assertTrue(output.contains("===== SERVER LOGS ====="));
    }

}
