package com.infinyquiz.datarepresentation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* A class to convert user data
 *
 * We start by retrieving our set of users, which we then modify in to a few
 * maps in order to easily access the correct data easily.
 */
public class UserDataConverter {

    //A map to convert user's ids to user's usernames.
    private Map<String, String> idToUsername = null;

    //Map from ID to users
    private Map<String, User> idToUser = null;

    //A map from a user's email to a user's ID
    //Note that emails are unique in our set up of firebase and so this does not cause
    //issues when using a map.
    private Map<String, String> emailToID = null;

    //Wheter or not firebase has been loaded
    private boolean isReady = false;

    //constructor:
    public UserDataConverter() {
        getUsersInfo();
    }

    /* Sets the map {@code idToUsername} using the data from firebase.
     *
     * @pre none
     * @post {@code idToUsername != null}
     * @modifies {@code idToUsername}
     * @throws none
     */
    private void getUsersInfo() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference ref = database.getReference().child("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idToUsername = new HashMap<String, String>();
                idToUser = new HashMap<String, User>();
                emailToID = new HashMap<String, String>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User thisUser = data.getValue(User.class);
                    idToUsername.put(data.getKey(), thisUser.getUsername());
                    idToUser.put(data.getKey(), thisUser);
                    emailToID.put(thisUser.getMail(), data.getKey());
                }
                isReady = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("could not read data", "The read failed: " + databaseError.getCode());
            }
        });

    }

    /* A function that converts a list of IDS to an arraylist of usernames
     *
     * @pre {@code idToUsername != null}
     * @returns {@code usernames} an arraylist of the corresponding user names.
     * @modifies none
     * @throws none
     */
    public ArrayList<String> getUsernames(List<String> ids) {
        ArrayList<String> usernames = new ArrayList<>();
        if (idToUsername != null) {
            for (String id : ids) {
                usernames.add(idToUsername.get(id));
            }
        }
        return usernames;
    }

    /* A function to convert the keys of a map (which are userId's) to a map where the keys are the
     * corresponding usernames.
     *
     * @pre{@code idToUsername != null}
     * @returns {@code convertedMap} a map where the keys have been converted
     * @modifies none
     * @throws none
     */
    public HashMap<String, Integer> convertScoreBoard(Map<String, Integer> map) {
        HashMap<String, Integer> convertedMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            convertedMap.put(idToUsername.get(entry.getKey()), entry.getValue());
        }
        return convertedMap;
    }

    /* A function to convert one userID to a username
     *
     * @param {@code String id} the user's id.
     * @pre {@code idToUsername != null}
     * @returns {@code idToUsername.get(id)}
     */
    public String getUserName(String id) {
        if (idToUsername == null) {
            return "";
        }
        return idToUsername.get(id);
    }


    //Returns whether or not the user data has been loaded from firebase
    public boolean isReady() {
        return isReady;
    }

    /* A function to convert one userID to a user object
     *
     * @param {@code String id} the user's id.
     * @pre {@code idToUser != null}
     * @returns {@code idToUser.get(id)}
     */
    public User getUser(String id) {
        if (idToUser == null) {
            return new User(); //return empty user.
        }
        return idToUser.get(id);
    }

    /* A function to convert an email adress to an ID
     *
     * @param {@code String email} the email adress who'se user ID we want to retrieve.
     * @pre {@code emailToID != null}
     * @post {@code emailToID.get(email)}
     */
    public String getID(String email) {
        if (emailToID == null) {
            return "";
        }
        return emailToID.get(email);
    }

    /* A function that uploads the encoded image to firebase
     *
     * @pre {@code String != null}
     * @returns Bitmap
     * @post image is decoded from base64 to bitmap
     */
    public static Bitmap decodeImage(String image) {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

}
