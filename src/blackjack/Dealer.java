package blackjack;

import java.util.ArrayList;
import java.util.List;

/*
    Dealer의 역할
    - 추가로 카드를 받는다
    - 2카드의 합계 점수가 16점 이하면 반드시 1장을 추가로 뽑고, 17점 이상이면 카드를 추가로 받을 수 없다.
    - 뽑은 카드 소유
    - 카드 오픈
 */
public class Dealer implements Player {
    private List<Card> cards;
    private static final int Limit_Receive_Point = 16;
    private boolean turn;
    private static final String name = "dealer";
    public Dealer() {
        cards = new ArrayList<>(); //dealer의 손패
    }

    @Override
    public void receiveCard(Card card) {
        if(this.isReceivedCard()){
            this.cards.add(card);
            this.showCards();
        }else{
            System.out.println("점수의 합이 17이상입니다. 카드를 받을 수 없습니다.");
        }
        int currentScore = getPointSum();
        if (currentScore > 21) {
            System.out.println(this.name + "는 버스트 했습니다! (점수: " + currentScore + ")");
            this.turnOff(); // 턴 자동 종료
        }
    }

    boolean isReceivedCard(){  //매직넘버를 방지하기 위한 메소드
        return getPointSum() <= Limit_Receive_Point;
    }

    public int getPointSum(){
        int sum = 0;
        for(Card card : cards){
            sum += card.getPoint();
        }
        return sum;
    }

    @Override
    public void showCards() { // 현재 유저의 손패를 보여줌
        StringBuilder sb = new StringBuilder();
        int cardCount = cards.size(); // 유저의 손패가 몇 장인지 확인
        sb.append(name).append("의 현재 보유카드 개수: ").append(cardCount).append("장\n");
        for (Card card : cards) {
            sb.append(card.toString()).append("\n");
        }
        sb.append("현재 점수: ").append(getPointSum()); // 점수도 출력
        System.out.println(sb.toString());
    }

    @Override
    public List<Card> openCards(){
        return this.cards;
    }

    @Override
    public void turnOff(){
        this.setTurn(false);
    }

    @Override
    public void turnOn(){
        this.setTurn(true);
    }

    @Override
    public boolean isTurn(){
        return this.turn;
    }

    private void setTurn(boolean turn){
        this.turn = turn;
    }

    @Override
    public String getName(){
        return this.name;
    }
}