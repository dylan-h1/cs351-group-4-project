import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    Socket socket;
    BankServer server;
    String username;

    ClientHandler(Socket socket, BankServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        String msg = "";
        try {
            Scanner scanner = new Scanner(socket.getInputStream());
            while (scanner.hasNextLine()) {
                msg = scanner.nextLine();
                System.out.println(msg);
                if(msg.equals("exit")){
                    System.out.println("Client disconnected");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error in ClientHandler: " + e.getMessage());
        }
    }

    void handleCommand(String cmd) {
    }

    void sendMessage(String msg) {
    }
}