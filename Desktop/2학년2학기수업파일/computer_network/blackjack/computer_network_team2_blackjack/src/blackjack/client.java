package blackjack;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class client {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;

    public client() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);
            System.out.println("Blackjack 서버에 연결되었습니다.");
        } catch (IOException e) {
            System.err.println("서버 연결 실패: " + e.getMessage());
            System.exit(1);
        }
    }

    public void start() {
        try {
            while (true) {
                String serverMessage = in.readLine();

                if (serverMessage == null) {
                    System.out.println("서버 연결이 종료되었습니다.");
                    break;
                }

                System.out.println(serverMessage);

                if (serverMessage.contains("입력하세요.") ||
                        serverMessage.contains("추가 카드: 1, 종료: 0") ||
                        serverMessage.contains("게임을 재시작하시겠습니까")) {
                    String userInput = getUserInput();
                    out.println(userInput);

                    if ("0".equals(userInput) && serverMessage.contains("게임을 재시작하시겠습니까")) {
                        break; // 게임 종료
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("통신 오류: " + e.getMessage());
        } finally {
            close();
        }
    }

    private String getUserInput() {
        System.out.print("입력: ");
        return scanner.nextLine();
    }

    private void close() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
            if (scanner != null) scanner.close();
            System.out.println("게임을 종료합니다. 감사합니다.");
        } catch (IOException e) {
            System.err.println("리소스 해제 중 오류: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        client client = new client();
        client.start();
    }
}
