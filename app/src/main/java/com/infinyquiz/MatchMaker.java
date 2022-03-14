package com.infinyquiz;

import static java.lang.String.valueOf;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infinyquiz.datarepresentation.Lobby;
import com.infinyquiz.datarepresentation.Question;

import java.util.ArrayList;
import java.util.Random;

public class MatchMaker {

    //A reference to the database (always set in constructor)
    final FirebaseDatabase database;

    //A reference to the lobby that has been found
    private String lobbyID = null;
    private Lobby lobby;
    //Index at which user is stored
    private int userIndex;

    //The current user ID
    final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //Constructor
    public MatchMaker() {
        database = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/");
    }

    public void lookForLobby() {
        DatabaseReference ref = database.getReference().child("Lobbies").child("OpenLobbies");
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (lobbyID != null && lobby != null) {
                    //update lobby data:
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Lobby curLobby = data.getValue(Lobby.class);
                        if(curLobby.getId() == lobbyID) {
                            lobby = curLobby;
                        }
                    }
                    return;
                }
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //Count size of lobby
                    Lobby curLobby = data.getValue(Lobby.class);

                    if (curLobby.getLobbySize() <= Lobby.MAX_PEOPLE) { //Add user to this lobby
                        lobby = curLobby;
                        curLobby.setID(data.getKey());
                        lobbyID = curLobby.getId();
                        curLobby.addUser(userID);
                        ref.child(lobbyID).setValue(lobby);
                        break;
                    }
                }
                //If no good lobby was found, make a new lobby:
                if (lobbyID == null) {
                    makeNewLobby();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("could not read data", "The read failed: " + databaseError.getCode());
            }
        });


    }

    private void makeNewLobby() {
        lobby = new Lobby();
        lobby.addUser(userID);
        DatabaseReference ref = database.getReference().child("Lobbies").child("OpenLobbies");
        lobby.setID(ref.push().getKey());
        lobbyID = lobby.getId();
        database.getReference().child("Lobbies").child("OpenLobbies").child(lobbyID).setValue(lobby);
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void leaveLobby() {
        if (lobby == null) {
            return;
        }
        database.getReference().child("Lobbies").child("OpenLobbies").child(lobby.getId()).child("users").child(String.valueOf(lobby.getUsers().indexOf(userID))).removeValue();
    }

}
