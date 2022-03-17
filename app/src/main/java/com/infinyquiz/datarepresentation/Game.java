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
    private Question currentQuestion;
    private Lobby lobby;
    //A list with the IDs of all players who have made the transition to the game.
    private ArrayList<String> joinedPlayers = new ArrayList<>();
    private Map<String, Integer> scoreboard = new HashMap<>();
    private ArrayList<String> categoryVotes = new ArrayList<>();

    //the number of questions, according to requirements, must be 10
    public static int NUMBER_OF_QUESTIONS = 2; // 10; //TODO set requirement

    //ID to game:
    private String gameID;

    public Question getCurrentQuestion(){
        if(currentQuestion == null || questions == null || currentQuestion.equals(new Question())){
            currentQuestion = new Question();
        }
        return currentQuestion;
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

    public Question getNextQuestion() {
        if(questions == null){
            currentQuestion = new Question();
        }
        else if(currentQuestion == null || currentQuestion.equals(new Question())) {
            currentQuestion = questions.get(0);
        } else{
            currentQuestion = questions.get(questions.indexOf(currentQuestion) + 1);
        }
        return currentQuestion;
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
