package com.infinyquiz.datarepresentation;


import java.util.ArrayList;

public class Question {

    //The question itself
    private String question;

    //The category of the question
    private String category;

    //The difficulty of the question, on a 1-5 scale.
    private int difficulty = 0;

    //The different possible answers
    private ArrayList<String> options;

    //The correct answer
    private String correctOption;

    //The ID/identifier of the picture in firebase database (is null if no picture is set).
    private String pictureID;

    //Reference in firebase, usefull for retrieving random questions.
    private String reference;

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
    public Question(String question, String category, int difficulty, ArrayList<String> options, String correctOption, String pictureID) throws IllegalArgumentException {
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
        if (question == null) {
            return "Could not find a Question";
        }
        return question;
    }

    public String getCategory() {
        if (category == null) {
            return "Could not retrieve a category";
        }
        return category;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public ArrayList<String> getOptions() {
        if (options == null) {
            options = new ArrayList<>();
            options.add("Could not retrieve any options");
            options.add("Could not retrieve any options");
            options.add("Could not retrieve any options");
            options.add("Could not retrieve any options");
        }
        return options;
    }

    public String getCorrectOption() {
        if (correctOption == null) {
            return "could not find correct answer";
        }
        return correctOption;
    }

    public String getPictureID() {
        if (pictureID == null) {
            return "Could not find pictureID";
        }
        return pictureID;
    }

    //Check if 2 instances are equal
    @Override
    public boolean equals(final Object obj) {
        //check if obj is of right type:
        if (obj == null | !(obj instanceof Question)) {
            return false;
        }
        Question reference = (Question) obj;
        if (!this.getQuestion().equals(reference.getQuestion())) {
            return false;
        }
        if (!this.getCategory().equals(reference.getCategory())) {
            return false;
        }
        if (this.getDifficulty() != reference.getDifficulty()) {
            return false;
        }
        if (!this.getOptions().equals(reference.getOptions())) {
            return false;
        }
        if (!this.getCorrectOption().equals(reference.getCorrectOption())) {
            return false;
        }
        if (!this.getPictureID().equals(reference.getPictureID())) {
            return false;
        }
        return true;
    }

    //A check to see if question is equal to the empty question
    public boolean isEmpty() {
        return this.equals(new Question());
    }

    public void setReference(String string) {
        reference = string;
    }

    public String getReference() {
        return reference;
    }

}
