package com.infinyquiz.datarepresentation;

import java.util.ArrayList;

/* A concrete class implementing the abstract class {@code Game.class}
 *
 * Note that this is the only concrete class implementing {@code Game.class} due to our
 * time constraints. Also note that we did not have a "must" requirement that stated
 * that we needed to implement more.
 *
 * The class is quite minimalistic. Originally the plan was to have the custom game
 * where the selection of questions would be very different from where it concerns a
 * regular game.
 *
 * This class is used to represent a game which came to be due to random matchmaking.
 */
public class RandomGame extends Game{

    //Constructor
    public RandomGame(Lobby lobby) {
        super(lobby);
    }

    //Constructor for firebase (needs to be empty)
    public RandomGame(){
        super();
    }

    //Originally we had different plans to do this method. In the end, due to time
    //constraints we went with the easier approach.
    @Override
    public void setQuestions(ArrayList<Question> _questions) {
        questions = _questions;
    }
}
