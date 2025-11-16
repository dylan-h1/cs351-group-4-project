package main;

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

    public BankClient(String address, int port) throws IOException {
        socket = new Socket(address, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //startListener(); -> commented out as it doesnt work fully, but needed for async updates
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean getLoggedIn() {
        return loggedIn;
    }

    public String sendCommand(String command) {
        try {
            out.println(command);
            return in.readLine();
        } catch (IOException e) {
            return "Error communicating with server: " + e.getMessage();
        }

    }
    //i dont think we need recieve(), as send command handles the response

    public void startListener() {
        Thread thread = new Thread(this::listenForUpdates);
        thread.start();
    }

    void listenForUpdates() {
            try {
                String update;
                while ((update = in.readLine()) != null) {
                    System.out.println(update);
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