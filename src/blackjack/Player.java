package blackjack;
import java.util.List;
import java.io.PrintWriter;

/*
  user와 dealer가 중복된 메소드를 갖고 있으므로 player 인터페이스를 통해 관리, 현재 1명의 유저만
  접속하는것으로 코드가 짜여져있으나, 인터페이스를 통해 여러 유저를 관리 할 수 있음
 */

public interface Player {
    void receiveCard(Card card);
    void turnOn();
    void turnOff();
    boolean isTurn();
    int getPointSum();
    List<Card> openCards();
    void showCards(PrintWriter out); // showCards 메서드가 PrintWriter를 매개변수로 받음
    String getName();
}