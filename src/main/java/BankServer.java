import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    List<String> serverLogs;
    protected ConcurrentHashMap<Socket, ClientHandler> connectedClients;
    protected boolean isRunning = false;


    public BankServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientPool = Executors.newFixedThreadPool(20);
        accounts = new ConcurrentHashMap<>();
        transactionLedger = new TransactionLedger();
        onlineUsers = new ConcurrentHashMap<>();
        serverLogs = new ArrayList<>();
        connectedClients = new ConcurrentHashMap<>();
        interestRate = 2.5;
        interestPeriod = 60;
        interestSchedule();
        loadData();
    }

    @Override
    public void run() {
        isRunning = true;
        System.out.println("Bank server started on port " + serverSocket.getLocalPort());

        while (isRunning) {
            try{
                Socket socket = serverSocket.accept();
                log("New client connected");
                ClientHandler handler = new ClientHandler(socket, this);
                connectedClients.put(socket, handler);
                clientPool.submit(handler);
            } catch (IOException e){
                if (isRunning) {
                    e.printStackTrace();
                } else {
                    System.out.println("Server shutting down.");
                }
            }
        }
    }

    public void stop() {
        isRunning = false;

        for (ClientHandler handler : connectedClients.values()) {
            handler.sendMessage("SERVER_SHUTDOWN");
            try {
                handler.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
            double interest;
            account.lock.lock();
            try {
                interest = account.getBalance() * interestRate / 100;
                account.deposit(interest);
            } finally {
                account.lock.unlock();
            }

            transactionLedger.addNewTransaction(
                    "INTEREST",
                    "BANK",
                    account.username,
                    interest
            );

            notifyUser(account.username,
                    String.format("[" + LocalDateTime.now() + "] " + "[INTEREST] £%.2f interest applied. New balance: £%.2f",
                            interest,
                            account.getBalance()));
        }
    }

    public void saveData() {
        saveAccounts();
        saveTransactionLedger();
    }

    public void loadData() {
        loadAccounts();
        loadTransactionLedger();
    }

    public void saveAccounts() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("accounts.csv"));

            writer.println("username,password,balance");

            for (Account account : accounts.values()) {
                writer.printf("%s,%s,%f%n", account.username, account.password, account.balance);
            }

            writer.flush();
            writer.close();

            log("Saved to accounts.csv");
        } catch (IOException e) {
            log("Error saving accounts to accounts.csv" + e.getMessage());
        }
    }

    public void saveTransactionLedger() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("ledger.csv"));

            writer.println("type,from,to,amount,timestamp");

            for (Transaction transaction : transactionLedger.getAllTransactions()) {
                writer.printf("%s,%s,%s,%f,%s%n", transaction.getType(), transaction.getFrom(), transaction.getTo(), transaction.getAmount(), transaction.getTimestamp());
            }

            writer.flush();
            writer.close();

            log("Saved to ledger.csv successfully");
        } catch (IOException e) {
            log("Error saving transaction data to ledger.csv" + e.getMessage());
        }
    }

    public void loadAccounts() {
        File file = new File("accounts.csv");

        if (!file.exists()) {
            System.out.println("No account data present, creating new data");
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // skips header of csv (username,password,balance)
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");

                String username = details[0];
                String password = details[1];
                double balance = Double.parseDouble(details[2]);

                Account account = new Account(username, password, balance);
                accounts.put(username, account);
            }

            log("Loaded from accounts.csv successfully");
        } catch (FileNotFoundException e) {
            log("accounts.csv not found" + e.getMessage());
        } catch (IOException e) {
            log("Error loading accounts.csv" + e.getMessage());
        }
    }

    public void loadTransactionLedger() {
        File file = new File("ledger.csv");

        if (!file.exists()) {
            System.out.println("No ledger data present, creating new data");
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // skips header of csv (type,from,to,amount,timestamp)
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");

                String type = details[0];
                String from = details[1];
                String to = details[2];
                double amount = Double.parseDouble(details[3]);
                LocalDateTime timestamp = LocalDateTime.parse(details[4]);

                Transaction transaction = new Transaction(type, from, to, amount, timestamp);
                transactionLedger.addExistingTransaction(transaction);
            }

            log("Loaded from ledger.csv successfully");
        } catch (FileNotFoundException e) {
            log("ledger.csv not found" + e.getMessage());
        } catch (IOException e) {
            log("Error loading ledger.csv" + e.getMessage());
        }
    }

    public void log(String msg) {
        String fullMsg = "[" + LocalDateTime.now() + "] " + msg;
        serverLogs.add(fullMsg);
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