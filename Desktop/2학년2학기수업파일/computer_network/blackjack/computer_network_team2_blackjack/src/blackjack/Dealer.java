package blackjack;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dealer implements Player {
    private List<Card> cards;
    private Card hiddenCard; // 숨겨진 카드
    private static final int LIMIT_RECEIVE_POINT = 16;
    private boolean turn;
    private static final String name = "Dealer";

    public Dealer() {
        cards = new ArrayList<>();
    }

    @Override
    public void receiveCard(Card card) {
        if (cards.size() == 0) {
            cards.add(card); // 첫 번째 카드는 공개된 카드 목록에 추가
        } else if (hiddenCard == null) {
            hiddenCard = card; // 두 번째 카드는 숨겨진 카드로 저장
        } else if (this.isReceivedCard()) {
            cards.add(card); // 추가로 받는 카드는 공개된 카드 목록에 추가
        } else {
            this.turnOff();
        }
    }

    // 딜러의 전체 점수를 계산 (숨겨진 카드 포함, 에이스 조정)
    public int getTotalPointSum() {
        int sum = 0;
        int aceCount = 0;

        // 공개된 카드의 점수 합계 및 에이스 개수
        for (Card card : cards) {
            int value = card.getPoint();
            if (value == 1) { // Ace
                aceCount++;
                sum += 11;
            } else if (value >= 10) {
                sum += 10;
            } else {
                sum += value;
            }
        }

        // 숨겨진 카드의 점수 합계 및 에이스 개수
        if (hiddenCard != null) {
            int value = hiddenCard.getPoint();
            if (value == 1) { // Ace
                aceCount++;
                sum += 11;
            } else if (value >= 10) {
                sum += 10;
            } else {
                sum += value;
            }
        }

        // 에이스 값 조정 (11점에서 1점으로)
        while (sum > 21 && aceCount > 0) {
            sum -= 10;
            aceCount--;
        }

        return sum;
    }

    @Override
    public void showCards(PrintWriter out) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("의 공개된 카드:\n");
        for (Card card : cards) {
            sb.append(card.toString()).append("\n");
        }
        sb.append("현재 점수: ").append(getPointSum());
        out.println(sb.toString());
    }

    // 딜러의 전체 카드를 보여주는 메서드 (게임 종료 시 사용)
    public String getAllCardsInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("의 전체 카드:\n");
        if (hiddenCard != null) {
            sb.append(hiddenCard.toString()).append(" (hidden)\n");
        }
        for (Card card : cards) {
            sb.append(card.toString()).append("\n");
        }
        sb.append("총 점수: ").append(getTotalPointSum());
        return sb.toString();
    }

    public String getAllCardsStringInfo() { //endgame에서 사용되는 딜러의 손패보여주기
        StringBuilder sb = new StringBuilder();
        if (hiddenCard != null) {
            sb.append(hiddenCard.toString()).append("(hidden) ");
        }
        for (Card card : cards) {
            sb.append(card.toString()).append(" ");
        }
        sb.append("(점수: ").append(getTotalPointSum()).append(")");
        return sb.toString();
    }

    @Override
    public int getPointSum() {
        int sum = 0;
        int aceCount = 0;

        for (Card card : cards) {
            int value = card.getPoint();
            if (value == 1) { // Ace
                aceCount++;
                sum += 11;
            } else if (value >= 10) { // Face cards
                sum += 10;
            } else {
                sum += value;
            }
        }

        // 에이스 값 조정 (11점에서 1점으로)
        while (sum > 21 && aceCount > 0) {
            sum -= 10;
            aceCount--;
        }

        return sum;
    }

    public boolean isReceivedCard() {
        return getTotalPointSum() <= LIMIT_RECEIVE_POINT;
    }

    @Override
    public void resetCards() {
        this.cards.clear();
        this.hiddenCard = null;
        this.turnOn();
    }

    @Override
    public List<Card> openCards() {
        return this.cards;
    }

    @Override
    public void turnOff() {
        this.setTurn(false);
    }

    @Override
    public void turnOn() {
        this.setTurn(true);
    }

    @Override
    public boolean isTurn() {
        return this.turn;
    }

    private void setTurn(boolean turn) {
        this.turn = turn;
    }

    @Override
    public String getName() {
        return Dealer.name;
    }

    @Override
    public int getBalance() {
        return 0; // 딜러는 소지금을 가지지 않음
    }

    @Override
    public void updateBalance(int amount) {
        // 딜러는 소지금 업데이트 없음
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Dealer)) return false;
        Dealer dealer = (Dealer) obj;
        return Objects.equals(name, dealer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
