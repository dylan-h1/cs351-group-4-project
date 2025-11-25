import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.Socket;

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
    public void viewNoOnlineUsers() {
        bankServer.onlineUsers = null;

        outContent.reset();
        adminMenu.viewOnlineUsers();

        String output = outContent.toString();
        assertTrue(output.contains("No users currently online."));
    }

    @Test
    public void viewOnlineUsers() {
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
    public void adjustUserBalance() {
        bankServer.accounts.put("alice", new Account("alice", "pass"));
        String fakeInput = "3\nalice\nadd\n200.0\n8\n";
        System.setIn(new java.io.ByteArrayInputStream(fakeInput.getBytes()));
        AdminMenu adminMenu = new AdminMenu(bankServer);
        adminMenu.showMenu();
        assertEquals(1200.0, bankServer.accounts.get("alice").getBalance());
    }

    @Test
    public void transferBetweenUsers() {
        bankServer.accounts.put("alice", new Account("alice", "pass"));
        bankServer.accounts.put("bob", new Account("bob", "word"));
        String fakeInput = "4\nalice\nbob\n300.0\n8\n";
        System.setIn(new java.io.ByteArrayInputStream(fakeInput.getBytes()));
        AdminMenu adminMenu = new AdminMenu(bankServer);
        adminMenu.showMenu();
        assertEquals(700.0, bankServer.accounts.get("alice").getBalance());
        assertEquals(1300.0, bankServer.accounts.get("bob").getBalance());
    }

    @Test
    public void setInterestRate() {
        String fakeInput = "5\n2.5\n8\n";
        System.setIn(new java.io.ByteArrayInputStream(fakeInput.getBytes()));
        AdminMenu adminMenu = new AdminMenu(bankServer);
        adminMenu.showMenu();
        assertEquals(2.5, bankServer.interestRate);
    }

    @Test
    public void setInterestPeriod() {
        String fakeInput = "6\n30\n8\n";
        System.setIn(new java.io.ByteArrayInputStream(fakeInput.getBytes()));
        AdminMenu adminMenu = new AdminMenu(bankServer);
        adminMenu.showMenu();
        assertEquals(30, bankServer.interestPeriod);
    }

    @Test
    public void showLogs() {
        String fakeInput = "7\n8\n";
        System.setIn(new java.io.ByteArrayInputStream(fakeInput.getBytes()));
        outContent.reset();
        AdminMenu adminMenu = new AdminMenu(bankServer);
        adminMenu.showMenu();
        String output = outContent.toString();
        assertTrue(output.contains("===== SERVER LOGS ====="));
    }

}
