package com.github.hanseter.TicTacToe;

import java.util.Arrays;
import java.util.List;
import java.util.stream.*;

/**
 * Hello world!
 *
 */
public class TicTacToe implements GameState {
    private static final char PLAYER_1_SYMBOL = 'X';
    private static final char PLAYER_2_SYMBOL = 'O';

    private final int fieldLength;
    private final int fieldSize;
    private final Character[] field;
    private final int toWin;
    private char nextPlayer = PLAYER_1_SYMBOL;
    private char winner = EMPTY_MARK;
    private int openFields;

    public TicTacToe(int fieldLength) {
        this.fieldLength = fieldLength;
        fieldSize = fieldLength * fieldLength;
        field = new Character[fieldSize];
        toWin = fieldLength;
        openFields = fieldSize;
    }

    public boolean place(int pos) {
        if (isDone()) return false;
        if (pos >= fieldSize || pos < 0) return false;
        if (field[pos] != null) return false;

        field[pos] = nextPlayer;
        determineWinner(pos, nextPlayer);
        nextPlayer = nextPlayer == PLAYER_1_SYMBOL ? PLAYER_2_SYMBOL : PLAYER_1_SYMBOL;
        openFields--;
        return true;
    }

    private void determineWinner(int updateField, char currentPlayer) {
        int row = updateField / fieldLength;
        int column = updateField % fieldLength;
        if (rowMatches(row, currentPlayer) || columnMatches(column, currentPlayer)
                || diagonalMatches(updateField, currentPlayer)) {
            winner = currentPlayer;
        }
    }

    private boolean rowMatches(int row, Character toCheck) {
        int rowStart = row * fieldLength;
        int rowEnd = (row + 1) * fieldLength;
        int currentStreak = 0;
        for (int i = rowStart; i < rowEnd; i++) {
            if (toCheck.equals(field[i])) {
                if (++currentStreak == toWin) {
                    return true;
                }
            } else {
                currentStreak = 0;
            }
        }
        return false;
    }

    private boolean columnMatches(int column, Character toCheck) {
        int currentStreak = 0;
        for (int i = column; i < fieldSize; i += fieldLength) {
            if (toCheck.equals(field[i])) {
                if (++currentStreak == toWin) {
                    return true;
                }
            } else {
                currentStreak = 0;
            }
        }
        return false;
    }

    private boolean diagonalMatches(int updateField, Character toCheck) {
        int column = updateField % fieldLength;
        int topLeft = IntStream.iterate(updateField, (it) -> it - fieldLength - 1)
                .takeWhile((it) -> it >= 0 && (it % fieldLength) <= column).reduce((a, b) -> b).orElse(updateField);
        int currentStreak = 0;
        for (int i = topLeft; i < fieldSize; i += fieldLength + 1) {
            if (toCheck.equals(field[i])) {
                if (++currentStreak == toWin) {
                    return true;
                }
            } else {
                currentStreak = 0;
            }
            if (i % fieldLength == fieldLength - 1) {
                break;
            }
        }
        int bottomLeft = IntStream.iterate(updateField, (it) -> it + fieldLength - 1)
                .takeWhile((it) -> it < fieldSize && (it % fieldLength) <= column).reduce((a, b) -> b)
                .orElse(updateField);
        currentStreak = 0;
        for (int i = bottomLeft; i > 0; i -= fieldLength - 1) {
            if (toCheck.equals(field[i])) {
                if (++currentStreak == toWin) {
                    return true;
                }
            } else {
                currentStreak = 0;
            }
            if (i % fieldLength == fieldLength - 1) {
                break;
            }
        }
        return false;
    }

    public boolean isDone() {
        return winner != EMPTY_MARK || openFields == 0;
    }

    public char getWinner() {
        return winner;
    }

    @Override
    public String toString() {
        // "―";
        String horBar = IntStream.range(0, fieldLength * 2).mapToObj(it -> "―").collect(Collectors.joining(""));
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < fieldLength; row++) {
            for (int column = 0; column < fieldLength; column++) {
                var elem = field[(row * fieldLength) + column];
                if (elem == null) {
                    sb.append(" ");
                } else {
                    sb.append(elem);
                }
                sb.append("|");
            }
            sb.setLength(sb.length() - 1);
            sb.append("\n");
            sb.append(horBar);
            sb.append("\n");
        }
        sb.setLength(sb.length() - horBar.length() - 1);
        return sb.toString();
    }

    @Override
    public List<Character> getPlayerMarks() {
        return Arrays.asList(PLAYER_1_SYMBOL, PLAYER_2_SYMBOL);
    }

    @Override
    public int getFieldLength() {
        return fieldLength;
    }

    @Override
    public List<Character> getField() {
        return Arrays.asList(field);
    }

    @Override
    public int getLengthToWin() {
        return toWin;
    }

    public static void main(String[] args) {
        var board = new TicTacToe(5);
        TicTacToeBotImpl bot = new TicTacToeBotImpl();
        while (!board.isDone()) {
            System.out.println(board);
            int pos = Integer.parseInt(System.console().readLine());
            board.place(pos);
            board.place(bot.place(PLAYER_2_SYMBOL, board));
        }
        System.out.println(board);
        System.out.println("Winner is: " + board.winner);
    }
}
