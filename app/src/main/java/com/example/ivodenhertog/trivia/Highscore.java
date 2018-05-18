package com.example.ivodenhertog.trivia;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Highscore implements Serializable, Comparable {
    // Variables.
    private String name;
    private int score;

    // Constructor for Firebase.
    public Highscore(){
    }

    // Normal Constructor
    public Highscore(String name, int newScore) {
        this.name = name;
        this.score = this.score + newScore;
    }

    // Getters and setters.
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int newScore) {
        this.score = newScore;
    }

    // Sort list.
    @Override
    public int compareTo(@NonNull Object o) {
        int compareScore = ((Highscore) o).getScore();
        return compareScore - this.score;
    }
}
