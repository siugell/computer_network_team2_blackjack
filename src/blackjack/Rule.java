package blackjack;

import java.util.List;

public class Rule {

    // 특정 플레이어의 카드 점수를 계산
    public int getScore(List<Card> cards) {
        int sum = 0;

        // 카드 점수 합산 (ACE는 항상 1점으로 계산)
        for (Card card : cards) {
            sum += card.getPoint();
        }

        return sum;
    }

    // 모든 플레이어 중 승자를 결정
    public Player getWinner(List<Player> players) {
        Player highestPlayer = null;
        int highestPoint = 0;

        for (Player player : players) {
            int playerPointSum = getScore(player.openCards());

            // 21점을 넘지 않는 범위에서 최고 점수인 플레이어 찾기
            if (playerPointSum > highestPoint && playerPointSum <= 21) {
                highestPlayer = player;
                highestPoint = playerPointSum;
            }
        }

        return highestPlayer;
    }
}
