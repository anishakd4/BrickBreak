package com.developer.anishakd4.breakout;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class BreakoutEngine extends SurfaceView implements Runnable {

    Context context;
    private Thread gameThread;
    private SurfaceHolder ourholder;
    private volatile boolean playing;
    private boolean paused = true;
    private Canvas canvas;
    private Paint paint;
    private int screenx;
    private int screeny;
    private long fps;
    private long timeThisFrame;

    Bat bat;
    Ball ball;
    Brick[] bricks = new Brick[200];
    int numBricks = 0;

    SoundPool soundPool;
    int beep1Id = -1;
    int beep2Id = -1;
    int beep3Id = -1;
    int loseLifeId = -1;
    int explodeId = -1;

    int score = 0;
    int lives = 3;

    public BreakoutEngine(Context context, int height, int width) {
        super(context);
        this.context = context;
        ourholder = getHolder();
        paint = new Paint();
        screenx = width;
        screeny = height;

        bat = new Bat(screenx, screeny);

        ball = new Ball();
        restart();
        setupSoundPool();
    }

    void setupSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(10)
                .build();

        try {

            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("beep1.ogg");
            beep1Id = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("beep2.ogg");
            beep2Id = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("beep3.ogg");
            beep3Id = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("loseLife.ogg");
            loseLifeId = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("explode.ogg");
            explodeId = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            Log.i("Crash", "Crash");
        }
    }

    public void onPause(){
        playing = false;
        try{
            gameThread.join();
        }catch (Exception e){

        }
    }

    public void onResume(){
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    @Override
    public void run() {
        while (playing){
            long startFrameTime = System.currentTimeMillis();
            if(!paused){
                update();
            }
            draw();
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if(timeThisFrame >= 1){
                fps = 1000/timeThisFrame;
            }
        }
    }

    private void update(){
        bat.update(fps);
        ball.update(fps);
        handleCollision();
    }

    void handleCollision(){
        checkBrickBallCollision();
        checkballBatCollision();
        checkBallScreenEdgeCollision();
    }

    void checkBrickBallCollision(){
        for(int i=0; i<numBricks; i++){
            if(bricks[i].getVisibility()){
                if(RectF.intersects(bricks[i].getRect(), ball.getRect())){
                    bricks[i].setInvisible();
                    ball.reverseyVelocity();
                    score = score + 10;
                    soundPool.play(explodeId, 1, 1, 0 , 0 , 1);
                }
            }
        }
    }

    void checkballBatCollision(){
        if(RectF.intersects(bat.getRect(), ball.getRect())){
            ball.setRandomXVelocity();
            ball.reverseyVelocity();
            ball.clearObstacleY(bat.getRect().top - 2);
            soundPool.play(beep1Id, 1, 1, 0, 0, 1);
        }
    }

    void checkBallScreenEdgeCollision(){
        if(ball.getRect().bottom > screeny){
            ball.reverseyVelocity();
            ball.clearObstacleY(screeny - 2);

            lives--;
            soundPool.play(loseLifeId, 1,1, 0, 0, 1);
            if(lives == 0){
                paused = true;
                restart();
            }
        }

        if(ball.getRect().top < 0){
            ball.reverseyVelocity();
            ball.clearObstacleY(12);
            soundPool.play(beep2Id, 1, 1, 0, 0, 1);
        }

        if(ball.getRect().left < 0){
            ball.reversexVelocity();
            ball.clearObstacleX(2);
            soundPool.play(beep3Id, 1, 1, 0, 0, 1);
        }

        if(ball.getRect().right > screenx - 10){
            ball.reversexVelocity();
            ball.clearObstacleX(screenx - 22);
            soundPool.play(beep3Id, 1, 1, 0, 0 , 1);
        }

        if(score == numBricks * 10){
            paused = true;
            restart();
        }
    }

    void restart(){
        ball.reset(screenx, screeny);
        int brickWidth = screenx / 8;
        int brickHeight = screeny / 10;
        numBricks=0;
        for(int column = 0; column<8; column++){
            for(int row = 0; row<3; row++){
                bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                numBricks++;
            }
        }
        score = 0;
        lives = 3;
    }

    private void draw(){
        if(ourholder.getSurface().isValid()){
            canvas = ourholder.lockCanvas();
            canvas.drawColor(Color.argb(255, 26, 128, 182));

            paint.setColor(Color.argb(255, 255, 255, 255));

            canvas.drawRect(bat.getRect(), paint);

            canvas.drawRect(ball.getRect(), paint);

            paint.setColor(Color.argb(255, 249, 129, 0));
            for(int i=0; i<numBricks; i++){
                if(bricks[i].getVisibility()){
                    canvas.drawRect(bricks[i].getRect(), paint);
                }
            }

            paint.setColor(Color.argb(255, 255, 255, 255));
            paint.setTextSize(70);
            canvas.drawText("Score: " + score + "     Lives: " + lives , 10, 80, paint);

            ourholder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                paused = false;
                if(event.getX() > screenx/2){
                    bat.setMovementState(bat.RIGHT);
                }else {
                    bat.setMovementState(bat.LEFT);
                }
                break;
            case MotionEvent.ACTION_UP:
                bat.setMovementState(bat.STOPPED);
                break;
        }

        return true;
    }
}
