package blackjack;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.CountDownLatch;


public class Server {
    private static final int PORT = 12345; // 서버 포트 번호
    private static final int MAX_CLIENTS = 3; // 최대 클라이언트 수
    private static CountDownLatch latch; // 클라이언트 선택을 기다리는 Latch
    private static AtomicInteger clientCount = new AtomicInteger(0); // 접속 클라이언트 수
    private static List<ClientHandler> clientHandlers = new ArrayList<>(); // 클라이언트 핸들러 리스트

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Blackjack 서버가 시작되었습니다.");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                System.out.println("클라이언트를 기다리는 중...");
                Socket clientSocket = serverSocket.accept(); // 클라이언트 연결 대기
                ClientHandler clientHandler = new ClientHandler(clientSocket);

                if (clientCount.get() >= MAX_CLIENTS) {
                    System.out.println("최대 접속 인원에 도달하여 더 이상 접속할 수 없습니다.");
                    clientSocket.close(); // 추가된 클라이언트 연결 닫기
                    break; // 3명 이상이면 접속을 차단
                }
                if (clientCount.incrementAndGet() <= MAX_CLIENTS) {
                    System.out.println("클라이언트가 연결되었습니다!");

                    // 새로운 스레드에서 클라이언트 처리
                    clientHandlers.add(clientHandler);
                    new Thread(clientHandler).start();
                }                  
              
            }
        } catch (IOException e) {
            System.err.println("서버 오류: " + e.getMessage());
        }
    }
}


class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Gamer gamer; // Gamer 객체로 유저 정보 관리


    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            out.println("Blackjack 게임에 오신 것을 환영합니다!");
            
            // 게임 시작 시 이름과 소지금 입력 받기
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

            boolean keepPlaying = true;
            while (keepPlaying) {
                // Play 객체 생성 및 실행
            	Play play = new Play(out, in,gamer); // balance 추가 전달
            	play.play();
            	balance = gamer.getBalance(); // 게임 후 balance 업데이트
            	if(balance<=0) {
            		keepPlaying = false;
            		out.println("소지금이 모두 소진되어 게임을 종료합니다.");
            		break;
            		}

                // 게임 종료 후, 다시 게임할지 묻기
                out.println("다시 게임을 시작하시겠습니까? (yes/no)");
                String response = in.readLine();

                if (response.equalsIgnoreCase("no")) {
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
    
    public void sendResults() {
        out.println("게임 결과를 전송합니다.");
        // 게임 결과 전송 로직 작성
    }
}

