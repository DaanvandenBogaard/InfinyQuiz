package com.infinyquiz.datarepresentation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class User {

    public String username, mail, id;
    public List<String> friendList;
    public String imageUrl;

    //This empty constructor is necessary for Firebase to work
    public User() {

    }

    public User(String username, String mail) {
        this.username = username;
        this.mail = mail;
        this.id = FirebaseAuth.getInstance().getUid();
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public String getMail() {
        return mail;
    }

    public void addFriend(String friendID) {
        friendList.add(friendID);
    }

    public void removeFriend(String friendID) {
        friendList.remove(friendID);
    }

    public void setProfilePicture(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProfilePictureID() {
        return imageUrl;
    }

    public void setUsername(String newUsername){
        username = newUsername;
    }
}