# computer_network_team2_blackjack

컴퓨터네트워크 2024
blackjack game project

2024-11-13 21:40 블랙잭 전반적인 구현 완료
2024-11-19 
- rule클래스에서 승리처리하는 메소드를 블랙잭 승리와 일반 승리로 구분하여 작성
- play클래스에서 startphase가 끝나면 블랙잭 승리를 처리하는 로직 추가
- 주석추가
- startphase에서 딜러의 점수가 17이 넘으면 차례를 종료하는 로직 추가
  
### 추가로 구현해야할 점

- 유저 이름이나 소지금 구현(client에서 구현 예정)
- network환경 구축(진행중, dealine 11/26)

### issue
- ~~유저가 한번도 카드를 뽑지않고 차례를 끝내면 dealer의 차례가 무한루프돌게됨~~ 11/19 issue solved
