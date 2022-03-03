package com.infinyquiz.userquestions;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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

public class ReviewQuestionActivity extends AppCompatActivity {

    private boolean hasRetrievedQuestion = false;
    private Question question = new Question();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewquestion);

        Button backToHomeBtn = (Button) findViewById(R.id.backToHomeBtn);
        backToHomeBtn.setOnClickListener(new MoveToActivityOnClickListener(new HomeActivity(), this));

        getQuestion();
    }

    //Method to get question data
    //Also sends it to setQuestion method, as
    private void getQuestion() {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("NotValidatedQuestions");
        // Attach a listener to read the data at our posts reference
        ArrayList<Question> questions = new ArrayList<>();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(hasRetrievedQuestion){
                    return;
                }
                hasRetrievedQuestion = true;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Question question = snapshot.getValue(Question.class);
                    questions.add(question);
                }
                //pick random question
                question = questions.get(new Random().nextInt(questions.size()));
                setQuestion(question);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("could not read data", "The read failed: " + databaseError.getCode());
            }
        });
    }

    //sets the correct question into UI;
    private void setQuestion(Question question){
        //TODO: provide correct implementation
        System.out.println(question);
        System.out.println(question.getQuestion());
        System.out.println(question.getCorrectOption());
        System.out.println(question.getOptions());
    }

}