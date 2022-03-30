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


public class HomeActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Set buttons:
        Button logoutBtn = (Button) findViewById(R.id.logOutBtn);
        logoutBtn.setOnClickListener((View.OnClickListener) new logOutOnClickListener(new LoginActivity(), this));
        Button playBtn = (Button) findViewById(R.id.randomMatchBtn);
        playBtn.setOnClickListener(new MoveToActivityOnClickListener(new MatchMakingActivity(), this));
        Button createQuestionBtn = (Button) findViewById(R.id.createQuestionBtn);
        createQuestionBtn.setOnClickListener(new MoveToActivityOnClickListener(new CreateQuestionActivity(), this));
        Button reviewQuestionBtn = (Button) findViewById(R.id.rateQuestionBtn);
        reviewQuestionBtn.setOnClickListener(new MoveToActivityOnClickListener(new ReviewQuestionActivity(), this));
        Button profileBtn = (Button) findViewById(R.id.profileBtn);
        profileBtn.setOnClickListener(new MoveToActivityOnClickListener(new ProfileActivity(), this));
        Button settingsBtn = (Button) findViewById(R.id.settingsBtn);
        settingsBtn.setOnClickListener(new MoveToActivityOnClickListener(new SettingsActivity(), this));
    }
}