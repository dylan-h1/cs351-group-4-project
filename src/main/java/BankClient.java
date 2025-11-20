import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BankClient {

    Socket socket;
    PrintWriter out;
    BufferedReader in;
    private boolean loggedIn = false;
    private volatile String lastResponse = null;

    public BankClient(String address, int port) throws IOException {
        socket = new Socket(address, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        startListener();
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean getLoggedIn() {
        return loggedIn;
    }

    public String sendCommand(String command) {
        lastResponse = null;
        try {
            if (socket.isClosed() || !socket.isConnected()) {
                System.out.println("\nServer disconnected, exiting");
                close();
                System.exit(1);
            }
            out.println(command);
        } catch (Exception e) {
            System.out.println("\nServer disconnected, exiting");
            close();
            System.exit(1);
        }

        while (lastResponse == null) {
            try {
                Thread.sleep(10);
                if (socket.isClosed() || !socket.isConnected()) {
                    System.out.println("\nServer disconnected, exiting");
                    close();
                    System.exit(1);
                }
            } catch (InterruptedException ignored) {
            }
        }

        return lastResponse;
    }

    public void startListener() {
        Thread thread = new Thread(this::listenForUpdates);
        thread.setDaemon(true);
        thread.start();
    }

    void listenForUpdates() {
        try {
            String update;
            while ((update = in.readLine()) != null) {
                if (update.startsWith("[INTEREST]") || update.startsWith("[TRANSFER]") || update.startsWith("[ADMIN]")) {
                    // showing interest message and giving user choice to enter option in clean way
                    System.out.println("\n\n" + update + "\n");
                    System.out.print("Enter your choice: ");
                } else if (update.equals("SERVER_SHUTDOWN")) {
                    System.out.println("\nServer is shutting down, exiting");
                    close();
                    System.exit(0);
                } else {
                    lastResponse = update;
                }
            }
        } catch (IOException e) {
            System.out.println("\nServer disconnected, exiting");
            close();
            System.exit(1);
        }

    }

    void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
             ClientMenu menu = new ClientMenu("127.0.0.1", 9000);
                menu.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}