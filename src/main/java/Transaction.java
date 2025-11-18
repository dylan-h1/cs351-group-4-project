import java.io.Serializable;
import java.time.LocalDateTime;

public class Transaction implements Serializable{

    private final String type;
    private final String from;
    private final String to;
    private final double amount;
    private final LocalDateTime timestamp;

    public Transaction(String type, String from, String to, double amount) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(String type, String from, String to, double amount, LocalDateTime timestamp) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: from=%s to=%s amount=£%.2f",
                timestamp, type, from, to, amount);
    }

    public String getFrom() { return from; }
    public String getTo() { return to; }

    //added these getters for the test cases
    public double getAmount() { return amount; }
    public String getType() { return type; }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}