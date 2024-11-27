package blackjack;

import java.util.*;
import java.io.*;

public class Play {
    private static final int START_RECEIVE_CARD_COUNT = 2;
    private static final String STOP_RECEIVE_CARD = "0";
    private PrintWriter out;
    private BufferedReader in;
    private Gamer gamer; // Gamer 객체로 유저 정보 관리
    private List<Player> players = new ArrayList<>(); // 여러 플레이어 관리
    private Map<Player, Integer> betAmounts; // 각 플레이어의 배팅 금액 저장
    private Dealer dealer;


    public Play(PrintWriter out, BufferedReader in,Gamer gamer) {
        this.out = out;
        this.in = in;
        this.gamer = gamer;
        this.betAmounts = new HashMap<>(); // betAmounts 초기화 추가

    }

    public void play() {
        out.println("================= Blackjack =================");
        try {
            players.add(gamer); // 플레이어 추가
            Dealer dealer = new Dealer();
            players.add(dealer); // 딜러 추가
            out.println("플레이어 초기화 완료: " + players.size() + "명 참가");

            for (Player player : players) {
                player.resetCards(); // 카드 초기화
            }

            // 배팅 금액 설정
            initializeBets();

            // 카드 덱 생성
            CardDeck cardDeck = new CardDeck();

            // 초기 카드 분배
            startPhase(cardDeck, players);

            // 플레이어의 턴 진행
            playingPhase(cardDeck, players);

            // 딜러의 턴 진행
            dealerTurn(cardDeck);

            // 결과 계산 및 출력
            Rule rule = new Rule();
            List<Player> winners = rule.getWinners(players);
            endGame(winners, players);

        } catch (IOException e) {
            out.println("잘못된 입력으로 게임을 종료합니다.");
        }
    }

    private void initializeBets() throws IOException {
        Player player = gamer;
        while (true) {
            out.println(player.getName() + "의 배팅 금액을 입력하세요. (소지금: " + player.getBalance() + ")");
            try {
                int betAmount = Integer.parseInt(in.readLine());
                if (betAmount > 0 && betAmount <= player.getBalance()) {
                    player.updateBalance(-betAmount); // 소지금 차감
                    betAmounts.put(player, betAmount);
                    break;
                } else {
                    out.println("소지금을 초과하거나 유효하지 않은 금액입니다. 다시 입력해주세요.");
                }
            } catch (NumberFormatException e) {
                out.println("숫자를 입력해주세요.");
            }
        }
    }

    private List<Player> playingPhase(CardDeck cardDeck, List<Player> players) throws IOException {
        for (Player player : players) {
            if (player instanceof Dealer) continue; // 딜러는 이 단계에서 제외

            out.println("-------------------------");
            out.println(player.getName() + "'s turn.");

            while (player.isTurn() && player.getPointSum() <= 21) {
                out.println(player.getName() + "의 현재 점수: " + player.getPointSum());
                out.println("추가 카드: 1, 종료: 0");
                String input = in.readLine();
                if ("1".equals(input)) {
                    Card card = cardDeck.draw();
                    player.receiveCard(card);
                    out.println(player.getName() + "가 뽑은 카드: " + card.toString());
                    player.showCards(out);
                } else if ("0".equals(input)) {
                    player.turnOff();
                } else {
                    out.println("잘못된 입력입니다. 다시 입력하세요.");
                }
            }

            if (player.getPointSum() > 21) {
                out.println(player.getName() + "님이 버스트 했습니다.");
                player.turnOff();
            }
        }

        return players;
    }

    private List<Player> receiveCardAllPlayers(CardDeck cardDeck, List<Player> players) throws IOException {
        for (Player player : players) {
            if (!player.isTurn()) continue;

            out.println("-------------------------"); // 구분선 추가
            out.println(player.getName() + "'s turn.");
            if (player instanceof Dealer) {
                Dealer dealer = (Dealer) player;
                if (dealer.isReceivedCard()) {
                    out.println("딜러가 카드를 뽑습니다.");
                    Card card = cardDeck.draw();
                    dealer.receiveCard(card);
                    out.println("딜러가 뽑은 카드: " + card.toString());
                    dealer.showCards(out);
                } else {
                    dealer.turnOff();
                }
                continue;
            }
            if (player.getPointSum() > 21) {
                out.println(player.getName() + "님이 이미 버스트 했습니다.");
                player.turnOff();
                continue;
            }

            if (isReceiveCard()) {
                Card card = cardDeck.draw();
                player.receiveCard(card);
                out.println(player.getName() + "가 뽑은 카드: " + card.toString());
                player.showCards(out);
            } else {
                player.turnOff();
            }
        }
        return players;
    }

    private boolean isAllPlayerTurnOff(List<Player> players) {
        return players.stream().noneMatch(Player::isTurn);
    }

    private boolean isReceiveCard() throws IOException {
        out.println("추가 카드: 1, 종료: 0");
        while (true) {
            String input = in.readLine(); // 클라이언트로부터 입력 받기
            if ("1".equals(input)) {
                return true;
            } else if ("0".equals(input)) {
                return false;
            } else {
                out.println("잘못된 입력입니다. 다시 입력하세요. 추가 카드: 1, 종료: 0");
            }
        }
    }

    private List<Player> startPhase(CardDeck cardDeck, List<Player> players) {
        out.println("게임 시작: 각 플레이어는 2장의 카드를 뽑습니다.");
        for (int i = 0; i < START_RECEIVE_CARD_COUNT; i++) {
            for (Player player : players) {
                out.println("-------------------------"); // 구분선 추가
                out.println(player.getName() + "가 카드를 뽑습니다.");
                Card card = cardDeck.draw();
                player.receiveCard(card);
                player.turnOn();
                out.println(player.getName() + "가 뽑은 카드: " + card.toString());
            }
        }

        for (Player player : players) {
            out.println("-------------------------"); // 구분선 추가
            out.println(player.getName() + "의 초기 카드 상태:");
            player.showCards(out);
        }
        return players;
    }

    private void endGame(List<Player> winners, List<Player> players) {
        out.println("================= Game Over =================");
        for (Player player : players) {
            out.println("-------------------------");
            out.println(player.getName() + "의 최종 카드 상태:");
            player.showCards(out);
        }

        if (winners.isEmpty()) {
            out.println("\n승자가 없습니다! 모든 플레이어가 버스트되었습니다.");
            // 모든 플레이어는 배팅 금액을 잃음
            for (Player player : players) {
                if (player instanceof Gamer) {
                    out.println(player.getName() + "는 배팅 금액을 잃었습니다.");
                }
            }
        } else if (winners.size() == 1) {
            Player winner = winners.get(0);
            out.println("\n승자는 " + winner.getName() + "입니다! 점수: " + winner.getPointSum());

            for (Player player : players) {
                if (player instanceof Gamer) {
                    Gamer gamer = (Gamer) player;
                    int betAmount = betAmounts.get(gamer);

                    if (player == winner) {
                        // 승리: 2배 지급
                        gamer.updateBalance(betAmount * 2);
                        out.println(gamer.getName() + "는 승리하여 소지금이 " + betAmount * 2 + " 증가했습니다!");
                    } else {
                        // 패배: 배팅 금액 잃음
                        out.println(gamer.getName() + "는 패배하여 배팅 금액을 잃었습니다.");
                    }
                }
            }
        } else {
            out.println("\n무승부입니다! 다음 플레이어가 동점입니다:");
            for (Player winner : winners) {
                out.println("- " + winner.getName() + " with a score of " + winner.getPointSum());
            }

            for (Player player : players) {
                if (player instanceof Gamer) {
                    Gamer gamer = (Gamer) player;
                    int betAmount = betAmounts.get(gamer);

                    if (winners.contains(player)) {
                        // 무승부: 배팅 금액 돌려줌
                        gamer.updateBalance(betAmount);
                        out.println(gamer.getName() + "는 무승부로 배팅 금액 " + betAmount + "을 돌려받았습니다.");
                    } else {
                        // 패배: 배팅 금액 잃음
                        out.println(gamer.getName() + "는 패배하여 배팅 금액을 잃었습니다.");
                    }
                }
            }
        }
        out.println("-------------------------");
    }

    private void dealerTurn(CardDeck cardDeck) {
        out.println("-------------------------");
        out.println("딜러의 턴 시작.");

        while (dealer.isReceivedCard()) {
            Card card = cardDeck.draw();
            dealer.receiveCard(card);
            out.println("딜러가 뽑은 카드: " + card);
            dealer.showCards(out);
        }

        out.println("딜러의 턴 종료.");
    }

}
