package com.infinyquiz.datarepresentation;

import java.util.ArrayList;
import java.util.Collections;

public class Lobby {

    //Constant: maximal number of people in lobby
    public static final int MAX_PEOPLE = 2;

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

    //Constructor
    public Lobby(){ }

    public void addUser(String id){
        users.add(id);
        users.removeAll(Collections.singleton(null));
    }

    public ArrayList<String> getUsers(){
        users.removeAll(Collections.singleton(null));
        return users;
    }

    public int getLobbySize(){
        users.removeAll(Collections.singleton(null));
        return users.size();
    }

    public String getId(){
        return ID;
    }

    public void setID(String id){
        ID = id;
    }

    public void setGameID(String id){
        gameID = id;
    }

    public String getGameID(){
        return gameID;
    }

    public boolean gameHasStarted(){
        return gameHasStarted;
    }

    public void shutDownLobby(){
        gameHasStarted = true;
    }
}
