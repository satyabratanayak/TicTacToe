import java.util.HashMap;
import java.util.Map;

public class Game {
    private static char[] BOARD = new char[9];
    private static int[][] WIN_CHANCES = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 },
            { 0, 4, 8 }, { 2, 4, 6 } };
    public static final char CROSS = 'X';
    public static final char CIRCLE = 'O';
    private static char lastPlayed = CROSS;
    private static Map<Integer, Integer> map = new HashMap<>();

    public Game() {
        resetBoard();
    }

    public void resetBoard() {
        for (int i = 0; i < BOARD.length; i++) {
            BOARD[i] = ' ';
        }
        lastPlayed = CROSS; // You can alternate who starts the game if you want
    }

    public String getBoard() {
        int count = 0;
        StringBuilder display = new StringBuilder("\n ");
        for (int i = 0; i < BOARD.length; i++) {
            if (count == 3) {
                display.append("\n--- --- --- \n ");
                count = 0;
            }
            display.append(BOARD[i]).append(" | ");
            count++;
        }
        return display.toString();
    }

    private boolean validateMove(char move, int position) {
        if (move == CROSS || move == CIRCLE) {
            if (BOARD[position] == CIRCLE || BOARD[position] == CROSS) {
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean isCurrentPlayerTurn(char move) {
        return move != lastPlayed;
    }

    public synchronized String makeMove(char move, int markedPosition) {
        if (!isCurrentPlayerTurn(move)) {
            return "~~ It's not your turn now! ~~";
        }
        Integer position = map.get(markedPosition);
        String data = "~~ Invalid Move! ~~";
        if (position != null) {
            if (validateMove(move, position)) {
                BOARD[position] = move;
                lastPlayed = move;
                if (checkWinner(move)) {
                    data = "+++ Congratulations! " + move + " wins! +++\n To play again, type 'restart'.";
                } else if (checkDraw()) {
                    data = "~~ Game Draw! ~~\n To play again, type 'restart'.";
                } else {
                    data = getBoard() + "\nMove Updated!\nNext move to be made by "
                            + (lastPlayed == CROSS ? CIRCLE : CROSS);
                }
            }
        }
        return data;
    }

    private boolean checkWinner(char move) {
        for (int[] winChance : WIN_CHANCES) {
            if (BOARD[winChance[0]] == move && BOARD[winChance[1]] == move && BOARD[winChance[2]] == move) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDraw() {
        for (char spot : BOARD) {
            if (spot == ' ') {
                return false;
            }
        }
        return true;
    }

    static {
        map.put(11, 0);
        map.put(12, 1);
        map.put(13, 2);
        map.put(21, 3);
        map.put(22, 4);
        map.put(23, 5);
        map.put(31, 6);
        map.put(32, 7);
        map.put(33, 8);
    }
}
