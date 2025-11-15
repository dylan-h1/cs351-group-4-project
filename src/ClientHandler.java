import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    Socket socket;
    BankServer server;
    String username;
    PrintWriter out;
    BufferedReader in;

    ClientHandler(Socket socket, BankServer server) {
        this.socket = socket;
        this.server = server;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Client connected");
        try {
            String input;
            {
                while (true) {
                    if ((input = in.readLine()) != null) {
                        System.out.println("Received command: " + input);
                        handleCommand(input);
                    } else {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (username != null && server.onlineUsers != null) {
                server.onlineUsers.remove(username);
            }
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void handleCommand(String cmd) {
        String[] commandArgs = cmd.split(" ");
        String command = commandArgs[0].toUpperCase();

        try{
            switch (command) {
                case "CREATE_ACCOUNT" -> handleCreateAccount(commandArgs);
                case "LOGIN" -> handleLogin(commandArgs);
                case "VIEW_BALANCE" -> handleViewBalance(commandArgs);
                case "DEPOSIT" -> handleDeposit(commandArgs);
                case "WITHDRAW" -> handleWithdraw(commandArgs);
                case "TRANSFER" -> handleTransfer(commandArgs);
                case "LOGOUT" -> handleLogout(commandArgs);
                default -> sendMessage("ERROR: Unknown command");
            }
        } catch (NumberFormatException e) {
            sendMessage("ERROR: Invalid number format");
        } catch (IllegalStateException e) {
            sendMessage("ERROR: " + e.getMessage());
        } catch (Exception e) {
            sendMessage("ERROR: An unexpected error occurred");
            e.printStackTrace();
        }
    }

    void sendMessage(String msg) {
        out.println(msg);
    }

    //added below as methods so the handleCommand method doesnt get messy
    //example methods added to show implementation
    public void requireLogin() {
        if (username == null) {
            throw new IllegalStateException("User must be logged in to perform this action");
        }
    }

    private void handleCreateAccount(String[] commandArgs) {
    }

    private void handleLogin(String[] commandArgs) {
        String username = commandArgs[1];
        String password = commandArgs[2];

        Account account = server.accounts.get(username);
        if(account == null) {
            sendMessage("ERROR: Account does not exist");
            return;
        }
        if(!account.getPassword().equals(password)) {
            sendMessage("ERROR: Incorrect password");
            return;
        }

        this.username = username;
        server.onlineUsers.put(username, this);
        sendMessage("SUCCESS: Logged in");
    }

    private void handleViewBalance(String[] commandArgs) {
    }

    private void handleDeposit(String[] commandArgs) {
        requireLogin();

        double amount = Double.parseDouble(commandArgs[1]);
        if (amount <= 0) {
            sendMessage("ERROR: Deposit amount must be greater than 0");
            return;
        }
        Account account = server.accounts.get(username);
        account.deposit(amount);

        server.transactionLedger.add(
                "DEPOSIT",
                "BANK",
                username,
                amount
        );
        server.notifyUser(username, "DEPOSIT: £" + amount + " deposited to your account");
        sendMessage("SUCCESS: Deposited £" + amount);
    }

    private void handleWithdraw(String[] commandArgs) {
    }

    private void handleTransfer(String[] commandArgs) {
    }

    private void handleLogout(String[] commandArgs) {

    }


}