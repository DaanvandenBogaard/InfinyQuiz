package com.infinyquiz.datarepresentation;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.infinyquiz.datarepresentation.Question;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Game {

    public ArrayList<Question> questions;
    private int questionCounter;
    private Lobby lobby;
    //A list with the IDs of all players who have made the transition to the game.
    private ArrayList<String> joinedPlayers = new ArrayList<>();
    private ArrayList<String> answeredPlayers = new ArrayList<>();
    private Map<String, Integer> scoreboard = new HashMap<>();
    private ArrayList<String> categoryVotes = new ArrayList<>();
    public int index = 0;

    //the number of questions, according to requirements, must be 10
    public static int NUMBER_OF_QUESTIONS = 2; // 10; //TODO set requirement

    //ID to game:
    private String gameID;

    public ArrayList<String> getAnsweredPlayers() {
        return answeredPlayers;
    }

    public Question getCurrentQuestion(){
        if(questions == null){
            return new Question();
        }
        return questions.get(index);
    }

    public void incrementQuestionIndex(){
        index++;
    }

    public ArrayList<Question> getQuestions(){
        return questions;
    }

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

    public int getScore(String userId){
       return scoreboard.get(userId);
    }

    public Map<String, Integer> getScoreboard(){
        return scoreboard;
    }

    public void setScore(String userId, int score){
        scoreboard.put(userId, score);
    }

    public void addVote(String vote){
        categoryVotes.add(vote);
    }

    public ArrayList<String> getCategoryVotes(){
        return categoryVotes;
    }

    public abstract void setQuestions(ArrayList<Question> questions);

    public void setGameID(String id){
        gameID = id;
    }

    public void clearAnsweredPlayers(){
        answeredPlayers = new ArrayList<>();
    }

    public boolean haveAllPlayersAnswered(){
        return lobby.getLobbySize() == answeredPlayers.size();
    }

    public void addPlayerToAnswered(String playerID){
        answeredPlayers.add(playerID);
    }

    public String getGameID(){
        return gameID;
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
        scoreboard.put(playerID, 0);
        joinedPlayers.add(playerID);
    }
}
