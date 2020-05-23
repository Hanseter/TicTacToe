package com.github.hanseter.TicTacToe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TicTacToeBotImpl implements TicTacToeBot {
    @Override
    public int place(char myMark, GameState state) {
        List<Character> field = state.getField();
        int myWin = findFieldToWin(field, state.getFieldLength(), myMark, state.getLengthToWin());
        if (myWin != -1) {
            return myWin;
        }
        for (var mark : state.getPlayerMarks()) {
            if (!mark.equals(myMark)) {
                int otherWin = findFieldToWin(field, state.getFieldLength(), mark, state.getLengthToWin());
                if (otherWin != -1) {
                    return otherWin;
                }
            }
        }
        int middleField = field.size()/2;
        if (field.get(middleField) == null) {
            return middleField;
        }
        int corner = findEmptyCorner(field, state.getFieldLength());
        if (corner != -1)
            return corner;
        for (int i = 0; i < field.size(); i++) {
            if (field.get(i) == null)
                return i;
        }
        return -1;
    }

    private int findFieldToWin(List<Character> field, int fieldSize, char toCheck, int toWin) {
        for (int i = 0; i < fieldSize; i++) {
            int pos = findWinningField(field.subList(i * fieldSize, (i + 1) * fieldSize), toCheck, toWin);
            if (pos != -1)
                return i * fieldSize + pos;
            pos = findWinningField(IntStream.iterate(i, it -> it + fieldSize).limit(fieldSize)
                    .mapToObj(it -> field.get(it)).collect(Collectors.toList()), toCheck, toWin);
            if (pos != -1)
                return i + (fieldSize * pos);
        }
        int startOfLastRow = fieldSize * fieldSize - fieldSize;
        for (int i = 0; i < fieldSize; i++) {
            int top = i;
            int left = i * fieldSize;
            int bottom = startOfLastRow + i;
            List<Character> topLeftToBottomRight = walkBottomRight(top, fieldSize).stream().map(it -> field.get(it))
                    .collect(Collectors.toList());
            List<Character> leftTopToBottomRight = walkBottomRight(left, fieldSize).stream().map(it -> field.get(it))
                    .collect(Collectors.toList());
            List<Character> bottomLeftToTopRight = walkTopRight(bottom, fieldSize).stream().map(it -> field.get(it))
                    .collect(Collectors.toList());
            List<Character> leftBottomToTopRight = walkTopRight(left, fieldSize).stream().map(it -> field.get(it))
                    .collect(Collectors.toList());
            int pos = findWinningField(topLeftToBottomRight, toCheck, toWin);
            if (pos != -1)
                return top + pos * (fieldSize + 1);
            pos = findWinningField(leftTopToBottomRight, toCheck, toWin);
            if (pos != -1)
                return left + pos * (fieldSize + 1);
            pos = findWinningField(bottomLeftToTopRight, toCheck, toWin);
            if (pos != -1)
                return bottom + pos * (-fieldSize + 1);
            pos = findWinningField(leftBottomToTopRight, toCheck, toWin);
            if (pos != -1)
                return left + pos * (-fieldSize + 1);
        }
        return -1;
    }

    private List<Integer> walkBottomRight(int start, int fieldSize) {
        int lastField = fieldSize * fieldSize;
        int bottomRightVector = fieldSize + 1;
        List<Integer> ret = new ArrayList<>();
        ret.add(start);
        int currentIndex = start + bottomRightVector;
        while (currentIndex < lastField) {
            ret.add(currentIndex);
            if (currentIndex % fieldSize == fieldSize - 1) {
                break;
            }
            currentIndex+=bottomRightVector;
        }
        return ret;
    }

    private List<Integer> walkTopRight(int start, int fieldSize) {
        int topRightVector = -fieldSize + 1;
        List<Integer> ret = new ArrayList<>();
        ret.add(start);
        int currentIndex = start + topRightVector;
        while (currentIndex >= 0) {
            ret.add(currentIndex);
            if (currentIndex % fieldSize == fieldSize - 1) {
                break;
            }
            currentIndex+=topRightVector;
        }
        return ret;
    }

    private int findWinningField(List<Character> field, Character toCheck, int toWin) {
        var currentStreak = 0;
        for (int i = 0; i < field.size(); i++) {
            if (toCheck.equals(field.get(i))) {
                if (++currentStreak == toWin - 1) {
                    if (i - toWin + 1 > -1 && field.get(i - toWin + 1) == null) {
                        return i - toWin + 1;
                    }
                    if (i + 1 < field.size() && field.get(i + 1) == null) {
                        return i + 1;
                    }
                    currentStreak = 0;
                }
            } else {
                currentStreak = 0;
            }
        }
        return -1;
    }

    private int findEmptyCorner(List<Character> field, int fieldLength) {
        if (field.get(0) == null)
            return 0;
        if (field.get(fieldLength - 1) == null)
            return fieldLength - 1;
        int lastField = fieldLength * fieldLength - 1;
        if (field.get(lastField) == null)
            return lastField;
        if (field.get(lastField - fieldLength) == null)
            return lastField - fieldLength;
        return -1;
    }
}