package blackjack;
import java.util.Scanner;

public class play{

    public void play() {
        System.out.println("========= Blackjack =========");
        Scanner sc = new Scanner(System.in);

        Dealer dealer = new Dealer();
        Gamer gamer = new Gamer();
        Rule rule = new Rule();
        CardDeck cardDeck = new CardDeck();

        System.out.println(cardDeck.toString());
    }
}
