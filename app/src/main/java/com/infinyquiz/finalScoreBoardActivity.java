package com.infinyquiz;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infinyquiz.datarepresentation.Game;
import com.infinyquiz.datarepresentation.RandomGame;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class finalScoreBoardActivity extends AppCompatActivity {

    //Constants used to greet user
    final private String congratzMessage = "You won!";
    final private String loseMessage = "You lost.";

    //Value of the game currently used.
    private Game game;

    //The current user ID
    final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //UI elements:
    private TextView finalMessageTv;
    private TextView top3TV;

    private Button leaveMatchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalscoreboard);

        setUIElements();
        leaveMatchBtn.setOnClickListener(new MoveToActivityOnClickListener(new HomeActivity(), this));

        getGame();
    }

    /* Sets UI elements appropriately
     *
     * @pre none
     * @post UI global variables are initialised using {@code findById()}.
     * @modifies all UI related global elements of this class
     * @throws none
     */
    private void setUIElements(){
        finalMessageTv = (TextView) findViewById(R.id.finalMessageTV);
        top3TV = (TextView) findViewById(R.id.top3TV);

        leaveMatchBtn = (Button) findViewById(R.id.leaveMatchBtn);
    }

    //Sets the {@code Game game} global variable using firebase real time database.
    //
    private void getGame(){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/");;
        DatabaseReference ref = database.getReference().child("Lobbies").child("gameLobbies").child(getIntent().getStringExtra("gameID"));
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                game = dataSnapshot.getValue(RandomGame.class);
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("could not read data", "The read failed: " + databaseError.getCode());
            }
        });
    }

    //A function to set the UI elements appropriately.
    private void updateUI(){
        String[] top3 = getTop3();
        String message;
        if (top3[0].equals(userID)) {
            message = congratzMessage;
        }
        else{
            message = loseMessage;
        }

        finalMessageTv.setText(message);
        top3TV.setText(top3.toString().trim());
    }

    /* Returns an array containing the top 3 scored people
     *
     * @pre {@Code game != null}
     * @returns An array {@code top3} containing 3 strings such that:
     * {@code top3[0] = (String) (the userID of the user who has scored the highest) &
     *  top3[1] = (String) (the userID of the user who has scored the second highest) &
     *  top3[2] = (String) (the userID of the user who has scored the third highest (may be null) }
     *
     * @modifies none
     *
     */
    private String[] getTop3(){
        String[] top3 = new String[3];
        for(int i = 0; i < 3; i++) {
            top3[i] = null;
        }
        Map<String, Integer> scoreboard = game.getScoreboard();
        List<String> users = game.getUsers();
        //iterate over all players, find largest value 3 times.
        for(int i = 0; i< top3.length; i++) {
            String highScoreID = findHighestScore(scoreboard, users);
            users.remove(highScoreID);
            top3[i] = highScoreID;

            if(users.size() == 0){
                break;
            }
        }

        return top3;
    }

    private String findHighestScore(Map<String, Integer> dictionary, List<String> users){
        int maxScore = 0;
        String highestUser = ""; //initialise to empty string so compiler won't complain
        for(String user : users){
            if( maxScore < dictionary.get(user)) {
                highestUser = user;
                maxScore = dictionary.get(user);
            }
        }
        return highestUser;
    }


}
