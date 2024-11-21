package blackjack;

import java.io.*;
import java.net.*;

public class Server {
    private static final int PORT = 12345; // 서버 포트 번호

    public static void main(String[] args) {
        System.out.println("Blackjack 서버가 시작되었습니다.");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                System.out.println("클라이언트를 기다리는 중...");
                Socket clientSocket = serverSocket.accept(); // 클라이언트 연결 대기
                System.out.println("클라이언트가 연결되었습니다!");

                // 새로운 스레드에서 클라이언트 처리
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("서버 오류: " + e.getMessage());
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            out.println("Blackjack 게임에 오신 것을 환영합니다!");

            boolean keepPlaying = true;
            while (keepPlaying) {
                // Play 객체 생성 및 실행
            	Play play = new Play(out, in); // PrintWriter와 BufferedReader 전달
            	play.play();

                // 게임 종료 후, 다시 게임할지 묻기
                out.println("다시 게임을 시작하시겠습니까? (yes/no)");
                String response = in.readLine();

                if (response == null || response.equalsIgnoreCase("no")) {
                    keepPlaying = false;
                    out.println("게임을 종료합니다. 이용해 주셔서 감사합니다!");
                } else if (response.equalsIgnoreCase("yes")) {
                    out.println("새로운 게임을 시작합니다...");
                } else {
                    out.println("잘못된 입력입니다. 연결을 종료합니다.");
                    keepPlaying = false;
                }
            }
        } catch (IOException e) {
            System.err.println("클라이언트 처리 중 오류: " + e.getMessage());
        } finally {
            try {
                clientSocket.close(); // 클라이언트 소켓 닫기
            } catch (IOException e) {
                System.err.println("소켓 닫는 중 오류: " + e.getMessage());
            }
        }
    }
}

