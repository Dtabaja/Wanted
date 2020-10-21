package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {
    private Button startBtn;
    private ImageButton setBtn, highscoresBtn, aboutBtn, exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUIVisibility();
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.startButton);
        setBtn = findViewById(R.id.settingsButton);
        highscoresBtn = findViewById(R.id.highscoresButton);
        aboutBtn = findViewById(R.id.aboutButton);
        exitBtn = findViewById(R.id.exitButton);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGameScreenActivity();
            }
        });
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOptionsActivity();
            }
        });
        highscoresBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startHighscoresActivity();
            }
        });
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutPopupDialog();
            }
        });
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitPopupDialog();
            }
        });
    }

    // Method that start the game activity.
    public void startGameScreenActivity(){
        Intent intent = new Intent(this, FiveLanesGameScreenActivity.class);
        startActivity(intent);
    }

    // Method that start the options activity.
    public void startOptionsActivity() {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }

    // Method that start the highscores activity.
    public void startHighscoresActivity() {
        Intent intent = new Intent(this, HighscoresActivity.class);
        startActivity(intent);
    }

    // Method that start the about pop up.
    public void aboutPopupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        LayoutInflater layoutInflater = getLayoutInflater();
        View popupView = layoutInflater.inflate(R.layout.popup_about, null);
        builder.setView(popupView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                setUIVisibility();
            }
        });
    }

    // Method that start the exit pop up.
    public void exitPopupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setUIVisibility();
            }
        });
        LayoutInflater layoutInflater = getLayoutInflater();
        View popupView = layoutInflater.inflate(R.layout.popup_exit, null);
        builder.setView(popupView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Phone back key event.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
                exitPopupDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
    public void onResume(){
        super.onResume();
        setUIVisibility();
    }


}
