package com.example.ivodenhertog.trivia;

public class Question {
    // Variables
    private String question;
    private String[] answers;
    private String correctAnswer;

    // Constructor
    public Question(String question, String[] answers, String correstAnswer) {
        this.question = question;
        this.answers = answers;
        this.correctAnswer = correstAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String[] getAnswers() {
        return answers;
    }

    public void setAnswers(String[] answers) {
        this.answers = answers;
    }

    public String getCorrestAnswer() {
        return correctAnswer;
    }

    public void setCorrestAnswer(String correstAnswer) {
        this.correctAnswer = correstAnswer;
    }
}
