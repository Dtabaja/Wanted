package com.example.myapplication;

import androidx.fragment.app.FragmentActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class HighscoresActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    private Button backBtn;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUIVisibility();
        setContentView(R.layout.activity_highscores);

        sharedPreferences = getSharedPreferences("shared_preferences", MODE_PRIVATE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);


        backBtn = findViewById(R.id.backButton);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.4117257, 35.0818155), 6));

        String jsonString = sharedPreferences.getString("list", null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Player>>() {
        }.getType();
        ArrayList<Player> listFromGson;
        listFromGson = gson.fromJson(jsonString, type);
        if (listFromGson == null) {
            listFromGson = new ArrayList<>();
        }
        Collections.sort(listFromGson);
        for (int i = 0; i < listFromGson.size(); i++) {
            if (listFromGson.get(i).getLatitude() != 0.0 && listFromGson.get(i).getLongitude() != 0.0) {
                map.addMarker(new MarkerOptions().position(new LatLng(listFromGson.get(i).getLatitude(), listFromGson.get(i).getLongitude()))).setTitle((i + 1) + ". " + listFromGson.get(i).getName());

            }
        }
    }

    // Method that set UI flags.
    public void setUIVisibility() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUIVisibility();
    }

}
