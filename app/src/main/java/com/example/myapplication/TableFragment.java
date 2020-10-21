package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;


import static android.content.Context.MODE_PRIVATE;

public class TableFragment extends Fragment {

    private TextView name1, name2, name3, name4, name5, name6, name7, name8, name9, name10,
                    score1, score2, score3, score4, score5, score6, score7, score8, score9, score10;
    private TableLayout tableLayout;
    private SharedPreferences sharedPreferences;
    private TextView[] nameArray, scoreArray;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        tableLayout = view.findViewById(R.id.tableLayout);

        nameArray = new TextView[10];
        scoreArray = new TextView[10];

        nameArray[0] = (name1 = view.findViewById(R.id.name1));
        nameArray[1] = (name2 = view.findViewById(R.id.name2));
        nameArray[2] = (name3 = view.findViewById(R.id.name3));
        nameArray[3] = (name4 = view.findViewById(R.id.name4));
        nameArray[4] = (name5 = view.findViewById(R.id.name5));
        nameArray[5] = (name6 = view.findViewById(R.id.name6));
        nameArray[6] = (name7 = view.findViewById(R.id.name7));
        nameArray[7] = (name8 = view.findViewById(R.id.name8));
        nameArray[8] = (name9 = view.findViewById(R.id.name9));
        nameArray[9] = (name10 = view.findViewById(R.id.name10));

        scoreArray[0] = (score1 = view.findViewById(R.id.score1));
        scoreArray[1] = (score2 = view.findViewById(R.id.score2));
        scoreArray[2] = (score3 = view.findViewById(R.id.score3));
        scoreArray[3] = (score4 = view.findViewById(R.id.score4));
        scoreArray[4] = (score5 = view.findViewById(R.id.score5));
        scoreArray[5] = (score6 = view.findViewById(R.id.score6));
        scoreArray[6] = (score7 = view.findViewById(R.id.score7));
        scoreArray[7] = (score8 = view.findViewById(R.id.score8));
        scoreArray[8] = (score9 = view.findViewById(R.id.score9));
        scoreArray[9] = (score10 = view.findViewById(R.id.score10));





        sharedPreferences = this.getActivity().getSharedPreferences("shared_preferences", MODE_PRIVATE);

        String jsonString = sharedPreferences.getString("list", null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Player>>() {}.getType();
        ArrayList<Player> listFromGson;
        listFromGson = gson.fromJson(jsonString, type);
        if(listFromGson == null){
            listFromGson = new ArrayList<>();
        }

        Collections.sort(listFromGson);

        for(int i=0; i < listFromGson.size() && i < 10; i++){
            nameArray[i].setText(listFromGson.get(i).getName());
            scoreArray[i].setText("" + listFromGson.get(i).getScore());
        }

        return view;
    }

}
