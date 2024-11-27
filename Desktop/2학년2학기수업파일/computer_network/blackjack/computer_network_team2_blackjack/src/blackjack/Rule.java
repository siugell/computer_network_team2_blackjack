package blackjack;

import java.util.ArrayList;
import java.util.List;

public class Rule {

    // 특정 플레이어의 카드 점수를 계산
    public int getScore(List<Card> cards) {
        int sum = 0;
        int aceCount = 0;

        // 카드 점수 합산
        for (Card card : cards) {
            sum += card.getPoint();
            if (card.isAce()) {
                aceCount++;
            }
        }

        // ACE 카드를 1점 또는 11점으로 계산
        while (aceCount > 0 && sum + 10 <= 21) {
            sum += 10;
            aceCount--;
        }

        return sum;
    }

    // 블랙잭 여부 확인 (처음 두 장의 카드로 블랙잭인지 체크)
    public boolean isBlackjack(Player player) {
        List<Card> cards = player.openCards();
        return cards.size() == 2 && player.getPointSum() == 21;
    }

    // 모든 플레이어 중 승자를 결정 (블랙잭 우선 처리 및 동점자 처리)
    public List<Player> getWinners(List<Player> players) {
        List<Player> blackjackWinners = new ArrayList<>();
        List<Player> regularWinners = new ArrayList<>();
        int highestPoint = 0;

        // 1. 블랙잭 승리자 찾기
        for (Player player : players) {
            if (isBlackjack(player)) {
                blackjackWinners.add(player);
            }
        }

        // 블랙잭 승리자가 있을 경우 해당 리스트 반환
        if (!blackjackWinners.isEmpty()) {
            return blackjackWinners;
        }

        // 2. 블랙잭이 없으면 최고 점수를 가진 일반 승리자 찾기
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
