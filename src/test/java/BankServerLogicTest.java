import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class BankServerLogicTest {
    private BankServer bankServer;

    @AfterEach
    public void tearDown() {
        if (bankServer != null) {
            bankServer.stop();
        }
    }

    private int getServerPort(BankServer server) throws Exception {
        Field field = BankServer.class.getDeclaredField("serverSocket");
        field.setAccessible(true);
        ServerSocket serverSocket = (ServerSocket) field.get(server);
        return serverSocket.getLocalPort();
    }

    private ServerSocket getServerSocket(BankServer server) throws Exception {
        Field field = BankServer.class.getDeclaredField("serverSocket");
        field.setAccessible(true);
        return (ServerSocket) field.get(server);
    }

    @Test
    public void AccountTransfer() throws Exception {
        bankServer = new BankServer(0);

        Account alice = new Account("alice", "pass");
        Account bob = new Account("bob", "word");

        assert (alice.getBalance() == 1000.0);
        assert (bob.getBalance() == 1000.0);

        alice.deposit(1000.0);
        assert (alice.getBalance() == 2000.0);
        alice.withdraw(500.0);
        assert (alice.getBalance() == 1500.0);

        alice.transferTo(bob, 300.0);
        assert (alice.getBalance() == 1200.0);
        assert (bob.getBalance() == 1300.0);

        alice.transferTo(bob, 2000.0);
        assert (alice.getBalance() == 1200.0);
        assert (bob.getBalance() == 1300.0);

        System.out.println("BankServerLogicTest completed successfully.");
    }

    @Test
    public void StartAndStopServer() throws Exception {
        bankServer = new BankServer(0);
        Thread serverThread = new Thread(bankServer);
        serverThread.start();

        Thread.sleep(100);
        assertTrue(bankServer.isRunning);

        int port = getServerPort(bankServer);
        Socket clientSocket = new Socket("localhost", port);
        assertTrue(clientSocket.isConnected());
        Thread.sleep(100);
        assertEquals(1, bankServer.connectedClients.size());

        bankServer.stop();
        serverThread.join(1000);

        assertFalse(bankServer.isRunning);
        assertTrue(getServerSocket(bankServer).isClosed());

        clientSocket.close();
    }

    @Test
    public void MultiClientHandling() throws Exception {
        bankServer = new BankServer(0);
        Thread serverThread = new Thread(bankServer);
        serverThread.start();

        Thread.sleep(100);
        int port = getServerPort(bankServer);

        int clientCount = 5;
        Socket[] clientSockets = new Socket[clientCount];

        for (int i = 0; i < clientCount; i++) {
            clientSockets[i] = new Socket("localhost", port);
            assertTrue(clientSockets[i].isConnected());
        }

        Thread.sleep(100);

        assertEquals(clientCount, bankServer.connectedClients.size());

        for (Socket clientSocket : clientSockets) {
            clientSocket.close();
        }

        bankServer.stop();
        serverThread.join(1000);
    }
}
