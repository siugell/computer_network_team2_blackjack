package blackjack;

import java.util.LinkedList;
import java.util.List;

/*
    카드를 받는다, 받은 카드를 소유한다. 카드를 오픈한다.

 */

public class Gamer implements Player{
    private List<Card> cards; //user가 소유하고있는 카드들, 손패
    private boolean turn;
    private String name;

    public Gamer(String name) {
        // cards 리스트를 초기화
        this.cards = new LinkedList<>();  // 또는 LinkedList<Card>로 초기화할 수도 있음
        this.name = name;
    }

    @Override
    public void receiveCard(Card card) {
        this.cards.add(card); //뽑은 카드를 유저 손패에 추가
        this.showCards(); //카드를 받을 때 마다 손패를 확인

        int currentScore = getPointSum();
        if (currentScore > 21) {
            System.out.println(this.name + "님이 버스트 했습니다! (점수: " + currentScore + ")");
            this.turnOff(); // 턴 자동 종료
        }
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
    public List<Card> openCards(){  //유저의 손패를 전달
        return this.cards;
    } //플레이어의 손패를 return

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