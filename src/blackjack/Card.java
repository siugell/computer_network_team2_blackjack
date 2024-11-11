package blackjack;

public class Card {
    private String pattern; //heart, spade등 카드 문양을 나타냄
    private String denomination; //2~10, A~K의 카드 숫자를 나타냄
    private int point;

    public Card(String pattern, String denomination)
    {
        this.pattern = pattern;
        this.denomination = denomination;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    @Override
    public String toString(){
        return "Card{" + "pattern='" + pattern + ", denomination='" + denomination + '}';
    }
}
