package com.infinyquiz;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

public abstract class Game {

    private List<String> users;
    private List<Question> questions;
    private int questionCounter;
    private Question currentQuestion;
    //TODO: add scoreboard

    public Game() {

    }

    public List<String> getUsers() {
        return users;
    }

    public Question getNextQuestion() {
        return questions.get(questions.indexOf(currentQuestion) + 1);
    }

    public abstract void setQuestions();
}
