package blackjack;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Play {

    private static final int START_RECEIVE_CARD_COUNT = 2;
    private static final String STOP_RECEIVE_CARD = "0";

    //게임 플레이가 선언되면 카드덱과 룰을 선언하고, 모든 유저와 딜러를 List<Player>에 넣는다.
    public void play() {
        System.out.println("========= Blackjack =========");
        try (Scanner sc = new Scanner(System.in)) {
            CardDeck cardDeck = new CardDeck();
            Rule rule = new Rule();

            // 플레이어 설정 (유저와 딜러)
            List<Player> players = Arrays.asList(new Gamer("user 1"), new Dealer());
            List<Player> startAfterPlayers = startPhase(cardDeck, players);

            //블랙잭 승자가 있으면 게임을 즉시 종료하는 로직 추가
            //블랙잭 승자가 있는지 확인
            List<Player> blackjackwinner = rule.getBlackjackWinners(startAfterPlayers);
            //블랙잭 승자가 있을때만 게임을 종료
            if(!blackjackwinner.isEmpty()){
                endBlackjackWinner(blackjackwinner, players);
            }else {
                List<Player> playingAfterPlayers = playingPhase(sc, cardDeck, startAfterPlayers);

                // 승자 판별
                List<Player> winner = rule.getWinners(playingAfterPlayers);

                // 게임 종료
                endGame(winner, players);
            }
        }
    }

    //게임의 시작 페이즈, 모든 플레이어가 카드를 2장 뽑게한 후 turnOn상태로 만든다.
    private List<Player> startPhase(CardDeck cardDeck, List<Player> players) {
        System.out.println("\n게임 시작: 각 플레이어는 2장의 카드를 뽑습니다.");
        for (int i = 0; i < START_RECEIVE_CARD_COUNT; i++) {
            for (Player player : players) {
                Card card = cardDeck.draw();
                player.receiveCard(card);
                System.out.println();
            }
        }

        // 각 플레이어의 초기 상태 출력
        for (Player player : players) {
            System.out.println("\n" + player.getName() + "의 초기 상태:");
            player.showCards();
            // 딜러가 17점 이상인 경우 초기 상태에서 턴 종료
            if (player instanceof Dealer) {
                Dealer dealer = (Dealer) player;
                if (dealer.getPointSum() >= 17) {
                    System.out.println("딜러의 점수가 17점 이상입니다. 더이상 카드를 받지 않습니다.");
                    dealer.turnOff();
                } else {
                    dealer.turnOn();
                }
            } else {
                // 게이머는 항상 턴을 켬
                player.turnOn();
            }
        }
        return players;
    }

    //입력받은(turnOn상태) List<Player> cardReceivedPlayer가 카드를 뽑게함, 모든 플레이어가 턴을 종료하면 break함.
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
                    if (dealer.getPointSum() > 16) {
                        dealer.turnOff();
                        System.out.println("\n딜러의 점수가 17점 이상이거나 버스트 했습니다. 더 이상 카드를 받을 수 없습니다.");
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

            // 추가 카드를 받을지 선택, 받지 않으면 turnOff상태로 만든다.
            if (isReceiveCard(sc)) {
                System.out.println(player.getName() + "님이 카드를 뽑습니다.");
                Card card = cardDeck.draw();
                player.receiveCard(card);
            } else {
                player.turnOff();
            }
        }
        return players;
    }

    //모든 player가 turnOff상태인지 확인
    private boolean isAllPlayerTurnOff(List<Player> players) {
        for (Player player : players) {
            if (player.isTurn()) {
                return false; //한명이라도 TurnOn이면 종료하지 않음
            }
        }
        return true; //모든 플레이어가 턴을 종료하면 true반환
    }

    //추가 카드를 받을지 물어보는 메소드
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
    //regular game종료
    private void endGame(List<Player> winners, List<Player> players) {
        System.out.println("\n========= Game Over =========");

        // 모든 플레이어의 점수 출력
        for (Player player : players) {
            System.out.println(player.getName() + "의 점수: " + player.getPointSum());
        }

        // 승자 판별
        if (winners.isEmpty()) {
            System.out.println("\nNo winner! All players busted.");
        } else if (winners.size() == 1) {
            System.out.println("\nWinner is " + winners.get(0).getName() + " with a score of " + winners.get(0).getPointSum());
        } else {
            System.out.println("\nIt's a tie between the following players:");
            for (Player winner : winners) {
                System.out.println("- " + winner.getName() + " with a score of " + winner.getPointSum());
            }
        }
    }
    //blackjack승리로 인한 종료
    private void endBlackjackWinner(List<Player> winners, List<Player> players){
        System.out.println("\n========= BlackJack Game Over =========");

        // 모든 플레이어의 점수 출력
        for (Player player : players) {
            System.out.println(player.getName() + "의 점수: " + player.getPointSum());
        }

        // 승자 발표
        if (winners.size() == 1) {
            System.out.println("\nWinner is " + winners.get(0).getName() + " with a score of " + winners.get(0).getPointSum());
            System.out.println(winners.get(0).getName() + "'s winning hand: ");
            winners.get(0).showCards();
        } else {
            System.out.println("\nBlackjack tie! The following players share the victory:");
            for (Player winner : winners) {
                System.out.println("- " + winner.getName() + " with a score of " + winner.getPointSum());
                System.out.println(winner.getName() + "'s winning hand: ");
                winner.showCards();
            }
        }
    }


}