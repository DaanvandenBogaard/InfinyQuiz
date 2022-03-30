package com.infinyquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.infinyquiz.auth.LoginActivity;
import com.infinyquiz.auth.RegisterActivity;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;
import com.infinyquiz.onclicklistener.logOutOnClickListener;
import com.infinyquiz.userquestions.CreateQuestionActivity;
import com.infinyquiz.userquestions.ReviewQuestionActivity;


public class SettingsActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button homeBtn = (Button) findViewById(R.id.toHomeBtn);
        homeBtn.setOnClickListener(new MoveToActivityOnClickListener(new HomeActivity(), this));
    }
}