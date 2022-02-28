package com.infinyquiz.datarepresentation;


public class Question {

    //The question itself
    private String question;

    //The category of the question
    private String category;

    //The difficulty of the question, on a 1-5 scale.
    private int difficulty;

    //The different possible answers
    private String[] options;

    //The correct answer
    private String correctOption;

    //The ID/identifier of the picture in firebase database (is null if no picture is set).
    private String pictureID;

    //Public empty constructor, needed by firebase
    public Question() {
    }

    /* Public constructor with data
     *
     * @pre {@code difficulty >= 1 && difficulty <= 5}
     * @returns {@code Question}
     * @throws IllegalArgumentException if precondition is violated.
     * @modifies none
     */
    public Question(String question, String category, int difficulty, String[] options, String correctOption, String pictureID) throws IllegalArgumentException {
        this.question = question;
        this.category = category;
        if (1 <= difficulty & difficulty <= 5) {
            this.difficulty = difficulty;
        } else {
            throw new IllegalArgumentException("Difficulty of the question is not in correct range.");
        }
        this.options = options;
        this.correctOption = correctOption;
        this.pictureID = pictureID;
    }

    //Getters:
    public String getQuestion() {
        return question;
    }

    public String getCategory() {
        return category;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String[] getOptions() {
        return options;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public String getPictureID() {
        return pictureID;
    }
}
