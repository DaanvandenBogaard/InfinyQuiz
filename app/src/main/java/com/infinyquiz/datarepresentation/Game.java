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
    private ArrayList<String> joinedPlayers = new ArrayList<>();
    //TODO: add scoreboard

    //the number of questions, according to requirements, must be 10
    public static int NUMBER_OF_QUESTIONS = 2; // 10; //TODO set requirement

    //ID to game:
    private String gameID;

    public Game(){};

    public Game(Lobby lobby) {
        this.lobby = lobby;
    }

    public List<String> getUsers() {
        return lobby.getUsers();
    }

    public ArrayList<String> getJoinedPlayers(){
        return joinedPlayers;
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

    public abstract void setQuestions(String category);

    public void setGameID(String id){
        gameID = id;
    }

    public String getGameID(){
        return gameID;
    }


    public List<Question> getQuestions(){
        return questions;
    }

    /* A boolean value to see if all players have joined
     *
     * @pre {@code lobby != null & joinedPlayers != null}
     * @return {@code lobby.getLobbySize() <= joinedPlayers.size()}
     * @modifies none
     */
    public Boolean allPlayersJoined(){
        return lobby.getLobbySize() <= joinedPlayers.size();
    }

    /* A boolean value to see if a certain player has joined
     *
     * @param {@code String playerID} the ID of the player we check if the player has joined.
     * @pre {playerID != null}
     * @returns {@code joinedPlayers.contains(playerId)}
     * @modifies none
     */
    public boolean hasPlayerJoined(String playerId){
        return joinedPlayers.contains(playerId);
    }

    /* let a player join the game
     *
     * @param {@code String} playerID, the ID of the player to join
     * @pre {@code joinedPlayer != null}
     * @post {@code joinedPlayers.contains(playerID)}
     * @modifies {@code joinedPlayers}
     */
    public void joinPlayer(String playerID){
        joinedPlayers.add(playerID);
    }
}
