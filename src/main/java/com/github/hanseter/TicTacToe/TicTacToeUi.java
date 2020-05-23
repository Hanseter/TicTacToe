package com.github.hanseter.TicTacToe;

import javafx.application.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class TicTacToeUi extends Application {

    private TicTacToe board = new TicTacToe(3);
    private final TicTacToeBot bot = new TicTacToeBotImpl();

    @Override
    public void start(Stage primaryStage) throws Exception {
        // primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(visualizeField(board), 400, 400));
        primaryStage.show();
    }

    private Group visualizeField(TicTacToe board) {
        Group ret = new Group();
        int length = board.getFieldLength() * 100;
        Rectangle background = new Rectangle(length, length);
        background.setFill(Color.WHITE);
        ret.getChildren().add(background);
        for (int i = 1; i < board.getFieldLength(); i++) {
            var horBar = new Polyline(0, i * 100, length, i * 100);
            var vertBar = new Polyline(i * 100, 0, i * 100, length);
            ret.getChildren().addAll(horBar, vertBar);
        }
        ret.setOnMouseClicked(e -> onFieldClicked(e, ret, board));
        return ret;
    }

    private void onFieldClicked(MouseEvent e, Group group, TicTacToe board) {
        int row = (int) e.getY() / 100;
        int column = (int) e.getX() / 100;
        int fieldIndex = column + row * board.getFieldLength();
        if (!board.place(fieldIndex))
            return;
        updateField(group, row, column, board.getField().get(fieldIndex));
        int pos = bot.place('O', board);
        if (board.place(pos))
            updateField(group, pos / board.getFieldLength(), pos % board.getFieldLength(), 'O');
    }

    private void updateField(Group group, int row, int column, Character mark) {
        Color color = mark.equals('X') ? Color.RED : Color.BLUE;
        group.getChildren().add(new Circle(column * 100 + 50, row * 100 + 50, 40, color));
    }

    public static void main(String[] args) {
        Application.launch(TicTacToeUi.class, args);
    }
}