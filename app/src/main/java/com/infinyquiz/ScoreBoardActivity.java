package com.infinyquiz;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infinyquiz.datarepresentation.Game;
import com.infinyquiz.datarepresentation.Question;
import com.infinyquiz.datarepresentation.RandomGame;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

public class ScoreBoardActivity extends AppCompatActivity {

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        Button leaveGameBtn = (Button) findViewById(R.id.leaveMatchBtn);
        leaveGameBtn.setOnClickListener(new MoveToActivityOnClickListener(new HomeActivity(), this));

        startDataBaseRead();
    }

    private void startDataBaseRead(){
        String gameID = getIntent().getStringExtra("gameID");
        database = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference ref = database.getReference().child("Lobbies").child("gameLobbies").child(gameID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Game game = snapshot.getValue(RandomGame.class);
                if(game.haveAllPlayersAnswered()){
                    startTimer();
                }
                //set UI
                setUI(game);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("could not read data", "The read failed: " + error.getCode());
            }
        });

    }

    //A function to start a timer to return to the GameActivity
    private void startTimer(){

    }

    //A function to set the UI accordingly
    private void setUI(Game game){
        Question question = game.getCurrentQuestion();
        TextView questionTV = (TextView) findViewById(R.id.questionTV);
        questionTV.setText(question.getQuestion().trim());

        TextView correctOption = (TextView) findViewById(R.id.createQuestionBtn);
        correctOption.setText(question.getCorrectOption().trim());

        TextView scoreBoardTV = (TextView) findViewById(R.id.scoreboardTV);
        scoreBoardTV.setText(game.getScoreboard().toString().trim());
    }

}
