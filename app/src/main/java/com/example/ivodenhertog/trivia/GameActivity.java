package com.example.ivodenhertog.trivia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GameActivity extends AppCompatActivity implements TriviaHelper.Callback {
    private static final String TAG = "GameActivity";

    // Global variables.
    private final int totalRounds = 10;
    private int currentRound = 1;
    private int answersCorrect = 0;
    private String correctAnswer;
    private String username;

    private View questionsView;
    private View resultsView;
    private TriviaHelper request;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mAuth = FirebaseAuth.getInstance();

        request = new TriviaHelper(getApplicationContext());
        request.getNextQuestion(this);

        questionsView = findViewById(R.id.questionView);
        resultsView = findViewById(R.id.resultView);
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
            username = currentUser.getDisplayName();
            Log.d(TAG, "onStart: " + username);
        } else {
            Intent login = new Intent(GameActivity.this, LoginActivity.class);
            startActivity(login);
        }
    }

    /**
     * Show new question on screen.
     **/
    @Override
    public void gotQuestion(Question question) {
        TextView questionView = findViewById(R.id.gameQuestion);
        String title = "Round " + currentRound + " of " + totalRounds;
        setTitle(title);

        questionView.setText(question.getQuestion());
        correctAnswer = question.getCorrestAnswer();
        Log.d(TAG, "gotQuestion: answer = " + correctAnswer);
    }

    /**
     * Show error in Toast
     **/
    @Override
    public void gotError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Submit button listner
     **/
    public void submitClicked(View view) {
        if (currentRound == totalRounds) {
            Button finish = findViewById(R.id.gameResultBtn);
            finish.setText(R.string.finish_game);
        }

        EditText answerView = findViewById(R.id.gameAnswer);
        String answer = answerView.getText().toString();

        // Check answer and show the corresponding text.
        if (correctAnswer.equalsIgnoreCase(answer)) {
            answerView.setText("");

            TextView result = findViewById(R.id.gameResult);
            answersCorrect += 1;

            result.setText(R.string.result_correct);
            showResult(true);
        } else {
            answerView.setText("");

            TextView result = findViewById(R.id.gameResult);
            result.setText(R.string.result_wrong);
            showResult(true);
        }

        // Get new question.
        request.getNextQuestion(this);
    }

    /**
     * Animator to show result screen
     **/
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showResult(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        questionsView.setVisibility(show ? View.GONE : View.VISIBLE);
        questionsView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                questionsView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        resultsView.setVisibility(show ? View.VISIBLE : View.GONE);
        resultsView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                resultsView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Next button listener.
     **/
    public void nextClicked(View view) {
        if (currentRound < totalRounds) {
            // Show new question.
            currentRound += 1;
            showResult(false);
        } else {
            // Send user to HighscoreActivity.
            Highscore highscore = new Highscore(username, answersCorrect);

            Intent highscoreActivity = new Intent(GameActivity.this, HighscoreActivity.class);
            highscoreActivity.putExtra("originActivity", "GameActivity");
            highscoreActivity.putExtra("highscore", highscore);
            startActivity(highscoreActivity);

            finish();
        }
    }
}
