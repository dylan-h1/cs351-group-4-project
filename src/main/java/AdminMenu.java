import java.util.List;
import java.util.Scanner;
import java.util.Set;

import static constants.Command.ADD;
import static constants.Command.REMOVE;

public class AdminMenu {

    private final BankServer bankServer;
    private final Scanner scanner = new Scanner(System.in);
    private final List<String> actions = List.of("add", "remove");

    public AdminMenu(BankServer bankServer) {
        this.bankServer = bankServer;
    }

    public void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1. View online users");
            System.out.println("2. View all transactions");
            System.out.println("3. Adjust user balance");
            System.out.println("4. Transfer between users");
            System.out.println("5. Change interest rate");
            System.out.println("6. Change interest period");
            System.out.println("7. View server logs");
            System.out.println("8. Shutdown server");
            System.out.print("\nSelect option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    viewOnlineUsers();
                    break;
                case "2":
                    viewAllTransactions();
                    break;
                case "3":
                    adjustUserBalance();
                    break;
                case "4":
                    transferBetweenUsers();
                    break;
                case "5":
                    changeInterestRate();
                    break;
                case "6":
                    changeInterestPeriod();
                    break;
                case "7":
                    showLogs();
                    break;
                case "8":
                    bankServer.stop();
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number 1-7");
            }
        }
    }

    public void viewOnlineUsers() {
        if (bankServer.onlineUsers != null) {
            Set<String> users = bankServer.onlineUsers.keySet();
            for (String user : users) {
                System.out.println(user);
            }
        } else {
            System.out.println("No users currently online.");
        }
    }

    private void viewAllTransactions() {
        if (bankServer.transactionLedger != null) {
            List<Transaction> transactions = bankServer.transactionLedger.getAllTransactions();
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        } else {
            System.out.println("No transactions available to view.");
        }
    }

    private void adjustUserBalance() {
        System.out.print("Enter username you wish to adjust balance for: ");
        String user = scanner.nextLine();

        Account account = bankServer.accounts.get(user);
        if (account == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.print("Do you wish to add to or subtract from balance? (add/remove) ");
        String action = scanner.nextLine();
        if (!actions.contains(action)) {
            System.out.println("Please enter either 'add' or 'remove'.");
            return;
        }

        double amount = readDouble("Enter amount: ");

        if (action.equals("add")) {
            account.deposit(amount);
            bankServer.transactionLedger.addNewTransaction(ADD.getText(), "Admin", user, amount);
            System.out.println("Added £" + amount + " to " + user + "'s account");
            bankServer.notifyUser(account.username,
                    "[ADMIN] £" + String.format("%.2f", amount) +
                            " added to your account by admin. New balance: £" + String.format("%.2f", account.getBalance()));
        } else {
            boolean success = account.withdraw(amount);
            if (success) {
                bankServer.transactionLedger.addNewTransaction(REMOVE.getText(), "Admin", user, amount);
                System.out.println("Removed £" + amount + " from " + user + "'s account");
                bankServer.notifyUser(account.username,
                        "[ADMIN] £" + String.format("%.2f", amount) +
                                " removed from your account by admin. New balance: £" + String.format("%.2f", account.getBalance()));
            }
        }
    }

    private void transferBetweenUsers() {
        System.out.print("Enter username you wish to transfer money from: ");
        String fromUser = scanner.nextLine();

        Account fromAccount = bankServer.accounts.get(fromUser);
        if (fromAccount == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.print("Enter username you wish to transfer money to: ");
        String toUser = scanner.nextLine();

        Account toAccount = bankServer.accounts.get(toUser);
        if (toAccount == null) {
            System.out.println("User not found.");
            return;
        }

        double amount = readDouble("Enter amount you wish to transfer from user '" + fromUser + "': ");

        boolean success = fromAccount.transferTo(toAccount, amount);
        if (success) {
            bankServer.transactionLedger.addNewTransaction(ADD.getText(), fromUser, toUser, amount);
            System.out.println("Transferred £" + amount + " to " + toUser + "'s account");
            bankServer.notifyUser(toAccount.username,
                    "[ADMIN] £" + String.format("%.2f", amount) +
                            " sent to you from " + fromAccount.username + " by admin. New balance: £" + String.format("%.2f", toAccount.getBalance()));
        }
    }

    private void changeInterestRate() {
        bankServer.interestRate = readDouble("Enter new interest rate (current is " + bankServer.interestRate + "): ");
        bankServer.interestSchedule();
    }

    private void changeInterestPeriod() {
        bankServer.interestPeriod = (long) readDouble("Enter new interest period in seconds (current is " + bankServer.interestPeriod + "): ");
        bankServer.interestSchedule();
    }

    private void showLogs() {
        System.out.println("\n===== SERVER LOGS =====");

        if (!bankServer.serverLogs.isEmpty()) {
            for (String entry : bankServer.serverLogs) {
                System.out.println(entry);
            }
        }

        System.out.println("=======================\n");
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            try {
                double value = Double.parseDouble(input);
                if (value < 0) {
                    System.out.println("Amount must be positive.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount — enter a number.");
            }
        }
    }
}
