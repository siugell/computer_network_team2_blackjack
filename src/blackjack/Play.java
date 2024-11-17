package blackjack;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Play {

    private static final int START_RECEIVE_CARD_COUNT = 2;
    private static final String STOP_RECEIVE_CARD = "0";

    public void play() {
        System.out.println("========= Blackjack =========");
        try (Scanner sc = new Scanner(System.in)) {
            CardDeck cardDeck = new CardDeck();
            Rule rule = new Rule();

            // 플레이어 설정 (유저와 딜러)
            List<Player> players = Arrays.asList(new Gamer("user 1"), new Dealer());
            List<Player> startAfterPlayers = startPhase(cardDeck, players);
            List<Player> playingAfterPlayers = playingPhase(sc, cardDeck, startAfterPlayers);

            // 승자 판별
            Player winner = rule.getWinner(playingAfterPlayers);

            // 게임 종료
            endGame(winner, players);
        }
    }

    private List<Player> playingPhase(Scanner sc, CardDeck cardDeck, List<Player> players) {
        List<Player> cardReceivedPlayers;
        while (true) {
            cardReceivedPlayers = receiveCardAllPlayers(sc, cardDeck, players);

            if (isAllPlayerTurnOff(cardReceivedPlayers)) {
                break;
            }
        }
        return cardReceivedPlayers;
    }

    private List<Player> receiveCardAllPlayers(Scanner sc, CardDeck cardDeck, List<Player> players) {
        for (Player player : players) {
            // 플레이어가 이미 턴을 종료한 경우 스킵
            if (!player.isTurn()) continue;

            System.out.println("\n" + player.getName() + "'s turn.");

            // 딜러의 경우 자동으로 한 장만 뽑고 턴 종료
            if (player instanceof Dealer) {
                Dealer dealer = (Dealer) player;
                if (dealer.isReceivedCard()) {
                    System.out.println("딜러가 카드를 뽑습니다.");
                    Card card = cardDeck.draw();
                    dealer.receiveCard(card);

                    // 딜러의 점수가 17 이상이거나 버스트되면 턴 종료
                    if (dealer.getPointSum() >= 17 || dealer.getPointSum() > 21) {
                        dealer.turnOff();
                        System.out.println("\n딜러의 점수가 17점 이상이거나 버스트 했습니다. 더 이상 카드를 받을 수 없습니다.");
                        dealer.showCards();
                    }
                }
                continue;
            }

            // 플레이어의 경우, 추가 카드를 받을지 여부 선택
            // 버스트된 경우에는 카드를 받을지 묻지 않음
            if (player.getPointSum() > 21) {
                System.out.println(player.getName() + "님이 이미 버스트 했습니다.");
                player.turnOff();
                continue;
            }

            // 추가 카드를 받을지 선택
            if (isReceiveCard(sc)) {
                Card card = cardDeck.draw();
                player.receiveCard(card);
            } else {
                player.turnOff();
            }
        }
        return players;
    }

    private boolean isAllPlayerTurnOff(List<Player> players) {
        for (Player player : players) {
            if (player.isTurn()) {
                return false;
            }
        }
        return true;
    }

    private boolean isReceiveCard(Scanner sc) {
        System.out.println("추가 카드: 1, 종료: 0");
        while (true) {
            String input = sc.nextLine();
            if (input.equals("1")) {
                return true;
            } else if (input.equals(STOP_RECEIVE_CARD)) {
                return false;
            } else {
                System.out.println("잘못된 입력입니다. 다시 입력하세요. 추가 카드: 1, 종료: 0");
            }
        }
    }

    private List<Player> startPhase(CardDeck cardDeck, List<Player> players) {
        System.out.println("\n게임 시작: 각 플레이어는 2장의 카드를 뽑습니다.");
        for (int i = 0; i < START_RECEIVE_CARD_COUNT; i++) {
            for (Player player : players) {
                Card card = cardDeck.draw();
                player.receiveCard(card);
                System.out.println();
                player.turnOn();
            }
        }

        // 각 플레이어의 초기 상태 출력
        for (Player player : players) {
            System.out.println("\n" + player.getName() + "의 초기 상태:");
            player.showCards();
        }
        return players;
    }

    private void endGame(Player winner, List<Player> players) {
        System.out.println("\n========= Game Over =========");
        for (Player player : players) {
            System.out.println(player.getName() + "의 점수: " + player.getPointSum());
        }
        System.out.println("Winner is " + winner.getName());
    }

}