package com.infinyquiz;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.infinyquiz.datarepresentation.Lobby;
import com.infinyquiz.datarepresentation.Question;
import com.infinyquiz.datarepresentation.RandomGame;
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
    private boolean hasAnsweredQuestion = true;

    //Time of delay
    private float DELAY = 15000;

    private Map<String, ArrayList<Question>> questionData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        categories = getResources().getStringArray(R.array.categories);

        Button leaveGameBtn = (Button) findViewById(R.id.leaveGameBtn);
        leaveGameBtn.setOnClickListener(new MoveToActivityOnClickListener(new HomeActivity(), this));

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
                    updateFirebaseGame();
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
                game = dataSnapshot.getValue(RandomGame.class);

                //If not all players have joined, we wait:
                if (!game.allPlayersJoined()) {
                    System.out.println("not all players have joined yet, wait!");
                    return;
                } else {
                    removeOpenLobby();
                }

                //Check if we are comming from ScoreBoardActivity:
                if(game.haveAllPlayersAnswered()){
                    game.clearAnsweredPlayers();
                    curQuestion = game.getNextQuestion();
                    updateFirebaseGame();
                }

                //If the game has not been set yet, we will set it. A game is not set if the
                //questions have yet to be set.
                if (game.getQuestions() == null || game.getQuestions().size() == 0) {
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
                    curQuestion = game.getNextQuestion();
                    hasAnsweredQuestion = false;
                    updateFirebaseGame();
                } else {
                    if (hasAnsweredQuestion) {
                        return;
                    }
                    //Run game behaviour
                    curQuestion = game.getCurrentQuestion();
                    updateUI();
                    //wait for {@code DELAY}
                    setTimerForQuestion();
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


    /* Updates game in firebase
     *
     * @pre {@code database != null}
     * @post updated the game in firebase
     * @modifies firebase database
     */
    private void updateFirebaseGame() {
        database.getReference().child("Lobbies").child("gameLobbies").child(gameID).setValue(game);
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
    private void setTimerForQuestion() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        try {
                            moveToScoreBoard();
                        }
                        catch(Exception e){
                            e.printStackTrace();
                            System.out.println("TEST");
                            System.out.println(e.getCause());
                            System.out.println("TEST");
                        }
                    }
                },
                (long) DELAY
        );
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
        //TODO IMPLEMENT TIME WITH POINTS
        int points = 0;
        if (answer.equals(game.getCurrentQuestion().getCorrectOption().trim())) {
            //Answered correctly
            points = 100;
        } else {
            //Answered incorrectly
            points = -10;
        }
        //Set points (will be updated when moving to next question).
        if (!game.getScoreboard().containsKey(userID)) {
            game.setScore(userID, 0);
        }
        game.setScore(userID, game.getScore(userID) + points);
        game.addPlayerToAnswered(userID);
        updateFirebaseGame();

        //Move to scoreboard.
        moveToScoreBoard();
    }

    //Temporarily move to the scoreboard activity and then come back.
    private void moveToScoreBoard() {
        Intent intent = new Intent(this, ScoreBoardActivity.class);
        intent.putExtra("lobbyID", getIntent().getStringExtra("lobbyID"));
        intent.putExtra("gameID", getIntent().getStringExtra("gameID"));
        intent.putExtra("category", getIntent().getStringExtra("category"));
        game.addPlayerToAnswered(userID);
        updateFirebaseGame();
        startActivity(intent);
    }
}