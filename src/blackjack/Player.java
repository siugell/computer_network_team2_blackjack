package blackjack;
import java.util.List;

/*
  user와 dealer가 중복된 메소드를 갖고 있으므로 player 인터페이스를 통해 관리, 현재 1명의 유저만
  접속하는것으로 코드가 짜여져있으나, 인터페이스를 통해 여러 유저를 관리 할 수 있음
 */

public interface Player {
    void receiveCard(Card card);

    void showCards();

    List<Card> openCards();

    void turnOff();

    void turnOn();

    boolean isTurn();

    String getName();

    int getPointSum();
}
