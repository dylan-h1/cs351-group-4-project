import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class BankClient {

    Socket socket;
    PrintWriter out;
    BufferedReader in;
    private boolean loggedIn = false;

    BankClient(String address, int port) throws IOException {
        socket = new Socket(address, port);
    }

    public void connect() throws IOException {
        Scanner scanner = new Scanner(System.in);
        out = new PrintWriter(socket.getOutputStream(), true);
        String msg = "";
        try{
            while (!msg.equals("exit")) {
                System.out.println("Type in message:");
                msg = scanner.nextLine();
                out.println(msg);
            }
        } finally {
            close();
        }
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean getLoggedIn() {
        return loggedIn;
    }

    void send(String command) {
        if (out != null) {
            out.println(command);
        }
    } //I reckon we could do send and recieve in the one method

    public String receive(String command) {
        try {
            send(command);

            String response = in.readLine();

            if (response == null) {
                return "Server closed unexpectedly";
            }
            return response;
        } catch (Exception e) {
            System.out.println("Problem communicating with the server: " + e.getMessage());
            close();
            return "Lost server connection";
        }
    }

    void listenForUpdates() {
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
            BankClient client = new BankClient("127.0.0.1", 9000);
            client.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}