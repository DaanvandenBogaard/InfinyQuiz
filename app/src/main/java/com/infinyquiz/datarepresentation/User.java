package com.infinyquiz.datarepresentation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class User {

    public String username, mail;

    //This empty constructor is necessary for Firebase to work
    public User() {

    }

    public User(String username, String mail) {
        this.username = username;
        this.mail = mail;
    }

    public String getMail() {
        return mail;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String newUsername){
        username = newUsername;
    }
}