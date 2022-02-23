package com.infinyquiz.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.infinyquiz.MoveToActivityOnClickListener;
import com.infinyquiz.R;

public class LoginActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button button = findViewById(R.id.toRegister);
        button.setOnClickListener(new MoveToActivityOnClickListener(new RegisterActivity(), this));
    }
}