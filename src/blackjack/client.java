package blackjack;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class client {
    private static final String SERVER_HOST = "127.0.0.1"; // Server IP
    private static final int SERVER_PORT = 12345; // Server port
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;
    private String clientId; // client ID

    public client() {
        try {
            // Connect to the server
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);
            System.out.println("Connected to the Blackjack server.");

            // client ID received
            clientId = in.readLine();
            System.out.println("Assigned Client ID: " + clientId);
        } catch (IOException e) {
            System.err.println("Unable to connect to the server: " + e.getMessage());
            System.exit(1);
        }
    }

    public void start() {
        try {
            while (true) {
                // Read server's message
                String serverMessage = in.readLine();

                if (serverMessage == null) {
                    System.out.println("Server connection has been closed.");
                    break;
                }

                System.out.println(serverMessage); // Display server message

                /* 1. 게임 시작 전, 게임에 참여할지 선택.
                2. 게임 시작 선택시, 얼마를 배팅할지 선택.
                3. 게임 진행 중, stand, hold를 선택.
               */
                if (serverMessage.startsWith("Input:")) {
                    String userInput = scanner.nextLine(); // Read user input
                    String formattedInput = clientId + ":" + userInput; //ID와 함께 입력값을 서버로 전송.
                    out.println(formattedInput); // Send formatted input to server

                    // If user chooses to quit, exit the client
                    if (userInput.equalsIgnoreCase("quit game")) {
                        System.out.println("You chose to quit the game. Disconnecting...");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Communication error: " + e.getMessage());
        } finally {
            close();
        }
    }

    private void close() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
            System.out.println("Client has been closed.");
        } catch (IOException e) {
            System.err.println("Error while releasing resources: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        client client = new client();
        client.start();
    }
}