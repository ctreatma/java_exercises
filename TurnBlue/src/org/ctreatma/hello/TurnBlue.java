package org.ctreatma.hello;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;

public class TurnBlue extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TurnBlueBoardView board = new TurnBlueBoardView(this);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        board.setGravity(Gravity.CENTER);
        
        this.addContentView(board,lp);
    }

}
