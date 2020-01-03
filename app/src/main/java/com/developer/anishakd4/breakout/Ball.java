package com.developer.anishakd4.breakout;

import android.graphics.RectF;

import java.util.Random;

public class Ball {

    private RectF rect;
    private float xVelocity;
    private float yVelocity;
    private float ballwidth = 10;
    private float ballHeight = 10;

    public Ball() {

        xVelocity = 200;
        yVelocity = -400;

        rect = new RectF();
    }

    public RectF getRect(){
        return rect;
    }

    public void update(long fps){
        rect.left = rect.left + (xVelocity/fps);
        rect.top = rect.top + (yVelocity/fps);
        rect.right = rect.left + ballwidth;
        rect.bottom = rect.top - ballHeight;
    }

    void reversexVelocity(){
        xVelocity = -xVelocity;
    }

    void reverseyVelocity(){
        yVelocity = -yVelocity;
    }

    void setRandomXVelocity(){
        Random random = new Random();
        int answer = random.nextInt(2);
        if(answer == 0){
            reversexVelocity();
        }
    }

    void clearObstacleY(float y){
        rect.bottom = y;
        rect.top = y - ballHeight;
    }

    void clearObstacleX(float x){
        rect.left = x;
        rect.right = x + ballwidth;
    }

    void reset(int x, int y){
        rect.left = x/2;
        rect.top = y - 20;
        rect.right = x / 2 + ballwidth;
        rect.bottom = y - 20 - ballHeight;
    }

}
