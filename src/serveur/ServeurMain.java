package serveur;

import java.io.IOException;
import java.net.ServerSocket;

public class ServeurMain {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            while (true)
                new Thread(new ClientHandler(serverSocket.accept())).start();
        } catch (IOException e) {}
    }
}