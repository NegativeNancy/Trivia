package com.example.ivodenhertog.trivia;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class HighscoresAdapter extends ArrayAdapter<Highscore> {
    private final ArrayList<Highscore> highscores;

    public HighscoresAdapter(@NonNull Context context, @NonNull ArrayList<Highscore> objects) {
        super(context, R.layout.highscore_row,  objects);
        highscores = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.highscore_row, parent, false);
        }

        Highscore highscore = highscores.get(position);

        TextView username = convertView.findViewById(R.id.rowUsername);
        TextView score = convertView.findViewById(R.id.rowScore);

        username.setText(highscore.getName());
        score.setText(Integer.toString(highscore.getScore()));

        return convertView;
    }
}
