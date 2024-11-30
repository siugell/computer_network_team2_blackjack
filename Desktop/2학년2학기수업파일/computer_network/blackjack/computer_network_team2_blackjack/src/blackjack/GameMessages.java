package blackjack;

public class GameMessages {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";

    public static final String GAME_START = "---------------- 블랙잭 시작! ----------------";
    //게임 승리 패배 관련 메시지
    public static final String PLAYER_WIN_BLACKJACK = ANSI_GREEN + "블랙잭으로 승리하셨습니다! 배팅 금액의 1.5배를 획득했습니다."+ ANSI_RESET;
    public static final String DEALER_ONLY_BLACKJACK = ANSI_RED + "딜러가 블랙잭입니다. 패배하셨습니다. 배팅 금액을 잃었습니다."+ ANSI_RESET;
    public static final String PLAYER_BURST_LOSS = ANSI_RED + "버스트되어 패배하셨습니다. 배팅 금액을 잃었습니다."+ ANSI_RESET;
    public static final String PLAYER_WIN = ANSI_GREEN + "승리하셨습니다! 배팅 금액의 2배를 획득했습니다." + ANSI_RESET;
    public static final String PLAYER_LOSS = ANSI_RED + "패배하셨습니다. 배팅 금액을 잃었습니다." + ANSI_RESET;

    //재시작 관련 메시지
    public static final String RESTART_PROMPT = "게임을 재시작하시겠습니까? (예: 1, 아니오: 0)";
    public static final String RESTART_YES = "새 게임을 시작합니다.";
    public static final String RESTART_NO = "게임을 종료합니다. 감사합니다!";
    public static final String INVALID_RESTART_INPUT = "잘못된 입력입니다. 다시 입력해주세요.";

    // 배팅 관련 메시지
    public static final String INVALID_BET = "소지금을 초과하거나 유효하지 않은 금액입니다. 다시 입력해주세요.";
    public static final String NUMBER_INPUT = "숫자를 입력해주세요.";

    //endgame관련 메시지
    public static final String END_GAME = "================= 게임종료 ==================";
    public static final String CARDSALLPLAYER = "-------------- 다른 유저의 카드 --------------";


    public static final String PROMPT_SEPARATOR = "--------------------------------------------";
    public static final String SEPARATOR = "============================================";
}
