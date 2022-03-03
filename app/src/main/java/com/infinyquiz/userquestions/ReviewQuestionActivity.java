package com.infinyquiz.userquestions;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.infinyquiz.QuestionRetriever;
import com.infinyquiz.R;
import com.infinyquiz.datarepresentation.Question;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

public class ReviewQuestionActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewquestion);

        QuestionRetriever qR = new QuestionRetriever(this);
        Question question = qR.getQuestion();
        TextView debugTV = findViewById(R.id.debugTV);
        debugTV.setText(question.toString());
        System.out.print(question);
    }

    public Question getRandomQuestion(){
        //Make connection to database:

        //Retrieve random question from database:
        return new Question();
    }
}