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

    /*
    딜러가 카드를 받는 로직입니다.
     */
    @Override
    public void receiveCard(Card card) {
        if(this.isReceivedCard()){ //딜러는 16점 이상이면 카드를 받을 수 없으므로 차례가 오면 16이상인지 확인합니다.
            this.cards.add(card); //16점 이하여서 카드를 받을 수 있을때
            this.showCards();
        }else{
            System.out.println("점수의 합이 17이상입니다. 카드를 받을 수 없습니다.");//16점 이상일때
            this.turnOff();//턴종료
            return;
        }
        int currentScore = getPointSum(); //현재 보유하고있는 카드의 점수를 합산하여 버스트인지 확인합니다.
        if (currentScore > 21) {
            System.out.println(Dealer.name + "는 버스트 했습니다! (점수: " + currentScore + ")");
            this.turnOff(); // 턴 자동 종료
        }
    }

    boolean isReceivedCard(){  //매직넘버를 방지하기 위한 메소드
        return getPointSum() <= Limit_Receive_Point;
    }

    public int getPointSum() {
        int sum = 0;
        int aceCount = 0;

        // 카드 점수 합산 및 Ace 개수 확인
        for (Card card : cards) {
            sum += card.getPoint();
            if (card.getPoint() == 11) {
                aceCount++;
            }
        }

        // 총 점수가 21을 초과할 경우 Ace의 점수를 1로 변경
        while (sum > 21 && aceCount > 0) {
            sum -= 10; // Ace를 11에서 1로 변경
            aceCount--;
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
        return Dealer.name;
    }
}