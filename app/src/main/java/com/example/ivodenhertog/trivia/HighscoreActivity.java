package com.example.ivodenhertog.trivia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class HighscoreActivity extends AppCompatActivity implements HighscoresHelper.Callback{

    private static final String TAG = "HighscoreActivity";
    private HighscoresHelper highscoresHelper;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        mAuth = FirebaseAuth.getInstance();

        highscoresHelper = new HighscoresHelper();

        Intent intent =getIntent();
        String originActivity = intent.getStringExtra("originActivity");

        // If activity started from the GameActivity update highscores.
        if (originActivity != null) {
            Highscore highscore = (Highscore) intent.getSerializableExtra("highscore");
            if (highscoresHelper.postNewHighscore(highscore)) {
                refreshScreen();
            }
        }

        refreshScreen();
    }

    /**
     * Get username of logged in user
     **/
    @Override
    public void onStart() {
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            super.onStart();
            String username = currentUser.getDisplayName();
            Log.d(TAG, "onStart: " + username);
        } else {
            Intent login = new Intent(HighscoreActivity.this, LoginActivity.class);
            startActivity(login);
        }
    }
    /**
     * Retrieve highscores
     **/
    private void refreshScreen() {
        highscoresHelper.getHighscore(this);
    }

    /**
     * Show highscores on screen
     **/
    @Override
    public void gotHighscores(ArrayList<Highscore> highscores) {
        HighscoresAdapter adapter = new HighscoresAdapter(this, highscores);
        ListView listView = findViewById(R.id.highscoreList);
        ProgressBar progressBar = findViewById(R.id.highscoreProgressBar);

        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

    }

    /**
     * Show error Toast
     **/
    @Override
    public void gotError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
