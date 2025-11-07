import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class BankServer {

    ServerSocket serverSocket;
    ExecutorService clientPool;
    ConcurrentHashMap<String, Account> accounts;
    TransactionLedger ledger;
    ConcurrentHashMap<String, ClientHandler> onlineUsers;
    ScheduledExecutorService scheduler;
    double interestRate;
    long interestPeriod;

    void start() {
    }

    void stop() {
    }

    void notifyUser(String username, String message) {
    }

    void applyInterest() {
    }

    void saveData() {
    }

    void loadData() {
    }
}