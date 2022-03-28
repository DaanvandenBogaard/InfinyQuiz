package com.infinyquiz.datarepresentation;

/* A class representing a user. This class is used to store data on our users
 * in firebase real time database.
 *
 * We store a username, email address and sometimes also a profile picture
 * (profile picture not incorporated into this class).
 */
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String newUsername) {
        username = newUsername;
    }
}