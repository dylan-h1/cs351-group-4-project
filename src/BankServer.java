import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

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
        onlineUsers = new ConcurrentHashMap<>();
        interestRate = 2.5;
        interestPeriod = 60;
        interestSchedule();
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
            }
            if (scheduler != null) {
                scheduler.shutdown();
            }
        }
        saveData();
    }

    public void notifyUser(String username, String message) {
        ClientHandler handler = onlineUsers.get(username);
        if (handler != null) {
            handler.sendMessage(message);
        }
    }

    public void interestSchedule() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(
                this::applyInterest,
                interestPeriod,
                interestPeriod,
                TimeUnit.SECONDS
                );
    }

    public void applyInterest() {
        for (Account account : accounts.values()) {
            double interest = account.getBalance() * interestRate / 100;
            account.deposit(interest);
            transactionLedger.add(
                    "INTEREST",
                    "BANK",
                    account.username,
                    interest
            );
            notifyUser(account.username,
                    String.format(
                            "Interest of %.2f applied to your account. New balance: %.2f",
                            interest,
                            account.getBalance()));
        }
    }

    public void saveData() {
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

    public void loadData() {
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