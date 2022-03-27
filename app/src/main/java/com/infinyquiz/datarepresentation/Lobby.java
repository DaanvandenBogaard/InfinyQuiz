package com.infinyquiz.datarepresentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/* The data representation of a lobby object.
 *
 * Used stand alone in firebase and {@code MatchMaker}.
 * Used via composition in {@code Game}.
 */
public class Lobby {

    //Constant: maximal number of people in lobby
    public static final int MAX_PEOPLE = 6;

    //Constant: minimal number of people to start game
    public static final int MIN_PEOPLE = 2;

    //The ID of this lobby (firebase reference ID)
    private String ID;

    //The ID of the place where the game will take place
    private String gameID;

    //List of users (by ID).
    private ArrayList<String> users = new ArrayList<>();

    //Boolean saying whether the game has started.
    private boolean gameHasStarted = false;

    //Empty constructor, needed by firebase.
    public Lobby() {
    }

    /* A function to add the id of a user to the list of user ids.
     *
     * @param String id, the id of the user that we want to add to the {@code users} list.
     * @pre {@code id != null && !id.equals("") }
     * @post {@code users.contains(id) && !users.contains(null)}
     * @throws none
     * @modifies {@code users}
     */
    public void addUser(String id) {
        users.add(id);
        users.removeAll(Collections.singleton(null));
    }

    /* A function to get the ArrayList of user ids.
     *
     * @pre {@code users != null}
     * @post {@code !users.contains(null)}
     * @throws none
     * @returns {@code users}
     * @modifies {@code users}
     */
    public ArrayList<String> getUsers() {
        users.removeAll(Collections.singleton(null));
        return users;
    }

    /* A function to retrieve the number of users registered in this lobby
     *
     * @pre {@code users != null}
     * @post {@code !users.contains(null)}
     * @returns {@code users.size()}
     */
    public int getLobbySize() {
        users.removeAll(Collections.singleton(null));
        return users.size();
    }

    /* Standard getters and setters.
     * Contracts are implied.
     */
    public String getId() {
        return ID;
    }

    public void setID(String id) {
        ID = id;
    }

    public void setGameID(String id) {
        gameID = id;
    }

    public String getGameID() {
        return gameID;
    }

    public boolean gameHasStarted() {
        return gameHasStarted;
    }

    public void shutDownLobby() {
        gameHasStarted = true;
    }

}
