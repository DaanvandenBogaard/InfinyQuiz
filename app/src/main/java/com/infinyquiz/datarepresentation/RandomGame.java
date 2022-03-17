package com.infinyquiz.datarepresentation;

public class RandomGame extends Game{


    public RandomGame(Lobby lobby) {
        super(lobby);
    }

    //Constructor for firebase
    public RandomGame(){
        super();
    }

    @Override
    public void setQuestions(String category) {

    }
}
