package com.infinyquiz;

import android.content.Intent;
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
import com.infinyquiz.datarepresentation.Question;
import com.infinyquiz.datarepresentation.RandomGame;
import com.infinyquiz.datarepresentation.UserDataConverter;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ScoreBoardActivity extends AppCompatActivity {

    private FirebaseDatabase database;

    private long DELAY = 5000;

    private Game game;

    DatabaseReference ref;

    private boolean timerHasBeenSet = false;

    //The current user ID
    final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //Object to convert userIDs to usernames
    final private UserDataConverter converter = new UserDataConverter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        Button leaveGameBtn = (Button) findViewById(R.id.leaveMatchBtn);
        leaveGameBtn.setOnClickListener(new MoveToActivityOnClickListener(new HomeActivity(), this));

        startDataBaseRead();
    }

    private void startDataBaseRead() {
        String gameID = getIntent().getStringExtra("gameID");

        database = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/");
        ref = database.getReference().child("Lobbies").child("gameLobbies").child(gameID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                game = snapshot.getValue(RandomGame.class);
                if (timerHasBeenSet || !game.haveAllPlayersAnswered()) {
                    return;
                }
                setUI(game);

                game.clearJoinedPlayers();
                ref.setValue(game);

                if (game.haveAllPlayersAnswered()) {
                    startTimer();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("could not read data", "The read failed: " + error.getCode());
            }
        });

    }


    //A function to start a timer to return to the GameActivity
    private void startTimer() {
        if (timerHasBeenSet) {
            return;
        }
        startDataBaseRead();
        timerHasBeenSet = true;
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        moveToGameActivity();
                    }
                },
                DELAY
        );
    }

    //A function to set the UI accordingly
    private boolean hasSetUI = false;
    private void setUI(Game game) {
        if(hasSetUI){
            return;
        }
        Question question = game.getCurrentQuestion();
        TextView questionTV = (TextView) findViewById(R.id.questionTV);
        questionTV.setText(question.getQuestion().trim());

        TextView correctOption = (TextView) findViewById(R.id.finalMessageTV);
        correctOption.setText(question.getCorrectOption().trim());

        TextView scoreBoardTV = (TextView) findViewById(R.id.scoreboardTV);
        //Make new scoreboard with usernames:
        if (converter.isReady()) {
            scoreBoardTV.setText(getScores(game.getScoreboard()));
        } else {
            //We wait one second and then assume that {@code converter.isReady()}.
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scoreBoardTV.setText(getScores(game.getScoreboard()));
                        }
                    });

                }
            }, 1000);
        }
    }

    //Function to get the scoreboard
    public String getScores(Map<String, Integer> scoreboard) {
        String scores = "";
        ArrayList<Integer> listOfScores = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : scoreboard.entrySet()) {
            listOfScores.add(entry.getValue());
        }
        //Sort in reverse order
        Collections.sort(listOfScores, Collections.reverseOrder());
        listOfScores.removeAll(Collections.singleton(null));
        //build the string:
        for (int i = 1; i <= listOfScores.size(); i++) {
            //find a key that suits
            for (Map.Entry<String, Integer> entry : scoreboard.entrySet()) {
                if (entry.getValue() == listOfScores.get(i - 1)) {
                    scores += i + ") " + converter.getUserName(entry.getKey()) + ": " + entry.getValue();
                    if (i != listOfScores.size()) {
                        scores += "\n";
                    }

                }
            }
        }
        hasSetUI = true;

        return scores;
    }

    private void moveToGameActivity() {
        Intent intent = new Intent(this, GameActivity.class);
        ref.setValue(game);
        intent.putExtra("lobbyID", getIntent().getStringExtra("lobbyID"));
        intent.putExtra("gameID", getIntent().getStringExtra("gameID"));
        intent.putExtra("category", getIntent().getStringExtra("category"));
        intent.putExtra("index", getIntent().getIntExtra("index", 0));
        startActivity(intent);
    }


}
