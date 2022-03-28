package com.infinyquiz.datarepresentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* An abstract class to represent a currently going on game
 * This class is implemented by {@code RandomGame} and {@code CustomGame} (please note that due to
 * time limitations, we have not been able to also implement {@code CustomGame}).
 *
 * This class stores the questions, the lobby, the joined players, the players that have answered
 * and many more.
 */
public abstract class Game {

    //The list of questions that will be asked during this game.
    public ArrayList<Question> questions;

    //The Lobby of which this game originated
    private Lobby lobby;

    //A list with the IDs of all players who have made the transition to the game.
    private ArrayList<String> joinedPlayers = new ArrayList<>();

    //A list with the IDs of all players that made the transition to the scoreboard
    private ArrayList<String> answeredPlayers = new ArrayList<>();

    //A map that maps IDs of users to their respective scores
    private Map<String, Integer> scoreboard = new HashMap<>();

    //A list of votes for categories
    private ArrayList<String> categoryVotes = new ArrayList<>();

    //A list players that have returned from the scoreboard to the game
    private ArrayList<String> returnedPlayers = new ArrayList<>();

    //The index at what question we are.
    public int index = 0;

    //the number of questions, according to requirements, must be 10
    public static int NUMBER_OF_QUESTIONS = 2; // 10; //TODO set requirement

    //ID to game:
    private String gameID;

    /* A function to retrieve the answeredPlayers
     *
     * @pre none
     * @returns {@code answeredPlayers}
     */
    public ArrayList<String> getAnsweredPlayers() {
        return answeredPlayers;
    }

    /* A function to get the question we are currently considering.
     *
     * @pre none
     * @returns {@code question} where this is the question in the right index. If questions
     * is null, give an empty question.
     */
    public Question getCurrentQuestion() {
        if (questions == null) {
            return new Question();
        }
        return questions.get(index);
    }

    public void incrementQuestionIndex() {
        index++;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public Game() {
    }

    ;

    public Game(Lobby lobby) {
        this.lobby = lobby;
    }

    public List<String> getUsers() {
        return lobby.getUsers();
    }

    public ArrayList<String> getJoinedPlayers() {
        return joinedPlayers;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public int getScore(String userId) {
        return scoreboard.get(userId);
    }

    public Map<String, Integer> getScoreboard() {
        return scoreboard;
    }

    public void setScore(String userId, int score) {
        scoreboard.put(userId, score);
    }

    public void addVote(String vote) {
        categoryVotes.add(vote);
    }

    public ArrayList<String> getCategoryVotes() {
        return categoryVotes;
    }

    public abstract void setQuestions(ArrayList<Question> questions);

    public void setGameID(String id) {
        gameID = id;
    }

    public void clearAnsweredPlayers() {
        answeredPlayers = new ArrayList<>();
    }

    public boolean haveAllPlayersAnswered() {
        return lobby.getLobbySize() <= answeredPlayers.size();
    }

    public void resetJoinedUsers() {
        joinedPlayers = new ArrayList<>();
    }

    public void addPlayerToAnswered(String playerID) {

        if(!answeredPlayers.contains(playerID)){
            answeredPlayers.add(playerID);
        }
    }

    public String getGameID() {
        return gameID;
    }

    /* A boolean value to see if all players have joined
     *
     * @pre {@code lobby != null & joinedPlayers != null}
     * @return {@code lobby.getLobbySize() <= joinedPlayers.size()}
     * @modifies none
     */
    public Boolean allPlayersJoined() {
        return lobby.getLobbySize() <= joinedPlayers.size();
    }

    /* A boolean value to see if a certain player has joined
     *
     * @param {@code String playerID} the ID of the player we check if the player has joined.
     * @pre {playerID != null}
     * @returns {@code joinedPlayers.contains(playerId)}
     * @modifies none
     */
    public boolean hasPlayerJoined(String playerId) {
        return joinedPlayers.contains(playerId);
    }

    /* let a player join the game
     *
     * @param {@code String} playerID, the ID of the player to join
     * @pre {@code joinedPlayer != null}
     * @post {@code joinedPlayers.contains(playerID)}
     * @modifies {@code joinedPlayers}
     */
    public void joinPlayer(String playerID) {
        scoreboard.put(playerID, 0);
        joinedPlayers.add(playerID);
    }

    public void returnPlayer(String ID){
        returnedPlayers.add(ID);
    }

    public void clearReturnedPlayers(){
        returnedPlayers = new ArrayList<>();
    }

    public boolean haveAllPlayersReturned(){
        return lobby.getLobbySize() <= returnedPlayers.size();
    }

    public ArrayList<String> getReturnedPlayers(){
        return returnedPlayers;
    }
}
