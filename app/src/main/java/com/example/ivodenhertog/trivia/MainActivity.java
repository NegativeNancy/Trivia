package com.example.ivodenhertog.trivia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "onCreate: " + currentUser.getEmail());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    /**
     * Force exit on back press in main menu.
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /**
     *  Update Firebase user
     **/
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            // Name, email address, and profile photo Url
            String name = currentUser.getDisplayName();

            Log.d(TAG, "updateUI: username = " + name);
        } else {
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(login);
        }
    }

    /**
     *  Click listener for new game.
     **/
    public void newGameClicked(View view) {
        Intent newGame = new Intent(MainActivity.this, GameActivity.class);
        startActivity(newGame);
    }

    /**
     * Click listener for highscore.
     **/
    public void highscoreClicked(View view) {
        Intent newGame = new Intent(MainActivity.this, HighscoreActivity.class);
        startActivity(newGame);
    }

    /**
     * Function to create on-screen menu.
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return(true);
    }

    /**
     * Function that handles the events when menu button is pressed.
     **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                updateUI(null);
                return(true);
        }
        return super.onOptionsItemSelected(item);
    }
}
