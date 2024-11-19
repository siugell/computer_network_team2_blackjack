package blackjack;

import java.util.ArrayList;
import java.util.List;

public class Rule {

    // 블랙잭 여부 확인 (처음 두 장의 카드로 블랙잭인지 체크)
    public boolean isBlackjack(Player player) {
        List<Card> cards = player.openCards();
        return cards.size() == 2 && player.getPointSum() == 21;
    }

    public List<Player> getBlackjackWinners(List<Player> players){
        List<Player> blackjackWinners = new ArrayList<>();
        // 블랙잭 승리자 찾고 해당 플레이어의 리스트를 반환
        for (Player player : players) {
            if (isBlackjack(player)) {
                blackjackWinners.add(player);
            }
        }
        return blackjackWinners;
    }
    // 모든 플레이어 중 승자를 결정 (동점자 처리)
    public List<Player> getWinners(List<Player> players) {
        List<Player> regularWinners = new ArrayList<>();
        int highestPoint = 0;


        //최고 점수를 가진 승리자 찾기
        for (Player player : players) {
            int playerPointSum = player.getPointSum();

            // 21점을 넘지 않는 범위에서 최고 점수인 플레이어 찾기
            if (playerPointSum > highestPoint && playerPointSum <= 21) {
                regularWinners.clear();
                regularWinners.add(player);
                highestPoint = playerPointSum;
            } else if (playerPointSum == highestPoint && playerPointSum <= 21) {
                regularWinners.add(player); // 동점자 추가
            }
        }

        return regularWinners;
    }
}
