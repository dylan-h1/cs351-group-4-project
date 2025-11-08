import java.util.concurrent.locks.ReentrantLock;

public class Account {

    String username;
    String password;
    double balance;
    ReentrantLock lock;

    void deposit(double amount) {
    }

    boolean withdraw(double amount) {
        return false;
    }

    boolean transferTo(Account target, double amount) {
        return false;
    }

    double getBalance() {
        return 0;
    }
}