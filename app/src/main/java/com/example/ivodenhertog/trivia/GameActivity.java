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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity implements TriviaHelper.Callback {
    private static final String TAG = "GameActivity";

    // ToDo: make rounds changeable from settings.
    private int totalRounds = 10;
    private int currentRound = 1;
    private int answersCorrect = 0;
    private String correctAnswer;

    private View questionsView;
    private View resultsView;
    private TriviaHelper request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        request = new TriviaHelper(getApplicationContext());
        request.getNextQuestion(this);

        questionsView = findViewById(R.id.questionView);
        resultsView = findViewById(R.id.resultView);



    }

    @Override
    public void gotQuestion(Question question) {
        TextView questionView = findViewById(R.id.gameQuestion);
        String title = "Round " + currentRound + " of " + totalRounds;
        setTitle(title);

        questionView.setText(question.getQuestion());
        correctAnswer = question.getCorrestAnswer();
        Log.d(TAG, "gotQuestion: answer = " + correctAnswer);
    }

    @Override
    public void gotError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void submitClicked(View view) {
        EditText answerView = findViewById(R.id.gameAnswer);
        String answer = answerView.getText().toString();

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
    }

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

    public void nextClicked(View view) {
        if (currentRound < totalRounds) {
            request.getNextQuestion(this);

            currentRound += 1;
            showResult(false);
        } else {
            // ToDo: Send score and send to highscore screen.
            Log.d(TAG, "nextClicked: current score = " + answersCorrect);

            Intent main = new Intent(GameActivity.this, MainActivity.class);
            startActivity(main);
        }
    }
}
