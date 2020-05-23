package com.github.hanseter.TicTacToe;

import java.util.List;

public interface GameState {
    public static final char EMPTY_MARK = ' ';
    List<Character> getPlayerMarks();
    int getFieldLength();
    int getLengthToWin();
    List<Character> getField();
}