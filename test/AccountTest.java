import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {
    private Account alice;
    private Account bob;

    @BeforeEach
    public void setUp() {
        alice = new Account("alice", "pass", 1000.0);
        bob = new Account("bob", "word", 1000.0);
    }

   @Test
    public void Deposit() {
        alice.deposit(500.0);
        assertEquals(1500.0, alice.getBalance(), 0.001);
    }

    @Test
    public void Withdraw() {
        alice.withdraw(300.0);
        assertEquals(700.0, alice.getBalance(), 0.001);

        alice.withdraw(800.0);
        assertEquals(700.0, alice.getBalance(), 0.001);
    }

    @Test
    public void TransferTo() {
        alice.transferTo(bob, 400.0);
        assertEquals(600.0, alice.getBalance(), 0.001);
        assertEquals(1400.0, bob.getBalance(), 0.001);

        alice.transferTo(bob, 700.0);
        assertEquals(600.0, alice.getBalance(), 0.001);
        assertEquals(1400.0, bob.getBalance(), 0.001);
    }

    @Test
    public void TransferToDeadlockPrevention() {
        Thread t1 = new Thread(() -> {
            alice.transferTo(bob, 100.0);
        });

        Thread t2 = new Thread(() -> {
            bob.transferTo(alice, 50.0);
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            fail("Threads interrupted");
        }

        assertEquals((1000.0 - 100.0 + 50.0), alice.getBalance(), 0.001);
        assertEquals((1000.0 + 100.0 - 50.0), bob.getBalance(), 0.001);
    }

    @Test
    public void GetBalance() {
        assertEquals(1000.0, alice.getBalance(), 0.001);
        assertEquals((1000.0), bob.getBalance(), 0.001);
    }

    @Test
    public void WithdrawFromEmptyAccount() {
        Account emptyAccount = new Account("charlie", "1234", 0.0);
        emptyAccount.withdraw(100.0);
        assertEquals(0.0, emptyAccount.getBalance(), 0.001);
    }


    //fails due to Account class not handling negative deposits
    //deducts the negative amount from balance
    @Test
    public void DepositNegativeAmount() {
        alice.deposit(-100.0);
        assertEquals(1000.0, alice.getBalance(), 0.001);
    }

    @Test
    public void TransferToSelf() {
        alice.transferTo(alice, 100.0);
        assertEquals(1000.0, alice.getBalance(), 0.001);
    }

    @Test
    public void ConcurrentDeposits() {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                alice.deposit(10.0);
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                alice.deposit(20.0);
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            fail("Threads interrupted");
        }

        assertEquals(1000.0 + (100 * 10.0) + (100 * 20.0), alice.getBalance(), 0.001);
    }
}
