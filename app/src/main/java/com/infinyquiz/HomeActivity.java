package com.infinyquiz;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.infinyquiz.auth.RegisterActivity;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;
import com.infinyquiz.userquestions.CreateQuestionActivity;
import com.infinyquiz.userquestions.ReviewQuestionActivity;


public class HomeActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Set buttons:
        //TODO set play now button
        //TODO set custom match button
        Button createQuestionBtn = (Button) findViewById(R.id.createQuestionBtn);
        createQuestionBtn.setOnClickListener(new MoveToActivityOnClickListener(new CreateQuestionActivity(), this));
        Button reviewQuestionBtn = (Button) findViewById(R.id.rateQuestionBtn);
        reviewQuestionBtn.setOnClickListener(new MoveToActivityOnClickListener(new ReviewQuestionActivity(), this));
    }
}