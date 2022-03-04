package com.infinyquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;
import com.infinyquiz.auth.LoginActivity;
import com.infinyquiz.auth.RegisterActivity;
import com.infinyquiz.userquestions.CreateQuestionActivity;

public class MainActivity extends AppCompatActivity {
    /* TODO:
     * Redirection to home if user is logged in already
     * Check requirements to see other things main screen must do.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: delelete this is just for easy testing
        startActivity(new Intent(this, HomeActivity.class));
        //TODO: end delete

        //Set buttons and their OnClicklisteners
        Button createAccBtn = (Button) findViewById(R.id.createAccBtn);
        Button startLoginBtn = (Button) findViewById(R.id.startLoginBtn);
        createAccBtn.setOnClickListener(new MoveToActivityOnClickListener(new RegisterActivity(), this));
        startLoginBtn.setOnClickListener(new MoveToActivityOnClickListener(new LoginActivity(),this));

    }
}