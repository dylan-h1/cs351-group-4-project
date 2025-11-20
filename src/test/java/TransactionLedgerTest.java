import org.junit.jupiter.api.Test;

import static constants.Command.*;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionLedgerTest {
    @Test
    public void addAndQueryTransactions() {
        TransactionLedger ledger = new TransactionLedger();

        ledger.addNewTransaction(DEPOSIT.getText(), "alice", "alice", 100.0);
        ledger.addNewTransaction(WITHDRAW.getText(), "alice", "alice", 50.0);
        ledger.addNewTransaction(TRANSFER.getText(), "bob", "alice", 200.0);


        var aliceTransactions = ledger.getUserTransactions("alice");
        assertEquals(3, aliceTransactions.size());
        assertTrue(aliceTransactions.stream().anyMatch(t ->
                t.getType().equals(DEPOSIT.getText()) && t.getFrom().equals("alice") && t.getTo().equals("alice") && t.getAmount() == 100.0));
        assertTrue(aliceTransactions.stream().anyMatch(t ->
                t.getType().equals(WITHDRAW.getText()) && t.getFrom().equals("alice") && t.getTo().equals("alice") && t.getAmount() == 50.0));
        assertTrue(aliceTransactions.stream().anyMatch(t ->
                t.getType().equals(TRANSFER.getText()) && t.getFrom().equals("bob") && t.getTo().equals("alice") && t.getAmount() == 200.0));
    }

    @Test
    public void getAllTransactions() {
        TransactionLedger ledger = new TransactionLedger();

        ledger.addNewTransaction(DEPOSIT.getText(),  "alice", "alice", 100.0);
        ledger.addNewTransaction(WITHDRAW.getText(), "alice", "alice", 50.0);
        ledger.addNewTransaction(TRANSFER.getText(), "bob",   "alice", 200.0);

        var allTransactions = ledger.getAllTransactions();
        assertEquals(3, allTransactions.size());
        assertTrue(allTransactions.stream().anyMatch(t ->
                t.getType().equals(DEPOSIT.getText()) && t.getFrom().equals("alice") && t.getTo().equals("alice") && t.getAmount() == 100.0));
        assertTrue(allTransactions.stream().anyMatch(t ->
                t.getType().equals(WITHDRAW.getText()) && t.getFrom().equals("alice") && t.getTo().equals("alice") && t.getAmount() == 50.0));
        assertTrue(allTransactions.stream().anyMatch(t ->
                t.getType().equals(TRANSFER.getText()) && t.getFrom().equals("bob") && t.getTo().equals("alice") && t.getAmount() == 200.0));

    }
}
