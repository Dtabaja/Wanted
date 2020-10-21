package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Random;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class FiveLanesGameScreenActivity extends AppCompatActivity implements View.OnTouchListener, SensorEventListener {

    private RelativeLayout relativeLayout;
    private int numOfLanes = 5, rand, newRand, score, prizeRand, prizeLastRand;
    private Handler handler = new Handler();
    private ObjectAnimator animate1Y, animate2Y, animate3Y, animate4Y, animate5Y, prizeAnim1, prizeAnim2, prizeAnim3, prizeAnim4, prizeAnim5;
    private Button leftBtn, rightBtn;
    private boolean moveLeft, moveRight, gameOver = false, ifPlaying = true, vib, tilt, prizeCollision = false;
    private View carView, life1, life2, life3;
    private TextView scoreView, timerView;
    private Vibrator vibrator;
    private SensorManager sensorManager;
    private Sensor sensor;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUIVisibility();
        setContentView(R.layout.activity_five_lanes_game_screen);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        savedInstanceState = getIntent().getExtras();
        if(savedInstanceState != null){
            vib = savedInstanceState.getBoolean("vib");
            tilt = savedInstanceState.getBoolean("tilt");
        }
        else{
            vib = true;
            tilt = false;
        }

        relativeLayout = findViewById(R.id.gameLayout);
        leftBtn = findViewById(R.id.leftBtn);
        rightBtn = findViewById(R.id.rightBtn);
        life1 = findViewById(R.id.life1);
        life2 = findViewById(R.id.life2);
        life3 = findViewById(R.id.life3);
        scoreView = findViewById(R.id.score);

        leftBtn.setOnTouchListener(this);
        rightBtn.setOnTouchListener(this);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if(tilt == true){
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, sensor, sensorManager.SENSOR_DELAY_NORMAL);
            rightBtn.setVisibility(View.GONE);
            leftBtn.setVisibility(View.GONE);
        }

        relativeLayout.post(new Runnable() {
            @Override
            public void run() {
                // Init the car view to the screen.
                carView = new View(FiveLanesGameScreenActivity.this);
                carView.setLayoutParams(new RelativeLayout.LayoutParams(relativeLayout.getWidth() / numOfLanes, 250));
                carView.setBackgroundResource(R.drawable.car);
                carView.setX(relativeLayout.getWidth() / numOfLanes * 2);
                carView.setY(relativeLayout.getHeight() - 250);
                relativeLayout.addView(carView);

                // Init a temporary text view for the timer.
                timerView = new TextView((FiveLanesGameScreenActivity.this));
                timerView.setLayoutParams(new RelativeLayout.LayoutParams(relativeLayout.getWidth() / numOfLanes, 300));
                timerView.setTextSize(80);
                timerView.setTextColor(Color.WHITE);
                timerView.setGravity(Gravity.CENTER);
                timerView.setX(relativeLayout.getWidth() / numOfLanes * 2);
                timerView.setY(relativeLayout.getHeight() / 2 - 300);
                relativeLayout.addView(timerView);
            }
        });

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new CountDownTimer(4000, 1000) {
                                public void onTick(long sec) {
                                    timerView.setText("" + sec / 1000);
                                }

                                @Override
                                public void onFinish() {
                                    ((ViewGroup) timerView.getParent()).removeView(timerView);
                                }
                            }.start();
                        }
                    });
                    Thread.sleep(3500);

                    rand = new Random().nextInt(numOfLanes);
                    prizeLastRand = new Random().nextInt(numOfLanes);
                    while (!gameOver) {
                        newRand = generateRand(rand);
                        do{
                            prizeRand = generateRand(prizeLastRand);
                        }while(prizeRand == newRand);
                        gameLoop();
                        prizeLoop();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (ifPlaying) {
                                    score = Integer.parseInt(scoreView.getText().toString());
                                    if(prizeCollision){
                                        score += 50;
                                    }
                                    score += 10;
                                    scoreView.setText("" + score);
                                    prizeCollision = false;
                                }
                            }
                        }, 1500);
                        rand = newRand;
                        prizeLastRand = prizeRand;
                        try {
                            Thread.sleep(1000);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startGameOverActivity();
                    finish();
                }
            }
        }).start();

    }

    // Generate different random number from the last one.
    public int generateRand(int lastRand) {
        int randNum;
        do{
            randNum = new Random().nextInt(numOfLanes);
        }while(lastRand == randNum);
        return randNum;
    }

    // Key touch events.
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            switch (view.getId()) {
                case R.id.leftBtn:
                    moveLeft = true;
                    break;
                case R.id.rightBtn:
                    moveRight = true;
                    break;
            }
        } else {
            moveLeft = false;
            moveRight = false;
        }
        view.post(new Runnable() {
            @Override
            public void run() {
                changePos();
            }
        });

        return false;
    }

    // Change position of the user.
    public void changePos() {
        float carViewX = carView.getX();
        if (moveLeft) {
            carViewX -= (relativeLayout.getWidth() / numOfLanes);

        } else if (moveRight) {
            carViewX += (relativeLayout.getWidth() / numOfLanes);
        }

        if (carViewX < 0) {
            carViewX = 0;
        }
        if (carViewX > relativeLayout.getWidth() - carView.getWidth()) {
            carViewX = relativeLayout.getWidth() - carView.getWidth();
        }
        carView.setX(carViewX);

    }

    // Collision detection.
    public boolean collisionDetection(View view, int i) {
        int viewX = (int) view.getX();
        int viewY = (int) view.getY();
        int viewRightTop = view.getWidth() + viewX;
        int viewRightBottom = view.getHeight() + viewY;
        int carViewX = (int) carView.getX();
        int carViewY = (int) carView.getY();

        if (carViewX >= viewX && carViewX < viewRightTop && carViewY >= viewY && carViewY <= viewRightBottom && ifPlaying) {
            if(i == 0){
                if (life3.getVisibility() == VISIBLE) {
                    life3.setVisibility(INVISIBLE);
                    if(vib == true){
                        vibrator.vibrate(300);
                    }
                    return true;
                } else {
                    if (life2.getVisibility() == VISIBLE) {
                        life2.setVisibility(INVISIBLE);
                        if(vib == true){
                            vibrator.vibrate(300);
                        }
                        return true;
                    } else {
                        life1.setVisibility(INVISIBLE);
                        if(vib == true){
                            vibrator.vibrate(500);
                        }
                        gameOver = true;
                        return true;
                    }
                }
            }else{
                prizeCollision = true;
                return true;
            }

        }
        return false;
    }

    // Game loop.
    public void gameLoop() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (newRand == 0) {
                    final View leftView = new View(FiveLanesGameScreenActivity.this);
                    leftView.setLayoutParams(new RelativeLayout.LayoutParams(relativeLayout.getWidth() / numOfLanes, 250));
                    leftView.setBackgroundResource(R.drawable.police_png);
                    relativeLayout.addView(leftView);
                    leftView.setX(newRand * (relativeLayout.getWidth() / numOfLanes));
                    animate1Y = ObjectAnimator.ofFloat(leftView, "translationY", 0f, relativeLayout.getHeight() + 250);
                    animate1Y.setDuration(2500);

                    leftView.post(new Runnable() {
                        @Override
                        public void run() {
                            animate1Y.start();
                            animate1Y.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    if (collisionDetection(leftView, 0)) {
                                        animate1Y.cancel();
                                    }
                                    if(gameOver){
                                        animate1Y.pause();
                                    }
                                }
                            });

                            animate1Y.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    ((ViewGroup) leftView.getParent()).removeView(leftView);
                                }
                            });

                        }
                    });
                } else if (newRand == 1) {
                    final View centerLeftView = new View(FiveLanesGameScreenActivity.this);
                    centerLeftView.setLayoutParams(new RelativeLayout.LayoutParams(relativeLayout.getWidth() / numOfLanes, 250));
                    centerLeftView.setBackgroundResource(R.drawable.police_png);
                    relativeLayout.addView(centerLeftView);
                    centerLeftView.setX(newRand * (relativeLayout.getWidth() / numOfLanes));
                    animate2Y = ObjectAnimator.ofFloat(centerLeftView, "translationY", 0f, relativeLayout.getHeight() + 250);
                    animate2Y.setDuration(2500);
                    centerLeftView.post(new Runnable() {
                        @Override
                        public void run() {
                            animate2Y.start();
                            animate2Y.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    if (collisionDetection(centerLeftView, 0)) {
                                        animate2Y.cancel();
                                    }
                                    if(gameOver){
                                        animate2Y.pause();
                                    }
                                }
                            });
                            animate2Y.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    ((ViewGroup) centerLeftView.getParent()).removeView(centerLeftView);
                                }
                            });
                        }
                    });
                } else if (newRand == 3) {
                    final View centerView = new View(FiveLanesGameScreenActivity.this);
                    centerView.setLayoutParams(new RelativeLayout.LayoutParams(relativeLayout.getWidth() / numOfLanes, 250));
                    centerView.setBackgroundResource(R.drawable.police_png);
                    relativeLayout.addView(centerView);
                    centerView.setX(newRand * (relativeLayout.getWidth() / numOfLanes));
                    animate3Y = ObjectAnimator.ofFloat(centerView, "translationY", 0f, relativeLayout.getHeight() + 250);
                    animate3Y.setDuration(2500);
                    centerView.post(new Runnable() {
                        @Override
                        public void run() {
                            animate3Y.start();
                            animate3Y.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    if (collisionDetection(centerView, 0)) {
                                        animate3Y.cancel();
                                    }
                                    if (gameOver) {
                                        animate3Y.pause();
                                    }
                                }
                            });
                            animate3Y.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    ((ViewGroup) centerView.getParent()).removeView(centerView);
                                }
                            });
                        }
                    });
                } else if (newRand == 4) {
                    final View centerRightView = new View(FiveLanesGameScreenActivity.this);
                    centerRightView.setLayoutParams(new RelativeLayout.LayoutParams(relativeLayout.getWidth() / numOfLanes, 250));
                    centerRightView.setBackgroundResource(R.drawable.police_png);
                    relativeLayout.addView(centerRightView);
                    centerRightView.setX(newRand * (relativeLayout.getWidth() / numOfLanes));
                    animate4Y = ObjectAnimator.ofFloat(centerRightView, "translationY", 0f, relativeLayout.getHeight() + 250);
                    animate4Y.setDuration(2500);
                    centerRightView.post(new Runnable() {
                        @Override
                        public void run() {
                            animate4Y.start();
                            animate4Y.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    if (collisionDetection(centerRightView, 0)) {
                                        animate4Y.cancel();
                                    }
                                    if (gameOver) {
                                        animate4Y.pause();
                                    }
                                }
                            });
                            animate4Y.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    ((ViewGroup) centerRightView.getParent()).removeView(centerRightView);
                                }
                            });
                        }
                    });
                }
                else {
                    final View rightView = new View(FiveLanesGameScreenActivity.this);
                    rightView.setLayoutParams(new RelativeLayout.LayoutParams(relativeLayout.getWidth() / numOfLanes, 250));
                    rightView.setBackgroundResource(R.drawable.police_png);
                    relativeLayout.addView(rightView);
                    rightView.setX(newRand * (relativeLayout.getWidth() / numOfLanes));
                    animate5Y = ObjectAnimator.ofFloat(rightView, "translationY", 0f, relativeLayout.getHeight() + 250);
                    animate5Y.setDuration(2500);
                    rightView.post(new Runnable() {
                        @Override
                        public void run() {
                            animate5Y.start();
                            animate5Y.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    if (collisionDetection(rightView, 0)) {
                                        animate5Y.cancel();
                                    }
                                    if(gameOver){
                                        animate5Y.pause();
                                    }
                                }
                            });
                            animate5Y.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    ((ViewGroup) rightView.getParent()).removeView(rightView);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public void prizeLoop(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (prizeRand == 0) {
                    final View prizeLeftView = new View(FiveLanesGameScreenActivity.this);
                    prizeLeftView.setLayoutParams(new RelativeLayout.LayoutParams(relativeLayout.getWidth() / numOfLanes, 250));
                    prizeLeftView.setBackgroundResource(R.drawable.coins);
                    relativeLayout.addView(prizeLeftView);
                    prizeLeftView.setX(prizeRand * (relativeLayout.getWidth() / numOfLanes));
                    prizeAnim1 = ObjectAnimator.ofFloat(prizeLeftView, "translationY", 0f, relativeLayout.getHeight() + 250);
                    prizeAnim1.setDuration(2500);

                    prizeLeftView.post(new Runnable() {
                        @Override
                        public void run() {
                            prizeAnim1.start();
                            prizeAnim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    if (collisionDetection(prizeLeftView, 1)) {
                                        prizeAnim1.cancel();
                                    }
                                    if(gameOver){
                                        prizeAnim1.pause();
                                    }
                                }
                            });

                            prizeAnim1.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    ((ViewGroup) prizeLeftView.getParent()).removeView(prizeLeftView);
                                }
                            });

                        }
                    });
                } else if (prizeRand == 1) {
                    final View prizeCenterLeftView = new View(FiveLanesGameScreenActivity.this);
                    prizeCenterLeftView.setLayoutParams(new RelativeLayout.LayoutParams(relativeLayout.getWidth() / numOfLanes, 250));
                    prizeCenterLeftView.setBackgroundResource(R.drawable.coins);
                    relativeLayout.addView(prizeCenterLeftView);
                    prizeCenterLeftView.setX(prizeRand * (relativeLayout.getWidth() / numOfLanes));
                    prizeAnim2 = ObjectAnimator.ofFloat(prizeCenterLeftView, "translationY", 0f, relativeLayout.getHeight() + 250);
                    prizeAnim2.setDuration(2500);
                    prizeCenterLeftView.post(new Runnable() {
                        @Override
                        public void run() {
                            prizeAnim2.start();
                            prizeAnim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    if (collisionDetection(prizeCenterLeftView, 1)) {
                                        prizeAnim2.cancel();
                                    }
                                    if(gameOver){
                                        prizeAnim2.pause();
                                    }
                                }
                            });
                            prizeAnim2.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    ((ViewGroup) prizeCenterLeftView.getParent()).removeView(prizeCenterLeftView);
                                }
                            });
                        }
                    });
                } else if (prizeRand == 3) {
                    final View prizeCenterView = new View(FiveLanesGameScreenActivity.this);
                    prizeCenterView.setLayoutParams(new RelativeLayout.LayoutParams(relativeLayout.getWidth() / numOfLanes, 250));
                    prizeCenterView.setBackgroundResource(R.drawable.coins);
                    relativeLayout.addView(prizeCenterView);
                    prizeCenterView.setX(prizeRand * (relativeLayout.getWidth() / numOfLanes));
                    prizeAnim3 = ObjectAnimator.ofFloat(prizeCenterView, "translationY", 0f, relativeLayout.getHeight() + 250);
                    prizeAnim3.setDuration(2500);
                    prizeCenterView.post(new Runnable() {
                        @Override
                        public void run() {
                            prizeAnim3.start();
                            prizeAnim3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    if (collisionDetection(prizeCenterView, 1)) {
                                        prizeAnim3.cancel();
                                    }
                                    if (gameOver) {
                                        prizeAnim3.pause();
                                    }
                                }
                            });
                            prizeAnim3.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    ((ViewGroup) prizeCenterView.getParent()).removeView(prizeCenterView);
                                }
                            });
                        }
                    });
                } else if (prizeRand == 4) {
                    final View prizeCenterRightView = new View(FiveLanesGameScreenActivity.this);
                    prizeCenterRightView.setLayoutParams(new RelativeLayout.LayoutParams(relativeLayout.getWidth() / numOfLanes, 250));
                    prizeCenterRightView.setBackgroundResource(R.drawable.coins);
                    relativeLayout.addView(prizeCenterRightView);
                    prizeCenterRightView.setX(prizeRand * (relativeLayout.getWidth() / numOfLanes));
                    prizeAnim4 = ObjectAnimator.ofFloat(prizeCenterRightView, "translationY", 0f, relativeLayout.getHeight() + 250);
                    prizeAnim4.setDuration(2500);
                    prizeCenterRightView.post(new Runnable() {
                        @Override
                        public void run() {
                            prizeAnim4.start();
                            prizeAnim4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    if (collisionDetection(prizeCenterRightView, 1)) {
                                        prizeAnim4.cancel();
                                    }
                                    if (gameOver) {
                                        prizeAnim4.pause();
                                    }
                                }
                            });
                            prizeAnim4.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    ((ViewGroup) prizeCenterRightView.getParent()).removeView(prizeCenterRightView);
                                }
                            });
                        }
                    });
                }
                else {
                    final View prizeRightView = new View(FiveLanesGameScreenActivity.this);
                    prizeRightView.setLayoutParams(new RelativeLayout.LayoutParams(relativeLayout.getWidth() / numOfLanes, 250));
                    prizeRightView.setBackgroundResource(R.drawable.coins);
                    relativeLayout.addView(prizeRightView);
                    prizeRightView.setX(prizeRand * (relativeLayout.getWidth() / numOfLanes));
                    prizeAnim5 = ObjectAnimator.ofFloat(prizeRightView, "translationY", 0f, relativeLayout.getHeight() + 250);
                    prizeAnim5.setDuration(2500);
                    prizeRightView.post(new Runnable() {
                        @Override
                        public void run() {
                            prizeAnim5.start();
                            prizeAnim5.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    if (collisionDetection(prizeRightView, 1)) {
                                        prizeAnim5.cancel();
                                    }
                                    if(gameOver){
                                        prizeAnim5.pause();
                                    }
                                }
                            });
                            prizeAnim5.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    ((ViewGroup) prizeRightView.getParent()).removeView(prizeRightView);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    // Method that start the game over activity when the game ends.
    public void startGameOverActivity() {
        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("numOfLanes", numOfLanes);
        intent.putExtra("vib", vib);
        intent.putExtra("tilt", tilt);
        startActivity(intent);
        finish();
    }

    // Method that set UI flags.
    public void setUIVisibility(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }


    @Override
    public void onResume() {
        ifPlaying = true;
        setUIVisibility();
        if(tilt == true){
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        ifPlaying = false;
        if(tilt == true){
            sensorManager.unregisterListener(this, sensor);
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        ifPlaying = false;
        if(tilt == true){
            sensorManager.unregisterListener(this, sensor);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if(tilt == true){
            sensorManager.unregisterListener(this, sensor);
        }
        super.onDestroy();
    }


    // Method for tilt sensors.
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double xSensor = sensorEvent.values[0];
        if(xSensor > 3 && xSensor < 5){
            moveLeft = true;
            changePos();
            moveLeft = false;
        }
        if(xSensor >= -5 && xSensor < -3){
            moveRight = true;
            changePos();
            moveRight = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
