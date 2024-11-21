package blackjack;

import java.util.LinkedList;
import java.util.List;
import java.io.PrintWriter;

public class Gamer implements Player {
    private List<Card> cards; // 유저가 소유하고 있는 카드들
    private boolean turn;
    private String name;

    public Gamer(String name) {
        this.cards = new LinkedList<>();
        this.name = name;
    }

    @Override
    public void receiveCard(Card card) {
        this.cards.add(card); // 뽑은 카드를 손패에 추가
        int currentScore = getPointSum();
        if (currentScore > 21) {
            System.out.println(this.name + "님이 버스트 했습니다! (점수: " + currentScore + ")");
            this.turnOff(); // 턴 자동 종료
        }
    }

    @Override
    public void showCards(PrintWriter out) { 
        StringBuilder sb = new StringBuilder();
        int cardCount = cards.size();
        sb.append(name).append("의 현재 보유카드 개수: ").append(cardCount).append("장\n");
        for (Card card : cards) {
            sb.append(card.toString()).append("\n");
        }
        sb.append("현재 점수: ").append(getPointSum()); // 점수도 출력
        out.println(sb.toString()); // 클라이언트로 전송
    }

    @Override
    public int getPointSum() {
        int sum = 0;
        int aceCount = 0;

        for (Card card : cards) {
            sum += card.getPoint();
            if (card.getPoint() == 11) {
                aceCount++;
            }
        }

        while (sum > 21 && aceCount > 0) {
            sum -= 10; // Ace를 11에서 1로 변경
            aceCount--;
        }

        return sum;
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
        return this.name;
    }
}