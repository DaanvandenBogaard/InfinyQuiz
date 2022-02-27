package com.infinyquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;
import com.infinyquiz.auth.LoginActivity;
import com.infinyquiz.auth.RegisterActivity;

public class MainActivity extends AppCompatActivity {
    /* TODO:
     * Redirection to home if user is logged in already
     * Check requirements to see other things main screen must do.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set buttons and their OnClicklisteners
        Button createAccBtn = (Button) findViewById(R.id.createAccBtn);
        Button startLoginBtn = (Button) findViewById(R.id.startLoginBtn);
        createAccBtn.setOnClickListener(new MoveToActivityOnClickListener(new RegisterActivity(), this));
        startLoginBtn.setOnClickListener(new MoveToActivityOnClickListener(new LoginActivity(),this));

    }
}