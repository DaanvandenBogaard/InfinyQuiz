package com.infinyquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.infinyquiz.FriendsManagement.FriendsActitivity;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //TODO: Change account data Activity and set button.
        Button manageFriendsBtn = (Button) findViewById(R.id.ManageFriendsBtn);
        manageFriendsBtn.setOnClickListener(new MoveToActivityOnClickListener(new FriendsActitivity(), this));
        //TODO: Add log out etc.
    }
}