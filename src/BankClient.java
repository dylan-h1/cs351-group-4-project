import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BankClient {

    Socket socket;
    PrintWriter out;
    BufferedReader in;

    public BankClient(String address, int port) throws IOException {
        socket = new Socket(address, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        listenForUpdates();
    }

    //unused but will be used in ClientMenu
    //send command to ClientHandler and return response
    public String send(String command) throws IOException {
        if (socket == null || socket.isClosed()) {
            throw new IOException("Socket is not connected");
        }
        out.println(command);
        return in.readLine();
    }

    private void listenForUpdates() {
        Thread listenerThread = new Thread(() -> {
            try {
                String update;
                while ((update = in.readLine()) != null) {
                    System.out.println("Update from server: " + update);
                }
            } catch (IOException e) {
                System.out.println("Error listening for updates:");
            }
        });
        listenerThread.start();
    }

    //unused but will be used in ClientMenu
    //use as a close method when done with client
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error closing client socket:");
        }
    }
}