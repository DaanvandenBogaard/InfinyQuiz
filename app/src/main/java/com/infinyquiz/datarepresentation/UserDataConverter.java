package com.infinyquiz.datarepresentation;

import android.service.autofill.UserData;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infinyquiz.datarepresentation.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* A class to convert user data
 * Supports an operation to convert a list of integers to a list of
 */
public class UserDataConverter {

    private Map<String, String> idToUsername = null;

    private boolean isReady = false;

    //Map from ID to users
    private Map<String, User> idToUser = new HashMap<>();

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
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User thisUser = data.getValue(User.class);
                    idToUsername.put(data.getKey(), thisUser.getUsername());
                    idToUser.put(data.getKey(),thisUser);
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
    public String getUserName(String id){
        if(idToUsername == null){
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
    public User getUser(String id){
        if(idToUser == null){
            return new User(); //return empty user.
        }
        return idToUser.get(id);
    }

}
