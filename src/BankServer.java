import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class BankServer implements Runnable {

    // To discuss if we should have all as private access, then access these through getters i.e. protected fields having getters
    private ServerSocket serverSocket;
    private ExecutorService clientPool;
    protected ConcurrentHashMap<String, Account> accounts;
    protected TransactionLedger transactionLedger;
    protected ConcurrentHashMap<String, ClientHandler> onlineUsers;
    private ScheduledExecutorService scheduler;
    protected double interestRate;
    protected long interestPeriod;
    private boolean isRunning = false;

    public BankServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientPool = Executors.newFixedThreadPool(20);
    }

    @Override
    public void run() {
        isRunning = true;
        System.out.println("Bank server started");
        try{
            while (isRunning) {
                try{
                    Socket socket = serverSocket.accept();
                    System.out.println("New client connected");
                    clientPool.submit(new ClientHandler(socket));
                } catch (IOException e){
                    if (isRunning) {
                        e.printStackTrace();
                    } else {
                        System.out.println("Server shutting down.");
                    }
                }
            }
        } finally {
            stop();
        }
    }

    void stop() {
        isRunning = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void notifyUser(String username, String message) {
    }

    void applyInterest() {
    }

    void saveData() {
    }

    void loadData() {
    }

    public static void main(String[] args) {
        try {
            BankServer bankServer = new BankServer(9000);

            Thread serverThread = new Thread(bankServer);
            serverThread.start();
//            bankServer.run();

            AdminMenu adminMenu = new AdminMenu(bankServer);
            adminMenu.showMenu();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}