import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class BankServer implements Runnable {

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
        accounts = new ConcurrentHashMap<>();
        transactionLedger = new TransactionLedger();
        loadData();
    }

    @Override
    public void run() {
        isRunning = true;
        System.out.println("Bank server started on port " + serverSocket.getLocalPort());
        try{
            while (isRunning) {
                try{
                    Socket socket = serverSocket.accept();
                    System.out.println("New client connected");
                    clientPool.submit(new ClientHandler(socket, this));
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
            if (clientPool != null) {
                clientPool.shutdown();
                saveData();
            }
        }
    }

    void notifyUser(String username, String message) {
        ClientHandler handler = onlineUsers.get(username);
        if (handler != null) {
            handler.sendMessage(message);
        }
    }

    void applyInterest() {
    }

    void saveData() {
        try{
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("accounts.dat"));
            out.writeObject(accounts);
            out.close();
        } catch (IOException e) {
            System.out.println("Error saving accounts data:");
        }
        try{
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("ledger.dat"));
            out.writeObject(transactionLedger);
            out.close();
        } catch (IOException e) {
            System.out.println("Error saving ledger data:");
        }
    }

    void loadData() {
        File accountsFile = new File("accounts.dat");
        if (accountsFile.exists()) {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(accountsFile));
                accounts = (ConcurrentHashMap<String, Account>) in.readObject();
                in.close();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading accounts data:");
            }
        }
        File ledgerFile = new File("ledger.dat");
        if (ledgerFile.exists()) {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(ledgerFile));
                transactionLedger = (TransactionLedger) in.readObject();
                in.close();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading ledger data:");
            }
        }
    }

    public static void main(String[] args) {
        try {
            BankServer bankServer = new BankServer(9000);

            Thread serverThread = new Thread(bankServer);
            serverThread.start();

            AdminMenu adminMenu = new AdminMenu(bankServer);
            adminMenu.showMenu();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}