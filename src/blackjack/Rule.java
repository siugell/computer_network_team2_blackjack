package blackjack;

import java.util.ArrayList;
import java.util.List;

public class Rule {
    // private int numPlayer = 3; // 플레이어 수
    private List<Gamer> participants = new ArrayList<>(); // 딜러를 포함한 전체 참가자

    // 딜러와 플레이어 추가
    public void addParticipants(Dealer dealer, List<Gamer> gamers) {
        participants.add(dealer); // 딜러 추가
        participants.addAll(gamers); // 플레이어 추가
    }

    // 우승자 판별
    public void getWinner(Dealer dealer, Gamer gamer){ 
        
        int bestDiff = Integer.MAX_VALUE; // init: 21과 Player 최소 차이
        List<String> winners = new ArrayList<>(); // init: 동점자를 고려하여 list로 작성
        boolean hasBlackjack = false;

        for (int i=0; i<participants.size(); i++){
            Gamer participant = participants.get(i);
            int score = cardSum(participant.openCards());

            // burst 참가자 제외
            if (score > 21){
                continue; 
            }

            int difference = 21 - score;
            String currentPlayer = (i == 0) ? "Dealer" : "Player " +i; // Player i

            // blackjack case
            if (difference == 0){
                if (!hasBlackjack) { // first blackjack
                    winners.clear();
                    hasBlackjack = true;
                }
                winners.add(currentPlayer);
                continue;
            }

            // blackjack이 없는 경우
            if (!hasBlackjack){
                if (difference < bestDiff){ // 단독 승자 갱신
                    bestDiff = difference;
                    winners.clear();
                    winners.add(currentPlayer);
                } else if (difference == bestDiff) // 동점자 추가
                    winners.add(currentPlayer);
            } 
        }

        // 결과 출력
        if (winners.isEmpty()){ // 승자 X
            System.out.println("No Winner!");
        } else if (winners.size() == 1){ // 단독승자
            if (hasBlackjack) // 블랙잭 단독승
                System.out.println(winners.get(0) + "got the BLACKJACK!");
            else { // 일반 단독승
                System.out.println("Winner: " + winners.get(0));
            }
        } else { // 무승부
            System.out.println("Winner: " + String.join(", ", winners));
        }
    }

    // 점수 구하기
    public int cardSum(List<Card> cards){
        int sum = 0;
        for(int i=0; i<cards.size(); i++){
            String denom = cards.get(i).getDenomination();

            if(denom.equals("A")){ // Ace 일때
                sum += 1; // 1로 고정
            } else if(denom.matches("K|Q|J")){
                sum += 10;
            } else{ // 일반 숫자
                sum += Integer.parseInt(denom);
            }
        }
        
        return sum;
    }

}
