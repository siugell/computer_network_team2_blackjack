package blackjack;

import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;

public class Dealer implements Player {
    private List<Card> cards;
    private static final int Limit_Receive_Point = 16;
    private boolean turn;
    private static final String name = "dealer";

    public Dealer() {
        cards = new ArrayList<>();
    }

    @Override
    public void receiveCard(Card card) {
        if (this.isReceivedCard()) {
            this.cards.add(card);
            int currentScore = getPointSum();
            if (currentScore > 21) {
                System.out.println(name + "는 버스트 했습니다! (점수: " + currentScore + ")");
                this.turnOff();
            } else if (currentScore >= 17) {
                System.out.println(name + "는 점수가 17 이상입니다. 턴 종료.");
                this.turnOff(); // 점수가 17 이상일 경우 턴 종료
            }
        } else {
            System.out.println(name + "는 더 이상 카드를 받을 수 없습니다.");
            this.turnOff(); // 카드 받을 조건이 아니면 턴 종료
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

    boolean isReceivedCard() {
        return getPointSum() <= Limit_Receive_Point;
    }

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
            sum -= 10;
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
    public void resetCards() {
        this.cards.clear();  // 카드 목록 초기화
    }
}
