package com.infinyquiz.userquestions;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infinyquiz.HomeActivity;
import com.infinyquiz.R;
import com.infinyquiz.datarepresentation.Question;
import com.infinyquiz.datarepresentation.UserDataConverter;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class ReviewQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    //The minimum number of votes needed before we either validate or invalidate our question.
    //Should be "50" according to our specifications (MUST).
    private final int VALIDATION_THRESHOLD = 1; //=50; //TODO SET CORRECT VALUE

    //The percentage (value must be in the range of [0,1]) of positive votes after which a question
    //is accepted.
    //Should be "0.7f" according to our specifications (MUST).
    private final float ACCEPTANCE_PERCENTAGE = 0.7f; //=0.7f;

    //The question under review
    private Question question;

    //List of users' ID who have voted positive for this question
    private ArrayList<String> posVotes = new ArrayList<>();

    //List of users' ID who have voted negative for this question
    private ArrayList<String> negVotes = new ArrayList<>();

    /* Method that is called when starting this activity. It is responsible for setting buttons and
     * calling auxiliary methods.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewquestion);

        //Set a random question in UI: (handling of UI is done via a seperate method, called withing
        // getQuestion(), as only there we know when the data is being read).
        getQuestion();

        //Set buttons
        Button backToHomeBtn = (Button) findViewById(R.id.quitToHomeBtn);
        backToHomeBtn.setOnClickListener(new MoveToActivityOnClickListener(new HomeActivity(), this));

        Button positiveVoteBtn = (Button) findViewById(R.id.positiveVoteBtn);
        Button negativeVoteBtn = (Button) findViewById(R.id.voteNegativeBtn);
        Button reportBtn = (Button) findViewById(R.id.reportBtn);

        //Set listener (this class)
        positiveVoteBtn.setOnClickListener(this);
        negativeVoteBtn.setOnClickListener(this);
        reportBtn.setOnClickListener(this);
    }

    /*Method to get question data
     *Also sends it to setQuestion method, as
     *
     * @pre {@none}
     * @post {@code Question question} (global variable) a question that has not been approved yet and has not
     * been reviewed by the current user, i.e. the user with the ID
     * {@code FirebaseAuth.getInstance().getCurrentUser().getUid()}.
     * @modifies none
     * @throws DatabaseError if no connection could be made to the database
     */
    private void getQuestion(){
        Question[] returnQuestion = new Question[1];
        returnQuestion[0] = new Question();

        //Lists of possible questions which we may need to review:
        ArrayList<Question> questions = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("NotValidatedQuestions");
        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Question newQuestion = snapshot.getValue(Question.class);
                    newQuestion.setReference(snapshot.getKey());
                    //If the current user has already voted on this question, it will not be added
                    String curUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    posVotes = new ArrayList<>();
                    negVotes = new ArrayList<>();

                    for (DataSnapshot data : snapshot.child("posVote").getChildren()) {
                        String user = data.getValue(String.class);
                        posVotes.add(user);
                    }
                    for (DataSnapshot data : snapshot.child("negVote").getChildren()) {
                        String user = data.getValue(String.class);
                        negVotes.add(user);
                    }
                    if (!negVotes.contains(curUserID) && !posVotes.contains(curUserID)) {
                        questions.add(newQuestion);
                    }
                }
                //pick random question
                if (questions.size() == 0) {
                    question = new Question();
                } else {
                    question = questions.get(new Random().nextInt(questions.size()));
                }
                setQuestion(question);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("could not read data", "The read failed: " + databaseError.getCode());
            }
        });
    }

    /* sets the correct question into UI;
     *
     * @param {@code question}, the Question that will be set to UI.
     * @pre {question != null}
     * @post UI elements are set according to our requirements
     * @modifies none
     */
    private void setQuestion(Question question) {
        //Get UI elements
        final TextView questionTV = (TextView) findViewById(R.id.questionTV);
        final TextView correctOptionTV = (TextView) findViewById(R.id.correctAnswerTV);
        final TextView optionsTV = (TextView) findViewById(R.id.optionsTV);
        final TextView categoryTV = (TextView) findViewById(R.id.categoryTV);
        final TextView difficultyTV = (TextView) findViewById(R.id.difficulityTV);

        //set text accordingly
        questionTV.setText(question.getQuestion());
        correctOptionTV.setText(question.getCorrectOption());
        categoryTV.setText(question.getCategory());
        difficultyTV.setText(String.valueOf(question.getDifficulty()));

        //We first rewrite this string
        StringBuilder optionsString = new StringBuilder();
        for (String option : question.getOptions()) {
            optionsString.append(option).append(",");
        }
        optionsString = new StringBuilder(optionsString.substring(0, optionsString.length() - 1));
        optionsString.append(".");
        optionsTV.setText(optionsString.toString());

        //set image
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        if(question.hasImage()){
            imageView.setImageBitmap(UserDataConverter.decodeImage(question.getPictureID()));
        }
    }

    /* A method that will respond to the user's choice to either vote positively or negatively for this question.
     *
     * @pre none
     * @post if the question has received 50 votes, and 70% of the mare positive, then
     * the question will be moved to the ValidatedQuestions database instead of the notValidatedQuestions database.
     * Furthermore, this question will be removed from the notValidatedQUestion database.
     * @modifies FireBase real time database entry of {@code this.question}.
     * @throws none
     */
    @Override
    public void onClick(View view) {
        //If question is not yet loaded, do nothing
        if (question.isEmpty()) {
            return; //Do nothing
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference ref = database.getReference().child("NotValidatedQuestions");

        if(view.getId() == R.id.reportBtn){
            //Delete question from this database
            ref.removeValue();

            //Put question in reported question database
            database.getReference().child("ReportedQuestions").child(question.getCategory()).push().setValue(question);
        }

        //number of positive votes
        int numPosVotes = posVotes.size();
        //number of negative votes
        int numNegVotes = negVotes.size();

        //Set whether or not this was a positive vote.
        String posOrNeg = "posVote";;
        if (view.getId() == R.id.voteNegativeBtn) {
            posOrNeg = "negVote";
            numNegVotes++;
        } else if (view.getId() == R.id.positiveVoteBtn){
            posOrNeg = "posVote";
            numPosVotes++;
        }

        ref.child(question.getReference()).child(posOrNeg).push().setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //Check if question must be moved to validated questions
        if (numPosVotes + numNegVotes >= VALIDATION_THRESHOLD) { //if threshold is exceeded
            if (numPosVotes / (numNegVotes + numPosVotes) >= ACCEPTANCE_PERCENTAGE) {
                //Move question to validated questions
                database.getReference().child("ValidatedQuestions").child(question.getCategory()).push().setValue(question);
            }
            //Remove question from database "NotValidatedQuestions"
            database.getReference().child("NotValidatedQuestions").child(question.getReference()).removeValue();

        }

        startActivity(new Intent(this, ReviewQuestionActivity.class));
    }

}