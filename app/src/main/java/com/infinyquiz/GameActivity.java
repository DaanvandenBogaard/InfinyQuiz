package com.infinyquiz;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.infinyquiz.datarepresentation.Lobby;
import com.infinyquiz.datarepresentation.Question;
import com.infinyquiz.datarepresentation.RandomGame;
import com.infinyquiz.datarepresentation.UserDataConverter;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    //The ID of the game
    private String gameID;

    //The ID of the lobby
    private String lobbyID;

    //The firebase database reference
    private FirebaseDatabase database;

    //The game object we are playing
    private Game game;

    //The question currently being discussed
    private Question curQuestion = new Question();

    //The vote for the category
    private String vote;

    //The current user ID
    final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //list of cateogries
    private String[] categories;

    //Buttons of options
    private Button optionA, optionB, optionC, optionD;

    //boolean to know if current question has been answered.
    private boolean hasAnsweredQuestion = false;

    //Time of delay
    private float DELAY = 10000;

    //see if timer has been set
    private boolean timerHasBeenSet = false;

    //The progress bar UI element
    private ProgressBar timerPB;

    private Map<String, ArrayList<Question>> questionData = new HashMap<>();

    //Handler object for using delay
    Handler handler = new Handler();
    //Runnable object for using delay
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        categories = getResources().getStringArray(R.array.categories);

        Button leaveGameBtn = (Button) findViewById(R.id.leaveGameBtn);
        leaveGameBtn.setOnClickListener(new MoveToActivityOnClickListener(new HomeActivity(), this));

        timerPB = (ProgressBar) findViewById(R.id.timerPB);

        optionA = (Button) findViewById(R.id.optionABtn);
        optionB = (Button) findViewById(R.id.optionBBtn);
        optionC = (Button) findViewById(R.id.optionCBtn);
        optionD = (Button) findViewById(R.id.optionDBtn);
        optionA.setOnClickListener(this);
        optionB.setOnClickListener(this);
        optionC.setOnClickListener(this);
        optionD.setOnClickListener(this);

        setUpFirebase();
        registerUserToGame();
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
        //database.getReference().child("Lobbies").child("gameLobbies").child(gameID).child("joinedPlayers").setValue(userID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                game = dataSnapshot.getValue(RandomGame.class);
                if (!game.hasPlayerJoined(userID)) {
                    game.joinPlayer(userID);
                    game.addVote(vote);
                    updateFirebaseGame(game);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("could not read data", "The read failed: " + databaseError.getCode());
            }
        });
        getQuestions();
    }

    /* A function that retrieves the questions
     *
     */
    private void getQuestions() {
        DatabaseReference ref = database.getReference().child("ValidatedQuestions");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ArrayList<Question> questions = new ArrayList<>();
                    for (DataSnapshot question : data.getChildren()) {
                        questions.add(question.getValue(Question.class));
                    }
                    questionData.put(data.getKey(), questions);
                }
                setDataListener();
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
                if(hasAnsweredQuestion || !game.allPlayersJoined()){
                    return;
                }
                if(game.allPlayersJoined()){
                    removeOpenLobby();
                }
                //Will only be executed once
                if(game.getQuestions() == null){
                    //set questions
                    //find highest rated category
                    String votedCategory = mostPopularCategory();
                    ArrayList<Question> allQuestions = questionData.get(votedCategory);
                    ArrayList<Question> chosenQuestions = new ArrayList<>();
                    for (int i = 0; i < Game.NUMBER_OF_QUESTIONS; i++) {
                        Random rand = new Random();
                        int j = rand.nextInt(allQuestions.size());
                        chosenQuestions.add(allQuestions.get(j));
                        allQuestions.remove(j);
                    }
                    game.setQuestions(chosenQuestions);
                    curQuestion = game.getCurrentQuestion();
                    updateFirebaseGame(game);
                }
                if(game.haveAllPlayersReturned()){
                    game.clearReturnedPlayers();
                    game.incrementQuestionIndex();
                    curQuestion = game.getCurrentQuestion();
                    updateFirebaseGame(game);
                }
                updateUI();
                setTimerForQuestion();
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


    /* Updates game in firebase
     *
     * @pre {@code database != null}
     * @post updated the game in firebase
     * @modifies firebase database
     */
    private void updateFirebaseGame(Game _game) {
        database.getReference().child("Lobbies").child("gameLobbies").child(gameID).setValue(_game);
    }

    /* updates to UI according to game
     *
     */
    private void updateUI() {
        TextView questionTV = (TextView) findViewById(R.id.questionTV);
        Question question = game.getCurrentQuestion();
        questionTV.setText(question.getQuestion());

        //get options
        ArrayList<String> options = question.getOptions();
        Collections.shuffle(options); //We get a random shuffle to make sure the correct answer does not stay in one point
        optionA.setText(options.get(0));
        optionB.setText(options.get(1));
        optionC.setText(options.get(2));
        optionD.setText(options.get(3));

        //Set image:
        if(question.hasImage()){
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(UserDataConverter.decodeImage(question.getPictureID()));
        }
    }

    /* Returns the most common category in the votes
     *
     * @pre {@code game.getCategoryVotes() != null}
     * @returns the most common element in {@code game.getCategoryVotes}
     */
    private String mostPopularCategory() {
        ArrayList<String> votes = game.getCategoryVotes();
        Map<String, Integer> votesCount = new HashMap<>();
        for (String category : categories) {
            votesCount.put(category, 0);
        }
        for (String vote : votes) {
            votesCount.put(vote, votesCount.get(vote) + 1);
        }

        //Find highest number
        int max = 0;
        String votedCategory = "";

        for (Map.Entry<String, Integer> entry : votesCount.entrySet()) {
            if (entry.getValue() >= max) {
                max = entry.getValue();
                votedCategory = entry.getKey();
            }
        }

        return votedCategory;
    }

    /* This function will set a timer for when we start a question. Once this timer is over, we move the users
     *
     */

    Timer timer;

    private void setTimerForQuestion() {
        if (timerHasBeenSet) {
            return;
        }
        timerPB.setProgress(0);
        timerHasBeenSet = true;
        timer = new java.util.Timer();
        timer.schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        moveToScoreBoard();
                    }
                },
                (long) DELAY
        );
    }

    //Functions to start timer
    @Override
    protected void onResume() {
        handler.postDelayed(runnable = new Runnable(){
            public void run(){
                handler.postDelayed(runnable,(long) DELAY/100);
                timerPB.incrementProgressBy(1);
            }
        },(long) DELAY/100);
        super.onResume();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //Stop the handler when the activity is not visible.
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        //if we have not moved to next question, do nothing
        if (hasAnsweredQuestion) {
            return;
        }
        hasAnsweredQuestion = true;

        Button clickedButton = (Button) view;
        String answer = clickedButton.getText().toString().trim();
        int points = 0;
        if (answer.equals(game.getCurrentQuestion().getCorrectOption().trim())) {
            //Answered correctly
            points = 100 - timerPB.getProgress();
        } else {
            //Answered incorrectly
            points = -10;
        }
        //Set points (will be updated when moving to next question).
        if (!game.getScoreboard().containsKey(userID)) {
            game.setScore(userID, 0);
        }
        game.setScore(userID, game.getScore(userID) + points);
        updateFirebaseGame(game);

        //Move to scoreboard.
        moveToScoreBoard();
    }

    //Temporarily move to the scoreboard activity and then come back.
    private void moveToScoreBoard() {
        hasAnsweredQuestion = true;
        Intent thisIntent;
        if(game.index == game.NUMBER_OF_QUESTIONS - 1){
            //GO TO FINAL SCREEN
            thisIntent = new Intent(this,finalScoreBoardActivity.class);
        } else {
            thisIntent = new Intent(this, ScoreBoardActivity.class);
        }
        thisIntent.putExtra("lobbyID", getIntent().getStringExtra("lobbyID"));
        thisIntent.putExtra("gameID", getIntent().getStringExtra("gameID"));
        thisIntent.putExtra("category", getIntent().getStringExtra("category"));
        timer.cancel();
        setUserToAnswered(thisIntent);

    }

    private boolean oneTimeUse = false;

    private void setUserToAnswered(Intent thisIntent){
        DatabaseReference ref = database.getReference().child("Lobbies").child("gameLobbies").child(gameID);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(oneTimeUse){
                    return;
                }
                oneTimeUse = true;
                game = dataSnapshot.getValue(RandomGame.class);
                if (!game.getAnsweredPlayers().contains(userID)) {
                    game.addPlayerToAnswered(userID);
                    updateFirebaseGame(game);
                }
                startActivity(thisIntent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("could not read data", "The read failed: " + databaseError.getCode());
            }
        });
    }
}