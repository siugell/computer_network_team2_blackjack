package blackjack;

import java.io.IOException;
import java.util.*;

public class Game {
    private List<ClientHandler> clientHandlers; // 클라이언트 핸들러 리스트
    private List<Gamer> players; // 플레이어 리스트
    private Dealer dealer;
    private CardDeck cardDeck;
    private Rule rule;
    private Map<Player, Integer> betAmounts; // 각 플레이어의 배팅 금액
    private List<Gamer> blackjackPlayers; // 블랙잭인 플레이어 목록

    public Game(List<ClientHandler> clientHandlers) {
        this.clientHandlers = clientHandlers;
        this.players = new ArrayList<>();
        for (ClientHandler handler : clientHandlers) {
            players.add(handler.getGamer());
        }
        this.dealer = new Dealer();
        this.cardDeck = new CardDeck();
        this.rule = new Rule();
        this.betAmounts = new HashMap<>();
    }

    public void start() {
        try {
            while (!players.isEmpty()) {
                // 모든 클라이언트에게 게임 시작 알림
                notifyAllClients("새 게임을 시작합니다!");

                // 배팅 금액 입력
                initializeBets();

                // 시작 단계: 카드 분배
                startPhase();

                // 플레이 단계: 추가 카드 여부 선택
                playingPhase();

                // 승자 결정
                List<Player> winners = rule.getWinners(new ArrayList<>(players) {{ add(dealer); }});

                // 게임 종료 및 결과 전달
                endGame(winners);

                // 플레이어의 소지금이 0인 경우 연결 종료
                removeBrokePlayers();

                resetPlayersHands();

                // 잠시 대기
                Thread.sleep(1000);
            }

            // 모든 플레이어가 소지금이 0이 되어 게임 종료
            notifyAllClients("모든 플레이어의 소지금이 소진되어 게임을 종료합니다.");

            // 모든 클라이언트의 소켓을 닫음.
            for (ClientHandler handler : clientHandlers) {
                handler.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void notifyAllClients(String message) throws IOException {
        for (ClientHandler handler : clientHandlers) {
            synchronized (handler) {
                handler.sendMessage(message);
            }
        }
    }

    private void initializeBets() throws IOException {
        for (Gamer player : players) {
            ClientHandler handler = getHandlerByGamer(player);
            handler.sendMessage(player.getName() + "의 배팅 금액을 입력하세요. (소지금: " + player.getBalance() + ")");
            while (true) {
                String betInput = handler.receiveInput();
                try {
                    int betAmount = Integer.parseInt(betInput);
                    if (betAmount > 0 && betAmount <= player.getBalance()) {
                        player.updateBalance(-betAmount); // 소지금 차감
                        betAmounts.put(player, betAmount);
                        break;
                    } else {
                        handler.sendMessage("소지금을 초과하거나 유효하지 않은 금액입니다. 다시 입력해주세요.");
                    }
                } catch (NumberFormatException e) {
                    handler.sendMessage("숫자를 입력해주세요.");
                }
            }
        }
    }

    private void startPhase() throws IOException {
        // 플레이어와 딜러에게 카드 두 장씩 분배
        for (int i = 0; i < 2; i++) {
            for (Gamer player : players) {
                Card card = cardDeck.draw();
                player.receiveCard(card);

                ClientHandler handler = getHandlerByGamer(player);
                handler.sendMessage("당신이 받은 카드: " + card.toString());
            }

            // 딜러 카드 분배
            Card dealerCard = cardDeck.draw();
            dealer.receiveCard(dealerCard);
        }

        // 플레이어의 턴 활성화
        for (Gamer player : players) {
            player.turnOn(); // 플레이어의 턴을 활성화합니다.
        }

        // 블랙잭 플레이어 목록 초기화
        blackjackPlayers = new ArrayList<>();

        // 플레이어에게 현재 카드와 점수 전달 및 블랙잭 여부 확인
        for (Gamer player : players) {
            ClientHandler handler = getHandlerByGamer(player);
            handler.sendMessage("당신의 현재 카드:");
            for (Card card : player.openCards()) {
                handler.sendMessage(card.toString());
            }
            handler.sendMessage("현재 점수: " + player.getPointSum());

            // 딜러의 첫 번째 카드 정보 전송
            handler.sendMessage("딜러의 공개된 카드:");
            List<Card> dealerOpenCards = dealer.openCards();
            handler.sendMessage(dealerOpenCards.get(0).toString());

            // 블랙잭 여부 확인
            if (rule.isBlackjack(player)) {
                blackjackPlayers.add(player); // 블랙잭인 플레이어 목록에 추가
                player.turnOff(); // 플레이어의 턴 종료
                handler.sendMessage("블랙잭입니다!");
            }
        }

        // 딜러의 블랙잭 여부 확인
        if (rule.isBlackjack(dealer)) {
            dealer.turnOff(); // 딜러의 턴 종료
            // 모든 플레이어에게 딜러의 블랙잭을 알림
            for (Gamer player : players) {
                ClientHandler handler = getHandlerByGamer(player);
                handler.sendMessage("딜러가 블랙잭입니다!");
            }
        }
    }


    private void playingPhase() throws IOException {
        // 딜러가 블랙잭이면 플레이어들의 추가 진행 없이 종료
        if (rule.isBlackjack(dealer)) {
            return;
        }

        boolean allPlayersDone = false;

        while (!allPlayersDone) {
            allPlayersDone = true;

            for (Gamer player : players) {
                ClientHandler handler = getHandlerByGamer(player);
                if (!player.isTurn()) {
                    handler.sendMessage("다른 플레이어의 차례를 기다리고있습니다.");
                    continue;
                }



                handler.sendMessage("현재 점수: " + player.getPointSum());
                handler.sendMessage("추가 카드: 1, 종료: 0");

                String input = handler.receiveInput();
                if ("1".equals(input)) {
                    Card card = cardDeck.draw();
                    player.receiveCard(card);
                    handler.sendMessage("당신이 받은 카드: " + card.toString());
                    handler.sendMessage("현재 점수: " + player.getPointSum());

                    if (player.getPointSum() > 21) {
                        handler.sendMessage("버스트되었습니다!");
                        player.turnOff();
                    } else {
                        allPlayersDone = false;
                    }
                } else if ("0".equals(input)) {
                    player.turnOff();
                } else {
                    handler.sendMessage("잘못된 입력입니다. 다시 입력하세요.");
                    allPlayersDone = false;
                }
            }
        }

        while (dealer.isReceivedCard()) { // 수정된 메서드 사용
            Card card = cardDeck.draw();
            dealer.receiveCard(card);
            // 딜러의 새 카드 정보를 모든 플레이어에게 알림
            notifyAllClients("딜러가 받은 카드: " + card.toString());
        }
    }


    private void endGame(List<Player> winners) throws IOException {
        // 각 플레이어에게 결과 전달
        for (Gamer player : players) {
            ClientHandler handler = getHandlerByGamer(player);

            handler.sendMessage("================= Game Over =================");

            // 딜러의 전체 카드와 점수 공개
            handler.sendMessage(dealer.getAllCardsInfo());

            handler.sendMessage("당신의 최종 카드:");
            for (Card card : player.openCards()) {
                handler.sendMessage(card.toString());
            }
            handler.sendMessage("당신의 최종 점수: " + player.getPointSum());

            int betAmount = betAmounts.get(player);

            if (rule.isBlackjack(player)) {
                if (rule.isBlackjack(dealer)) {
                    // 플레이어와 딜러 모두 블랙잭인 경우 무승부
                    player.updateBalance(betAmount); // 배팅 금액 돌려받음
                    handler.sendMessage("딜러도 블랙잭입니다. 무승부로 배팅 금액을 돌려받습니다.");
                } else {
                    // 플레이어만 블랙잭인 경우 승리
                    player.updateBalance(betAmount + (int)(betAmount * 1.5));
                    handler.sendMessage("블랙잭으로 승리하셨습니다! 배팅 금액의 1.5배를 획득했습니다.");
                }
            } else if (rule.isBlackjack(dealer)) {
                // 딜러만 블랙잭인 경우 패배
                handler.sendMessage("딜러가 블랙잭입니다. 패배하셨습니다. 배팅 금액을 잃었습니다.");
            } else if (player.getPointSum() > 21) {
                // 플레이어가 버스트된 경우 패배
                handler.sendMessage("버스트되어 패배하셨습니다. 배팅 금액을 잃었습니다.");
            } else if (dealer.getTotalPointSum() > 21 || player.getPointSum() > dealer.getTotalPointSum()) {
                // 딜러가 버스트되었거나, 플레이어가 딜러보다 점수가 높은 경우 승리
                player.updateBalance(betAmount * 2);
                handler.sendMessage("승리하셨습니다! 배팅 금액의 2배를 획득했습니다.");
            } else if (player.getPointSum() == dealer.getTotalPointSum()) {
                // 무승부인 경우 배팅 금액 돌려받음
                player.updateBalance(betAmount);
                handler.sendMessage("무승부입니다. 배팅 금액을 돌려받습니다.");
            } else {
                // 그 외의 경우 패배
                handler.sendMessage("패배하셨습니다. 배팅 금액을 잃었습니다.");
            }

            handler.sendMessage("현재 소지금: " + player.getBalance());
        }
    }

    private void removeBrokePlayers() throws IOException {
        Iterator<Gamer> playerIterator = players.iterator();
        Iterator<ClientHandler> handlerIterator = clientHandlers.iterator();

        while (playerIterator.hasNext() && handlerIterator.hasNext()) {
            Gamer player = playerIterator.next();
            ClientHandler handler = handlerIterator.next();

            if (player.getBalance() <= 0) {
                handler.sendMessage("소지금이 0이 되어 게임에서 탈퇴합니다.");
                handler.close();
                handlerIterator.remove();
                playerIterator.remove();
            }
        }
    }

    private ClientHandler getHandlerByGamer(Gamer gamer) {
        for (ClientHandler handler : clientHandlers) {
            if (handler.getGamer() == gamer) {
                return handler;
            }
        }
        return null;
    }

    private void resetPlayersHands() {
        for (Gamer player : players) {
            player.resetCards();
        }
        dealer.resetCards();
    }
}
