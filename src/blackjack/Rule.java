package blackjack;

import java.util.ArrayList;
import java.util.List;

public class Rule {
    
    public int getScore(List<Card> cards){ // 카드 숫자의 합을 점수로 환산
        int score = 0;
        score = cardSum(cards); // dealer의 카드 합

        if ()
        return score;
    }
    public void getWinner(Dealer dealer, Gamer gamer){
        
    }

    public int cardSum(List<Card> cards){ // 플레이어의 카드를 21에 가까운 합으로 전환
        int sum = 0;
        for(int i = 0; i < cards.size(); i++){
            sum += getCard(cards).get(i);
        }
        return sum;
    }

    public List<Integer> getCard(List<Card> cards){ // 카드 리 숫자로 통일하기
        List<Integer> cardNo = new ArrayList<>();

        for(int i=0; i<cards.size(); i++){
            String denom = cards.get(i).getDenomination();

            if(denom.equals("A")){ // Ace 일때
                cardNo.add(11); // 우선 11로 간주
            } else if(denom.matches("K|Q|J")){
                cardNo.add(10);
            } else{ // 일반 숫자
                cardNo.add(Integer.parseInt(cards.get(i).getDenomination()));
            }
        }

        return cardNo;
    }
}
