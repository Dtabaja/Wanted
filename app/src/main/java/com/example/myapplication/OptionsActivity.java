package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class OptionsActivity extends AppCompatActivity {
    private Spinner spinner;
    private Button startBtn ,cancelBtn;
    private Switch vibSwitch, tiltSwitch;
    private boolean vib, tilt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUIVisibility();
        setContentView(R.layout.activity_options);

        spinner = findViewById(R.id.numOfLanesSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.numOfLanes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        vibSwitch = findViewById(R.id.vibSwitch);
        tiltSwitch = findViewById(R.id.tiltSwitch);
        startBtn = findViewById(R.id.startButton);
        cancelBtn = findViewById(R.id.cancelButton);

        vibSwitch.toggle();
        vib = true;
        tilt = false;
        vibSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    setVib(true);
                    Toast.makeText(OptionsActivity.this, "Vibration: ON", Toast.LENGTH_SHORT).show();
                }
                else{
                    setVib(false);
                    Toast.makeText(OptionsActivity.this, "Vibration: OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tiltSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    setTilt(true);
                    Toast.makeText(OptionsActivity.this, "Tilt: ON", Toast.LENGTH_SHORT).show();
                }
                else{
                    setTilt(false);
                    Toast.makeText(OptionsActivity.this, "Tilt: OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGameActivity();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Method that start the game activity.
    public void startGameActivity(){
        Intent intent;
        int spinnerPos = spinner.getSelectedItemPosition();
        String[] numValues = getResources().getStringArray(R.array.numOfLanes_array);
        final int numOfLanes = Integer.valueOf(numValues[spinnerPos]);
        if(numOfLanes == 3){
            intent = new Intent(this, ThreeLanesGameScreenActivity.class);
        }
        else {
            intent = new Intent(this, FiveLanesGameScreenActivity.class);
        }
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

    // Getters and setters.
    public void setVib(boolean bool){
        this.vib = bool;
    }

    public boolean getVib(){
        return this.vib;
    }

    public void setTilt(boolean bool){
        this.tilt = bool;
    }

    public boolean getTilt(){
        return this.tilt;
    }

    @Override
    public void onResume(){
        super.onResume();
        setUIVisibility();
    }
}
