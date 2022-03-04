package com.infinyquiz;

public class Question {

    private String question, category, correctOption;
    private int difficulty;
    private String[] options;
    private long pictureID;

    public Question() {}

    public String getQuestion() {
        return question;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getCategory() {
        return category;
    }

    public String[] getOptions() {
        return options;
    }

    public String getCorrectOption() {
        return correctOption;
    }

}
