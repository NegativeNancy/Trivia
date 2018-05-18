package com.example.ivodenhertog.trivia;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HighscoresHelper {

    private Callback activity;
    private final DatabaseReference myRef;
    private ArrayList<Highscore> highscores = new ArrayList<>();

    private static final String TAG = "HighscoresHelper";
    private static final String HIGHSCORE = "Highscore";

    /**
     * New Interface
     **/
    public interface Callback {
        void gotHighscores(ArrayList<Highscore> highscores);
        void gotError(String message);
    }

    /**
     * Constructor
     **/
    public HighscoresHelper() {
        myRef = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Upload new highscore to database
     **/
    public boolean postNewHighscore(final Highscore highscore) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String uid = currentUser.getUid();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(HIGHSCORE).hasChild(uid)) {
                    Highscore lastScore = dataSnapshot.child(HIGHSCORE).child(uid).
                            getValue(Highscore.class);
                    int newScore = lastScore.getScore() + highscore.getScore();

                    Log.e(TAG, "onDataChange: posted new score - " + newScore );
                    highscore.setScore(newScore);

                    myRef.child(HIGHSCORE).child(uid).setValue(highscore);
                } else {
                    myRef.child(HIGHSCORE).child(uid).setValue(highscore);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                activity.gotError(databaseError.getMessage());
            }
        });
        return true;
    }

    /**
     * Retrieve Highscores and display them in an row.
     **/
    public void getHighscore(final Callback activity) {
        this.activity = activity;

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear old highscore list.
                if (!highscores.isEmpty()) {
                    highscores = new ArrayList<>();
                }

                // Fill highscores list with current highscores.
                for (DataSnapshot snapshot : dataSnapshot.child(HIGHSCORE).getChildren()) {
                    Highscore highscore = snapshot.getValue(Highscore.class);
                    highscores.add(highscore);
                }

                // Sort highscores in descending order.
                //noinspection unchecked
                Collections.sort(highscores);
                activity.gotHighscores(highscores);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                activity.gotError(databaseError.getMessage());
            }
        });
    }
}
