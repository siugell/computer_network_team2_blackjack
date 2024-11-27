package blackjack;

public class Card {
    private String pattern; //heart, spade등 카드 문양을 나타냄
    private String denomination; //2~10, A~K의 카드 숫자를 나타냄
    private int point;

    public Card(String pattern, int index) //카드의 index만 받아 card내에서 숫자와 점수를 매김
    {
        this.pattern = pattern;
        this.denomination = numberToDenomination(index);
        this.point = numberToPoint(index);
    }

    private String numberToDenomination(int num){

        if(num == 1){
            return "A";
        }else if(num == 11){
            return "J";
        }else if(num == 12){
            return "Q";
        }else if(num == 13){
            return "K";
        }

        return String.valueOf(num);
    }

    private int numberToPoint(int num) {
        if (num >= 11) {
            return 10;
        }else if(num == 1){
            return 11;
        }

        return num;
    }

    public boolean isAce(){
        if (this.denomination.equals('A')){
            return true;
        }
        return false;
    }

    public int getPoint(){
        return this.point;
    }

    public String getDenomination(){
        return this.denomination;
    }

    @Override
    public String toString(){
        return pattern + denomination;
    }
}
