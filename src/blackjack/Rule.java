package blackjack;

import java.util.ArrayList;
import java.util.List;

public class Rule {
    private int numPlayer = 3; // 플레이어 수
    private List<List<Integer>> participants = new ArrayList<>(); // 딜러를 포함한 전체 참가자

    /*
     * 참가자의 점수화된 값을 전달 받을 setter
     public void setParticipants(List<List<Integer>> participants) {
         this.participants = participants;
     }
     */

    

    /* 
    * 플레이어 수 입력받을 생성자
     private int numPlayer; // 플레이어 수

     public Rule(int n){ // 플레이어 수 초기화 하는 객체
         this.numPlayer = n;
     }
    */

    public void getWinner(Dealer dealer, Gamer gamer){ // 우승자 가려내기
        /*
         * Player No.
         * 0 -> Dealer
         * n -> Player n
         */
        
        int tmp = 0; // init: 현재 가장 가까운 Player
        int bestDiff = Integer.MAX_VALUE; // init: 21과 Player 최소 차이

        for (int i=0; i<(numPlayer+1); i++){

        }
    }
    
    public int getScore(List<Card> cards){ // 카드 숫자의 합을 점수로 환산
        int score = 0;
        score = cardSum(cards); // dealer의 카드 합

        return score;
    }

    public int cardSum(List<Card> cards){ // 합 구하기
        int sum = 0;
        for(int i = 0; i < cards.size(); i++){
            sum += getCard(cards).get(i);
        }
        return sum;
    }

    public List<Integer> getCard(List<Card> cards){ // 카드 리스트 각자 점수화
        List<Integer> cardNo = new ArrayList<>();

        for(int i=0; i<cards.size(); i++){
            String denom = cards.get(i).getDenomination();

            if(denom.equals("A")){ // Ace 일때
                cardNo.add(1); // 1로 고정
            } else if(denom.matches("K|Q|J")){
                cardNo.add(10);
            } else{ // 일반 숫자
                cardNo.add(Integer.parseInt(cards.get(i).getDenomination()));
            }
        }

        return cardNo;
    }

    
}

/*
 * import java.util.ArrayList;
import java.util.List;

public class BlackjackWinner {

    public static String findClosestTo21(List<List<Integer>> participants) {
        String tmp = null; // 현재 가장 가까운 사람
        int bestDifference = Integer.MAX_VALUE; // 21과의 최소 차이

        for (int i = 0; i < participants.size(); i++) {
            int score = calculateScore(participants.get(i));

            if (score > 21) {
                continue; // 버스트한 사람은 스킵
            }

            int difference = 21 - score; // 21과의 차이 계산

            // 차이가 더 작으면 tmp 업데이트
            if (difference < bestDifference) {
                bestDifference = difference;
                tmp = "Participant " + (i + 1); // 딜러는 Participant 1, 플레이어는 이후 번호
            }
        }

        return tmp != null ? tmp + " is the winner!" : "No winner! All participants busted.";
    }

    private static int calculateScore(List<Integer> cards) {
        int sum = 0;
        int aceCount = 0;

        // 카드 합산
        for (int card : cards) {
            if (card == 1) { // Ace
                aceCount++;
                sum += 11; // 임시로 11로 계산
            } else if (card > 10) { // J, Q, K
                sum += 10;
            } else {
                sum += card;
            }
        }

        // Ace 조정 (합계가 21을 초과하면 Ace 값을 1로 조정)
        while (sum > 21 && aceCount > 0) {
            sum -= 10;
            aceCount--;
        }

        return sum;
    }

    public static void main(String[] args) {
        // 테스트: 딜러 + 플레이어의 카드
        List<List<Integer>> participants = new ArrayList<>();
        participants.add(List.of(10, 7));      // Dealer: 17
        participants.add(List.of(10, 9, 2));  // Player 1: 21
        participants.add(List.of(10, 6, 7));  // Player 2: 23 (Bust)
        participants.add(List.of(8, 8, 3));   // Player 3: 19

        String result = findClosestTo21(participants);
        System.out.println(result); // 출력: "Participant 2 is the winner!"
    }
}

 */