package com.infinyquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;
import com.infinyquiz.auth.LoginActivity;
import com.infinyquiz.auth.RegisterActivity;
import com.infinyquiz.userquestions.CreateQuestionActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //If the user is loged in, we will redirect the user to the home screen.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
        }

        //Set buttons and their OnClicklisteners
        Button createAccBtn = (Button) findViewById(R.id.createAccBtn);
        Button startLoginBtn = (Button) findViewById(R.id.startLoginBtn);
        createAccBtn.setOnClickListener(new MoveToActivityOnClickListener(new RegisterActivity(), this));
        startLoginBtn.setOnClickListener(new MoveToActivityOnClickListener(new LoginActivity(), this));

    }
}