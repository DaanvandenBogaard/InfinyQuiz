package com.infinyquiz.FriendsManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.infinyquiz.FriendsManagement.SearchFriendActivity;
import com.infinyquiz.R;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

public class FriendsActitivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_actitivity);

        Button addFriendsBtn = (Button) findViewById(R.id.addFriendsBtn);
        addFriendsBtn.setOnClickListener(new MoveToActivityOnClickListener(new SearchFriendActivity(), this));
    }
}