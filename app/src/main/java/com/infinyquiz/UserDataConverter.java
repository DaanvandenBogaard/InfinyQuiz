package com.infinyquiz;

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

    //constructor:
    public UserDataConverter(){
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
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    User thisUser = data.getValue(User.class);
                    idToUsername.put(data.getKey(), thisUser.getUsername());
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
    public ArrayList<String> getUsernames(List<String> ids){
        ArrayList<String> usernames = new ArrayList<>();
        if(idToUsername != null){
            for(String id : ids){
                usernames.add(idToUsername.get(id));
            }
        }
        return usernames;
    }

    public boolean isReady(){
        return isReady;
    }

}
