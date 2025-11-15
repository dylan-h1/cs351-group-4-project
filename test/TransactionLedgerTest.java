import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionLedgerTest {
    @Test
    public void addAndQueryTransactions() {
        TransactionLedger ledger = new TransactionLedger();

        ledger.add("deposit", "alice", "alice", 100.0);
        ledger.add("withdraw", "alice", "alice", 50.0);
        ledger.add("transfer", "bob", "alice", 200.0);


        var aliceTransactions = ledger.getUserTransactions("alice");
        assertEquals(3, aliceTransactions.size());
        assertTrue(aliceTransactions.stream().anyMatch(t ->
                t.getType().equals("deposit") && t.getFrom().equals("alice") && t.getTo().equals("alice") && t.getAmount() == 100.0));
        assertTrue(aliceTransactions.stream().anyMatch(t ->
                t.getType().equals("withdraw") && t.getFrom().equals("alice") && t.getTo().equals("alice") && t.getAmount() == 50.0));
        assertTrue(aliceTransactions.stream().anyMatch(t ->
                t.getType().equals("transfer") && t.getFrom().equals("bob") && t.getTo().equals("alice") && t.getAmount() == 200.0));
    }

    @Test
    public void getAllTransactions() {
        TransactionLedger ledger = new TransactionLedger();

        ledger.add("deposit",  "alice", "alice", 100.0);
        ledger.add("withdraw", "alice", "alice", 50.0);
        ledger.add("transfer", "bob",   "alice", 200.0);

        var allTransactions = ledger.getAllTransactions();
        assertEquals(3, allTransactions.size());
        assertTrue(allTransactions.stream().anyMatch(t ->
                t.getType().equals("deposit") && t.getFrom().equals("alice") && t.getTo().equals("alice") && t.getAmount() == 100.0));
        assertTrue(allTransactions.stream().anyMatch(t ->
                t.getType().equals("withdraw") && t.getFrom().equals("alice") && t.getTo().equals("alice") && t.getAmount() == 50.0));
        assertTrue(allTransactions.stream().anyMatch(t ->
                t.getType().equals("transfer") && t.getFrom().equals("bob") && t.getTo().equals("alice") && t.getAmount() == 200.0));

    }
}
