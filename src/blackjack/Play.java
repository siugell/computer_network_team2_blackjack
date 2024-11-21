package blackjack;

import java.util.*;
import java.io.*;

public class Play {
    private static final int START_RECEIVE_CARD_COUNT = 2;
    private static final String STOP_RECEIVE_CARD = "0";
    private PrintWriter out;
    private BufferedReader in;

    public Play(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

    public void play() {
        out.println("================= Blackjack =================");
        try {
            CardDeck cardDeck = new CardDeck();
            Rule rule = new Rule();

            List<Player> players = Arrays.asList(new Gamer("user 1"), new Dealer());
            List<Player> startAfterPlayers = startPhase(cardDeck, players);
            List<Player> playingAfterPlayers = playingPhase(cardDeck, startAfterPlayers);

            List<Player> winners = rule.getWinners(playingAfterPlayers);
            endGame(winners, players);
        } catch (IOException e) {
            out.println("게임 진행 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private List<Player> playingPhase(CardDeck cardDeck, List<Player> players) throws IOException {
        List<Player> cardReceivedPlayers;
        while (true) {
            cardReceivedPlayers = receiveCardAllPlayers(cardDeck, players);
            if (isAllPlayerTurnOff(cardReceivedPlayers)) {
                break;
            }
        }
        return cardReceivedPlayers;
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
            } else if (STOP_RECEIVE_CARD.equals(input)) {
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
        } else if (winners.size() == 1) {
            out.println("\n승자는 " + winners.get(0).getName() + "입니다! 점수: " + winners.get(0).getPointSum());
        } else {
            out.println("\n무승부입니다! 다음 플레이어가 동점입니다:");
            for (Player winner : winners) {
                out.println("- " + winner.getName() + " with a score of " + winner.getPointSum());
            }
        }
        out.println("-------------------------");
    }
}
