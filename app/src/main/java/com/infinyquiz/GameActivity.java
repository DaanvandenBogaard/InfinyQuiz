package com.infinyquiz;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infinyquiz.datarepresentation.Game;
import com.infinyquiz.datarepresentation.Lobby;
import com.infinyquiz.datarepresentation.RandomGame;

import java.util.Map;

public class GameActivity extends AppCompatActivity {

    //The ID of the game
    private String gameID;

    //The ID of the lobby
    private String lobbyID;

    //The firebase database reference
    private FirebaseDatabase database;

    //The game object we are playing
    private Game game;

    //The vote for the category
    private String vote;

    //The current user ID
    final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        setUpFirebase();
        registerUserToGame();
        setDataListener();
    }

    //Sets the value of the database, gameID and lobbyID variables.
    private void setUpFirebase() {
        database = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/");
        gameID = getIntent().getStringExtra("gameID");
        lobbyID = getIntent().getStringExtra("lobbyID");
        vote = getIntent().getStringExtra("category");
    }

    /* A function which ads this user to the list of users who have joined.
     * @pre database and gameID have been set correctly
     * @post in the database, the userID has been added to joined users.
     */
    private void registerUserToGame() {
        DatabaseReference ref = database.getReference().child("Lobbies").child("gameLobbies").child(gameID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                game = dataSnapshot.getValue(RandomGame.class);
                if (!game.hasPlayerJoined(userID)) {
                    game.joinPlayer(userID);
                    game.addVote(vote);
                    updateFirebaseGame();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("could not read data", "The read failed: " + databaseError.getCode());
            }
        });

    }

    /* sets the listener to the game data, where we will
     *
     * @pre the gameID, lobbyID and database variables are set correctly
     * @post sets the next question, waits for answers, goes to the scoreboard activity and returns
     * @modifies the behaviour of the game
     */
    private void setDataListener() {
        //get reference to game object
        DatabaseReference ref = database.getReference().child("Lobbies").child("gameLobbies").child(gameID);
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                game = dataSnapshot.getValue(RandomGame.class);

                //If not all players have joined, we wait:
                if (!game.allPlayersJoined()) {
                    System.out.println("not all players have joined yet, wait!");
                    return;
                } else {
                    removeOpenLobby();
                }

                //If the game has not been set yet, we will set it. A game is not set if the
                //questions have yet to be set.
                if (game.getQuestions() == null) {
                    //find highest rated category

                    game.setQuestions(vote);
                    updateFirebaseGame();
                } else {
                    //Run game behaviour
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("could not read data", "The read failed: " + databaseError.getCode());
            }
        });
    }

    /* Once all players have joined the game, we can remove the lobby.
     *
     * @pre database, and lobbyID have been set correctly.
     * @post the lobby is removed from the open lobbies.
     * @modifies firebase real time database.
     */
    private void removeOpenLobby() {
        database.getReference().child("Lobbies").child("OpenLobbies").child(lobbyID).removeValue();
    }

    private void updateFirebaseGame() {
        database.getReference().child("Lobbies").child("gameLobbies").child(gameID).setValue(game);
    }
}
