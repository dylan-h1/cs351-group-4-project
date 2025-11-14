import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class BankServerLogicTest {
    private BankServer bankServer;

    @AfterEach
    public void tearDown() {
        if (bankServer != null) {
            bankServer.stop();
        }
    }

    @Test
    public void AccountTransfer() throws Exception {
        bankServer = new BankServer(0);

        Account alice = new Account("alice", "pass", 1000.0);
        Account bob = new Account("bob", "word", 1000.0);

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
}
