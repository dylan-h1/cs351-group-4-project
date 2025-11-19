import java.io.IOException;
import java.util.Scanner;

import static constants.Command.*;

public class ClientMenu {

    BankClient client;
    Scanner scanner;

    public ClientMenu(String address, int port) throws IOException {
        this.client = new BankClient(address, port);
        this.scanner = new Scanner(System.in);
    }

    public void run(){
        showLoginMenu();
    }

    void showLoginMenu() {
        while (true) {
            System.out.println("\n===== CLIENT LOGIN MENU =====");
            System.out.println("1. Login");
            System.out.println("2. Sign up");
            System.out.println("3. Exit");
            System.out.print("Please enter 1, 2 or 3: ");

            if (scanner.hasNextLine()) {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        handleLogin();
                        if (client.getLoggedIn()) {
                            showMainMenu();
                            return;
                        }
                        break;
                    case 2:
                        handleSignup();
                        break;
                    case 3:
                        System.out.println("Exiting...");
                        client.close();
                        return;
                    default:
                        System.out.println("Invalid choice, try again");
                }
            } else {
                System.out.println("Invalid input, try again please enter an integer");
                scanner.next();
            }
        }
    }

    public void handleLogin() {
        System.out.println("\n===== LOGIN =====");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        String loginCommand = "LOGIN" + " " + username + " " + password;
        String response = client.sendCommand(loginCommand);
        System.out.println(response);

        if(response != null && response.contains("SUCCESS")) {
            client.setLoggedIn(true);
            System.out.println("Login successful");
        } else{
            System.out.println("Login failed");
        }
    }

    public void handleSignup() {
        System.out.println("\n===== SIGN UP =====");
        System.out.print("Enter a username: ");
        String username = scanner.nextLine();
        System.out.print("Enter a password: ");
        String password = scanner.nextLine();

        String signupCommand = CREATE_ACCOUNT.getText() + " " + username + " " + password;
        String response = client.sendCommand(signupCommand);
        System.out.println(response);

        if(response != null && response.contains("SUCCESS")) {
            client.setLoggedIn(true);
            System.out.println("Sign-up successful");
        } else{
            System.out.println("Sign-up failed");
        }
    }

    void showMainMenu() {
        int choice;
        while (client.getLoggedIn()) {

            System.out.println("\n===== ACCOUNT MENU =====");
            System.out.println("1. Current Balance");
            System.out.println("2. Deposit Funds");
            System.out.println("3. Withdraw Funds");
            System.out.println("4. Transfer Funds");
            System.out.println("5. View Transactions");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");

            if (scanner.hasNextLine()) {
                choice = Integer.parseInt(scanner.nextLine());
                handleUserChoice(choice);
            } else {
                System.out.println("Invalid input, try again");
                scanner.nextLine();
            }
        }
        showLoginMenu();
    }

    void handleUserChoice(int choice) {
        String command = "";
        String response = "";

        switch (choice) {
            case 1:
                command = BALANCE.getText();

                response = client.sendCommand(command);
                System.out.println(response);
                break;
            case 2:
                double depositAmount = readDouble("Please enter the amount you want to deposit: ");
                command = DEPOSIT.getText() + " " + depositAmount;
                response = client.sendCommand(command);
                System.out.println(response);
                break;
            case 3:
                double withdrawAmount = readDouble("Please enter the amount you want to withdraw: ");
                command = WITHDRAW.getText() + " " + withdrawAmount;
                response = client.sendCommand(command);
                System.out.println(response);
                break;
            case 4:
                System.out.print("Enter the username of the account you want to transfer: ");
                String username = scanner.nextLine();
                double transferAmount = readDouble("Enter the amount you want to send: ");
                command = TRANSFER.getText() + " " + username + " " + transferAmount;
                response = client.sendCommand(command);
                System.out.println(response);
                break;
            case 5:
                command = VIEW_TRANSACTIONS.getText();
                response = client.sendCommand(command);
                System.out.println(response);
                break;
            case 6:
                command = LOGOUT.getText();
                response = client.sendCommand(command);
                System.out.println(response);
                client.setLoggedIn(false);
                break;
            default:
                System.out.println("Invalid input, try again");
        }
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