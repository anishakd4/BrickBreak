package com.developer.anishakd4.breakout;

import android.graphics.RectF;

public class Bat {

    private RectF rectF;
    private float length;
    private float x;
    private float paddleSpeed;
    final int STOPPED = 0;
    final int LEFT = 1;
    final int RIGHT = 2;
    private int paddleMoving = STOPPED;


    Bat(int screenx, int screeny){
        length = 130;
        float height = 20;

        x = screenx / 2;
        float y = screeny - 20;
        rectF = new RectF(x, y, x + length, y+height);
        paddleSpeed = 350;
    }

    RectF getRect(){
        return  rectF;
    }

    void setMovementState(int state){
        paddleMoving = state;
    }

    void update(long fps){
        if(paddleMoving == LEFT){
            x = x - paddleSpeed/fps;
        }

        if(paddleMoving == RIGHT){
            x = x + paddleSpeed/fps;
        }

        rectF.left = x;
        rectF.right = x + length;
    }
}
