package com.infinyquiz.datarepresentation;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.infinyquiz.datarepresentation.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

public abstract class Game {

    private List<Question> questions;
    private int questionCounter;
    private Question currentQuestion;
    private Lobby lobby;
    //A list with the IDs of all players who have made the transition to the game.
    private List<String> joinedPlayers = new ArrayList<>();
    //TODO: add scoreboard

    //ID to game:
    private String gameID;

    public Game(){};

    public Game(Lobby lobby) {
        this.lobby = lobby;
    }

    public List<String> getUsers() {
        return lobby.getUsers();
    }

    public Lobby getLobby(){
        return lobby;
    }

    public Question getNextQuestion() {
        if(questions == null){
            return new Question();
        }
        return questions.get(questions.indexOf(currentQuestion) + 1);
    }

    public abstract void setQuestions();

    public void setGameID(String id){
        gameID = id;
    }

    public String getGameID(){
        return gameID;
    }

    public Boolean allPlayersJoined(){
        return lobby.getLobbySize() == joinedPlayers.size();
    }
}
