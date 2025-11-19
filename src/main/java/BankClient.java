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
        out.println(command);

        while (lastResponse == null) {
            try {
                Thread.sleep(10);
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
                } else {
                    lastResponse = update;
                }
            }
        } catch (IOException e) {
            System.out.println("Error listening for updates:");
        }

    }

    void close() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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