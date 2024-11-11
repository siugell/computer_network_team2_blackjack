package blackjack;

import java.util.LinkedList;
import java.util.List;

public class CardDeck {
    private List<Card> cards;
    private static final String[] PATTERNS = {"spade", "club", "heart", "diamond"};
    private static final int CARD_COUNT = 13;

    public CardDeck(){
        cards = this.generateCards();
    }

    private List<Card> generateCards() { //카드 덱 생성하는 함수
        List<Card> cards = new LinkedList<>(); //Card를 linkedlist로 만들어 carddeck객체를 생성

        for (String pattern : PATTERNS){
            for (int i = 1; i <= CARD_COUNT; i++)
            {
                String denomination = this.numberToDenomination(i);
                Card card = new Card(pattern, denomination);
                cards.add(card); //Card 객체를 List<Card>에 추가
            }
        }
        return cards; //생성 완료된 cardDeck을 리턴
    }

    private String numberToDenomination(int num) //카드의 숫자를 string으로 넘겨주는 함수
    {
        if(num == 1) return "A";
        else if(num == 11) return "J";
        else if(num == 12) return "Q";
        else if(num == 13) return "K";
        return String.valueOf(num);
    }

    public Card draw(){
        Card selectedCard = this.getRandomCard();
        cards.remove(selectedCard); //뽑은 카드는 deck에서 지움
        return selectedCard;
    }

    private Card getRandomCard() {
        int size = cards.size();
        int select = (int)(Math.random()*size);  //card deck에서 랜덤하게 생성된 정수index 카드를 뽑아옴
        return cards.get(select);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for(Card card : cards){
            sb.append(card.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}