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

    //List of users (by ID).
    private ArrayList<String> users = new ArrayList<>();

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
}
