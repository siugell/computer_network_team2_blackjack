package blackjack;

import java.io.*;
import java.net.*;
import java.util.concurrent.CountDownLatch;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Gamer gamer; // Gamer 객체로 유저 정보 관리
    private CountDownLatch latch;

    public ClientHandler(Socket clientSocket, CountDownLatch latch) {
        this.clientSocket = clientSocket;
        this.latch = latch;
    }

    public Gamer getGamer() {
        return gamer;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String receiveInput() throws IOException {
        return in.readLine();
    }

    // 소켓과 스트림을 닫는 메서드 추가
    public void close() {
        try {
            if (clientSocket != null) clientSocket.close();
            if (in != null) in.close();
            if (out != null) out.close();
            System.out.println(gamer.getName() + "의 연결이 종료되었습니다.");
        } catch (IOException e) {
            System.err.println("소켓 닫는 중 오류: " + e.getMessage());
        }
    }

    public void startGame() {
        synchronized (this) {
            this.notify();
        }
    }

    @Override
    public void run() {
        try {
            // 클라이언트와의 입출력 스트림 설정
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Blackjack 게임에 오신 것을 환영합니다!");
            out.println("게임을 시작합니다. 이름을 입력하세요.");
            String username = in.readLine();
            while (username == null || username.trim().isEmpty()) {
                out.println("올바른 이름을 입력하세요.");
                username = in.readLine(); // 다시 입력 요청
            }

            out.println("소지금을 입력하세요.");
            String balanceInput = in.readLine();
            while (balanceInput == null || !balanceInput.matches("\\d+")) { // 숫자가 아닐 경우 다시 요청
                out.println("올바른 숫자를 입력하세요.");
                balanceInput = in.readLine();
            }
            int balance = Integer.parseInt(balanceInput);
            gamer = new Gamer(username, balance); // Gamer 객체 생성
            out.println(username + "님의 게임이 시작됩니다! 현재 소지금: " + balance + "원");

            // 클라이언트 준비 완료를 서버에 알림
            latch.countDown();

            synchronized (this) {
                wait(); // 게임 시작까지 대기
            }

            // 게임 진행은 Game 클래스에서 관리

        } catch (IOException | InterruptedException e) {
            System.err.println("클라이언트 처리 중 오류: " + e.getMessage());
        }
        // finally 블록 제거
    }
}
