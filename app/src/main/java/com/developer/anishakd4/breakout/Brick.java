package com.developer.anishakd4.breakout;

import android.graphics.RectF;

public class Brick {

    private RectF rect;
    private boolean isvisible;

    Brick(int row, int column, int width, int height){
        isvisible = true;
        int padding = 1;
        rect = new RectF(column*width + padding,
                row*height + padding,
                column*width + width - padding,
                row*height + height - padding);
    }

    RectF getRect(){
        return rect;
    }

    void setInvisible(){
        isvisible = false;
    }

    boolean getVisibility(){
        return isvisible;
    }
}
