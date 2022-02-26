package com.infinyquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.infinyquiz.auth.LoginActivity;

public class MainActivity extends AppCompatActivity {
    /* TODO:
     * Logout button
     * Redirection to login activity
     * Check requirements to see other things main screen must do.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //If user is not logged in, redirect to login screen
        //TODO implement redirection to login screen.

        //Retrieve button:
        Button button = findViewById(R.id.button);

        //Retrieve text block:
        TextView txt = findViewById(R.id.resultView);

        //Has to be defined in this scope, not in scope of the listener.
        Intent switchActivityIntent = new Intent(this, LoginActivity.class);
        //Set button's listener:
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(switchActivityIntent);
            }
        });
    }
}