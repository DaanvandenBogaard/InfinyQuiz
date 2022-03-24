package com.infinyquiz.datarepresentation;

import java.util.ArrayList;

public class RandomGame extends Game{


    public RandomGame(Lobby lobby) {
        super(lobby);
    }

    //Constructor for firebase
    public RandomGame(){
        super();
    }

    @Override
    public void setQuestions(ArrayList<Question> _questions) {
        questions = _questions;
    }
}
