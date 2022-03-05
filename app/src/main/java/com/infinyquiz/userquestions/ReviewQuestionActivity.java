package com.infinyquiz.userquestions;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infinyquiz.HomeActivity;
import com.infinyquiz.R;
import com.infinyquiz.datarepresentation.Question;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

import java.util.ArrayList;
import java.util.Random;

public class ReviewQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    //TODO implement image of question!

    private boolean hasRetrievedQuestion = false;
    private Question question = new Question();
    private String reference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewquestion);

        getQuestion();

        //Set buttons
        Button backToHomeBtn = (Button) findViewById(R.id.quitToHomeBtn);
        backToHomeBtn.setOnClickListener(new MoveToActivityOnClickListener(new HomeActivity(), this));

        Button positiveVoteBtn = (Button) findViewById(R.id.positiveVoteBtn);
        Button negativeVoteBtn = (Button) findViewById(R.id.voteNegativeBtn);
        //Set listener (this class)
        positiveVoteBtn.setOnClickListener(this);
        negativeVoteBtn.setOnClickListener(this);
    }

    //Method to get question data
    //Also sends it to setQuestion method, as
    private void getQuestion() {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("NotValidatedQuestions");
        // Attach a listener to read the data at our posts reference
        ArrayList<Question> questions = new ArrayList<>();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(hasRetrievedQuestion){
                    return;
                }
                hasRetrievedQuestion = true;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Question question = snapshot.getValue(Question.class);
                    question.setReference(snapshot.getKey());
                    questions.add(question);
                }
                //pick random question
                question = questions.get(new Random().nextInt(questions.size()));
                setQuestion(question);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("could not read data", "The read failed: " + databaseError.getCode());
            }
        });
    }

    //sets the correct question into UI;
    private void setQuestion(Question question){
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
        for(String option : question.getOptions()){
            optionsString.append(option).append(",");
        }
        optionsString = new StringBuilder(optionsString.substring(0, optionsString.length() - 1));
        optionsString.append(".");
        optionsTV.setText(optionsString.toString());
    }


    @Override
    public void onClick(View view) {
        //If question is not yet loaded, do nothing
        if(question.isEmpty()){
            return; //Do nothing
        }
        DatabaseReference ref = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("NotValidatedQuestions");
        ref.child(question.getReference()).child("test").push().setValue(1);

    }

}