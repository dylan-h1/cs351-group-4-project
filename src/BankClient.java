import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class BankClient {

    Socket socket;
    PrintWriter out;
    BufferedReader in;

    BankClient(String address, int port) throws IOException {
        socket = new Socket(address, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void connect() throws IOException {
        Thread reader = new Thread(() -> {
            try{
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Server: " + line);
                }
            } catch (IOException e) {
                System.out.println("Error reading from server:");
            }
        });
        reader.start();

        Scanner scanner = new Scanner(System.in);
        String msg = "";
            while (!msg.equals("exit")) {
                System.out.println("Type in message:");
                msg = scanner.nextLine();
                out.println(msg);
            }
        close();
    }

    void send(String command) {

    }

    void receive() {
    }

    void listenForUpdates() {
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error closing client socket:");
        }
    }

    public static void main(String[] args) {
        try {
            BankClient client = new BankClient("127.0.0.1", 9000);
            client.connect();
        } catch (IOException e) {
            System.out.println("Error starting client:");
        }
    }
}