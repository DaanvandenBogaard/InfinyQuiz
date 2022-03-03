package com.infinyquiz.userquestions;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.infinyquiz.HomeActivity;
import com.infinyquiz.QuestionRetriever;
import com.infinyquiz.R;
import com.infinyquiz.datarepresentation.Question;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

public class ReviewQuestionActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewquestion);

        Button backToHomeBtn = (Button) findViewById(R.id.backToHomeBtn);
        backToHomeBtn.setOnClickListener(new MoveToActivityOnClickListener(new HomeActivity(),this));

        QuestionRetriever qR = new QuestionRetriever(this);
        Question question = qR.getReviewQuestion();

        TextView debugTV = findViewById(R.id.debugTV);
    }

    public Question getRandomQuestion(){
        //Make connection to database:

        //Retrieve random question from database:
        return new Question();
    }
}