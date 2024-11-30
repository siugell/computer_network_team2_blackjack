package blackjack;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 2;
    private static CountDownLatch latch;
    private static AtomicInteger clientCount = new AtomicInteger(0);
    private static List<ClientHandler> clientHandlers = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Blackjack server init.");

        latch = new CountDownLatch(MAX_CLIENTS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (clientCount.get() < MAX_CLIENTS) {
                System.out.println("waiting client...");
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, latch);

                if (clientCount.incrementAndGet() <= MAX_CLIENTS) {
                    System.out.println("connect client!");
                    clientHandlers.add(clientHandler);
                    new Thread(clientHandler).start();
                } else {
                    System.out.println("no more client.");
                    clientSocket.close();
                }
            }

            latch.await();

            Game game = new Game(clientHandlers);
            game.start();

        } catch (IOException e) {
            System.err.println("server error: " + e.getMessage());
        }
    }
}
