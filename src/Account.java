import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

public class Account implements Serializable {

    String username;
    String password;
    double balance;
    ReentrantLock lock = new ReentrantLock();

    public Account(String username, String password, double balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
    }

    public void deposit(double amount) {
        lock.lock();
        try {
            balance = balance + amount;
        }
        finally {
            lock.unlock();
        }
    }

    public void withdraw(double amount) {
        lock.lock();
        try {
            if (balance == 0){
                System.out.println("There is nothing in this account to withdraw");
            }
            else if (amount <= balance) {
                balance = balance - amount;
            } else {
                System.out.println("Insufficient funds to withdraw");
            }
        } finally {
            lock.unlock();
        }
    }

    public void transferTo(Account target, double amount) {
        Account first = this;
        Account second = target;
        if ((first.username).compareTo(second.username) < 0) {
            first = target;
            second = this;
        }

        Account firstLock = first; //using two locks to prevent deadlocking
        Account secondLock = second;

        firstLock.lock.lock();
        try {
            secondLock.lock.lock();
            try {
                if (balance == 0) {
                    System.out.println("There is nothing in this account to withdraw");
                    return;
                }
                if (amount <= balance) {
                    this.withdraw(amount);
                    target.deposit(amount);
                } else {
                    System.out.println("Insufficient funds to transfer");
                }

            } finally {
                secondLock.lock.unlock();
            }

        } finally{
            firstLock.lock.unlock();
        }

    }

    public double getBalance() {
        return balance;
    }

    //added getter method for password
    //used in BankServer class for authentication
    public String getPassword() { return password; }
}