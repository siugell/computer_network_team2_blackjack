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

    public client() {
        try {
            // 서버와 연결
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);
            System.out.println("Connected to the Blackjack server.");
        } catch (IOException e) {
            System.err.println("Unable to connect to the server: " + e.getMessage());
            System.exit(1);
        }
    }

    public void start() {
        try {
            while (true) {
                // 서버 메시지 읽기
                String serverMessage = in.readLine();

                // 서버 연결이 종료된 경우 처리
                if (serverMessage == null) {
                    System.out.println("Server connection has been closed.");
                    break;
                }

                // 서버 메시지 출력
                System.out.println(serverMessage);

                // 클라이언트의 입력이 필요한 메시지인 경우
                if (serverMessage.contains("추가 카드: 1, 종료: 0")) {
                    String userInput = getUserInput(); // 사용자 입력
                    out.println(userInput); // 서버로 입력 전송
                } else if(serverMessage.contains("입력하세요.")){
                	String userInput = getUserInput(); // 사용자 입력
                    out.println(userInput); // 서버로 입력 전송
                } else if (serverMessage.startsWith("다시 게임을 시작하시겠습니까")) {
                    String userInput = getUserInput(); // 사용자 입력
                    out.println(userInput); // 서버로 입력 전송
                    if (userInput.equalsIgnoreCase("no")) {
                        break; // 게임 종료
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Communication error: " + e.getMessage());
        } finally {
            close();
        }
    }

    // 사용자 입력 처리 메서드
    private String getUserInput() {
        System.out.print("입력: ");
        return scanner.nextLine();
    }

    // 클라이언트 종료 처리
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
