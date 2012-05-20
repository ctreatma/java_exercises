package org.ctreatma.hello;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

// TODO:  Handle getting a parcelable that says what the level was, how many clicks, and what the colors were.
public class TurnBlueBoardView extends LinearLayout {
    private List<TurnBlueSquare> board;
    private TextView clickDisplay, levelDisplay;
    private int boardSize;
    private int level;
    private int lossDifferential;
    private int clicks;

    class TurnBlueListener implements OnClickListener {
        private int idx;

        TurnBlueListener(int idx) {
            this.idx = idx;
        }
        public void onClick(View v) {
            clicks++;
            clickDisplay.setText("Clicks: " + clicks);
            updateBoard(idx);
            checkCompleted();
        }      
    }

    public TurnBlueBoardView(Context context) {
        this(context, null);
    }

    public TurnBlueBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setOrientation(VERTICAL);

        level = Integer.valueOf(context.getString(R.string.starting_level));
        lossDifferential = Integer.valueOf(context.getString(R.string.loss_differential));
        boardSize = Integer.valueOf(context.getString(R.string.board_size));

        clickDisplay = new TextView(context);
        levelDisplay = new TextView(context);

        board = new ArrayList<TurnBlueSquare>();

        this.addView(levelDisplay);

        for (int i = 0; i < boardSize; ++i) {
            LinearLayout tableRow = new LinearLayout(context);
            tableRow.setOrientation(HORIZONTAL);


            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(25, 25, 1.0f);
            lp.setMargins(1, 1, 1, 1);

            for (int j = 0; j < boardSize; ++j) {
                TurnBlueSquare square = new TurnBlueSquare(context);

                board.add(square);
                tableRow.addView(square, lp);

                square.setClickable(true);
                square.setOnClickListener(new TurnBlueListener(board.indexOf(square)));
            }

            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            this.addView(tableRow,lp);
        }

        this.addView(clickDisplay);

        initializeBoard();
    }

    private void initializeBoard() {
        List<TurnBlueSquare> clicked = new ArrayList<TurnBlueSquare>();

        // Click random squares to set up the board for play
        while (clicked.size() < level) {
            int index = (int) Math.floor(Math.random()*(board.size()));
            TurnBlueSquare target = board.get(index);
            if (clicked.contains(target)) continue;
            clicked.add(target);
            updateBoard(index);
        }

        clicks = 0; // Reset click counter
        clickDisplay.setText("Clicks: " + clicks);
        levelDisplay.setText("Level: " + level);
    }

    private void resetBoard() {
        level = 1;
        for (int i = 0; i < board.size(); ++i) {
            TurnBlueSquare square = board.get(i);
            square.setBackgroundColor(Color.BLUE);
        }
        initializeBoard();
    }

    private void checkCompleted() {
        for (TurnBlueSquare square : board) {
            if (square.color != Color.BLUE) {               
                if (clicks > level + lossDifferential) {
                    resetBoard();
                }
                return;
            }
        }
        level++;
        initializeBoard();
    }

    private void updateBoard(int idx) {
        // Change the color of the clicked square
        board.get(idx).changeColor();
        // Click the squares above and below
        if (idx >= boardSize)
            board.get(idx-boardSize).changeColor();
        if (idx < (boardSize * (boardSize - 1)))
            board.get(idx+boardSize).changeColor();
        // If the clicked square is not on the left
        // edge, click the square to the left
        if (idx % boardSize > 0)
            board.get(idx-1).changeColor();
        // If the clicked square is not on the right
        // edge, click the square to the right
        if (idx % boardSize < boardSize - 1)
            board.get(idx+1).changeColor();
    }
}
