package org.ctreatma.hello;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;

public class TurnBlueSquare extends Button {
    int color;
    
    public TurnBlueSquare(Context context) {
        super(context);
        
        this.setBackgroundColor(Color.BLUE);
    }
    
    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        this.color = color;
    }
    
    void changeColor() {
        if (this.color == Color.BLUE) {
            this.setBackgroundColor(Color.RED);
        }
        else {
            this.setBackgroundColor(Color.BLUE);
        }
    }
}
