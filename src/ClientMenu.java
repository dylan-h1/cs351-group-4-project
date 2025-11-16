import java.io.IOException;
import java.util.Scanner;

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

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();

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
        System.out.println("Username: ");
        String username = scanner.nextLine();
        System.out.println("Password: ");
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

    void handleSignup() {
        System.out.println("\n===== SIGN UP =====");
        System.out.println("Enter new username: ");
        String username = scanner.nextLine();
        System.out.println("Enter new username password: ");
        String password = scanner.nextLine();

        String signupCommand = "CREATE_ACCOUNT" + " " + username + " " + password;
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
            System.out.println("Enter your choice: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
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
                command = "BALANCE";

                response = client.sendCommand(command);
                System.out.println(response);
                break;
            case 2:
                System.out.println("Please enter the amount you want to deposit: ");
                if (scanner.hasNextDouble()) {
                    double amount = scanner.nextDouble();
                    command = "DEPOSIT" + amount;

                    response = client.sendCommand(command);
                    System.out.println(response);
                }
                System.out.println("Invalid amount");
                scanner.nextLine();
                break;
            case 3:
                System.out.println("Please enter the amount you want to withdraw: ");
                if (scanner.hasNextDouble()) {
                    double amount = scanner.nextDouble();

                    command = "WITHDRAW" + amount;
                    response = client.sendCommand(command);
                    System.out.println(response);

                }
                System.out.println("Invalid amount");
                scanner.nextLine();
                break;
            case 4:
                System.out.println("Enter the username of the account you want to transfer: ");
                String username = scanner.nextLine();
                System.out.println("Enter the amount you want to send: ");
                double amount = scanner.nextDouble();

                command = "TRANSFER" + username + " " + amount;
                response = client.sendCommand(command);
                System.out.println(response);
                break;
            case 5:
                command = "VIEW TRANSACTIONS";
                response = client.sendCommand(command);
                System.out.println(response);
                break;
            case 6:
                command = "LOGOUT";
                response = client.sendCommand(command);
                System.out.println(response);
                client.setLoggedIn(false);
                break;
            default:
                System.out.println("Invalid input, try again");
        }
    }

    public static void main(String[] args) {
        try{
            ClientMenu menu = new ClientMenu("127.0.0.1", 9000);
            menu.run();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}