import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class BankClient {

    Socket socket;
    PrintWriter out;
    BufferedReader in;

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

    void send(String command) {
    }

    void receive() {
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