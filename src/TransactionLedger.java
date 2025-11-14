import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionLedger {

    private final List<Transaction> transactions = Collections.synchronizedList(new ArrayList<>());

    public void add(String type, String from, String to, double amount) {
        transactions.add(new Transaction(type, from, to, amount));
    }

    public List<Transaction> getUserTransactions(String username) {
        synchronized (transactions) {
            List<Transaction> userTransactions = new ArrayList<>();
            for (Transaction transaction : transactions) {
                if (transaction.getFrom().equals(username) || transaction.getTo().equals(username)) {
                    userTransactions.add(transaction);
                }
            }
            return userTransactions;
        }
    }

    public List<Transaction> getAllTransactions() {
        synchronized (transactions) {
            // returning a copy of transactions instead of actual object to prevent modifications from other threads apart from add method
            return new ArrayList<>(transactions);
        }
    }
}