package com.infinyquiz.userquestions;

import android.content.Intent;
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
import com.infinyquiz.HomeActivity;
import com.infinyquiz.R;
import com.infinyquiz.datarepresentation.Question;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class ReviewQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    //TODO implement image of question!

    //A boolean stating whether or not the question has been selected from firebase
    private boolean hasRetrievedQuestion = false;

    //The question under review
    private Question question = new Question();

    //List of users' ID who have voted positive for this question
    private ArrayList<String> posVotes = new ArrayList<>();

    //List of users' ID who have voted negative for this question
    private ArrayList<String> negVotes = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewquestion);

        //Set a random question in UI:
        setQuestion(getQuestion());

        //Set buttons
        Button backToHomeBtn = (Button) findViewById(R.id.quitToHomeBtn);
        backToHomeBtn.setOnClickListener(new MoveToActivityOnClickListener(new HomeActivity(), this));

        Button positiveVoteBtn = (Button) findViewById(R.id.positiveVoteBtn);
        Button negativeVoteBtn = (Button) findViewById(R.id.voteNegativeBtn);
        //Set listener (this class)
        positiveVoteBtn.setOnClickListener(this);
        negativeVoteBtn.setOnClickListener(this);
    }

    /*Method to get question data
     *Also sends it to setQuestion method, as
     *
     * @pre {@none}
     * @returns {@code Question question} a question that has not been approved yet and has not
     * been reviewed by the current user, i.e. the user with the ID
     * {@code FirebaseAuth.getInstance().getCurrentUser().getUid()}.
     * @modifies none
     * @throws DatabaseError if no connection could be made to the database
     */
    private Question getQuestion() {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("NotValidatedQuestions");
        // Attach a listener to read the data at our posts reference
        ArrayList<Question> questions = new ArrayList<>();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (hasRetrievedQuestion) {
                    return;
                }
                hasRetrievedQuestion = true;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Question question = snapshot.getValue(Question.class);
                    question.setReference(snapshot.getKey());
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
                        questions.add(question);
                    }
                }
                //pick random question
                if (questions.size() == 0) {
                    question = new Question();
                }
                else {
                    question = questions.get(new Random().nextInt(questions.size()));
                }
                setQuestion(question);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("could not read data", "The read failed: " + databaseError.getCode());
            }
        });
        return question;
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
        TextView questionTV = (TextView) findViewById(R.id.questionTV);
        TextView correctOptionTV = (TextView) findViewById(R.id.correctAnswerTV);
        TextView optionsTV = (TextView) findViewById(R.id.optionsTV);
        TextView categoryTV = (TextView) findViewById(R.id.categoryTV);
        TextView difficultyTV = (TextView) findViewById(R.id.difficulityTV);

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
        String posOrNeg = "posVote";
        if (view.getId() == R.id.voteNegativeBtn) {
            posOrNeg = "negVote";
        }
        DatabaseReference ref = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("NotValidatedQuestions");
        ref.child(question.getReference()).child(posOrNeg).push().setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //Check if question must be moved to validated questions
        //TODO implement

        startActivity(new Intent(this, ReviewQuestionActivity.class));
    }

}