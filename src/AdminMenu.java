import java.util.List;
import java.util.Scanner;
import java.util.Set;

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
            System.out.println("7. Shutdown server");
            System.out.print("Select option: ");

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
                    bankServer.stop();
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number 1-7");
            }
        }
    }

    private void viewOnlineUsers() {
        Set<String> users = bankServer.onlineUsers.keySet();
        for (String user : users) {
            System.out.println(user);
        }
    }

    private void viewAllTransactions() {
        List<Transaction> transactions = bankServer.transactionLedger.getAllTransactions();
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    // Need to add full input validation, discuss the standard of how we will do this across application
    // Maybe worth adding an enum for type?
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

        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        if (amount <= 0) {
            System.out.println("Amount must be greater than 0.");
        }

        if (action.equals("add")) {
            account.deposit(amount);
            bankServer.transactionLedger.add("ADD", "Admin", user, amount);
            System.out.println("Added £" + amount + " to " + user + "'s account");
        } else {
            account.withdraw(amount);
            bankServer.transactionLedger.add("REMOVE", "Admin", user, amount);
            System.out.println("Removed £" + amount + " from " + user + "'s account");
        }
    }

    private void transferBetweenUsers() {
        // Need to finish
    }

    // Input validation required
    private void changeInterestRate() {
        System.out.print("Enter new interest rate (e.g. 5.5): ");
        bankServer.interestRate = Double.parseDouble(scanner.nextLine());
    }

    // Input validation required
    private void changeInterestPeriod() {
        System.out.print("Enter new interest period in seconds (e.g. 5): ");
        bankServer.interestPeriod = Long.parseLong(scanner.nextLine());
    }
}
