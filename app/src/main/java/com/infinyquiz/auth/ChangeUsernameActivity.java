package com.infinyquiz.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.infinyquiz.FriendsManagement.FriendsActitivity;
import com.infinyquiz.ProfileActivity;
import com.infinyquiz.R;
import com.infinyquiz.datarepresentation.User;
import com.infinyquiz.datarepresentation.UserDataConverter;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

public class ChangeUsernameActivity extends AppCompatActivity implements View.OnClickListener {

    //The current user ID
    final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //Data converted
    final private UserDataConverter converter = new UserDataConverter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeusername);

        Button moveToProfileActivityBtn = (Button) findViewById(R.id.toProfileActivityBtn);
        moveToProfileActivityBtn.setOnClickListener(new MoveToActivityOnClickListener(new ProfileActivity(), this));

        Button submitBtn = (Button) findViewById(R.id.submitUsernameBtn);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //Get username
        EditText newUsername = (EditText) findViewById(R.id.usernameET);
        String username = newUsername.getText().toString().trim();
        if (username == "" || username.isEmpty()) {
            newUsername.setError("Username is required.");
            newUsername.requestFocus();
            return;
        }
        Intent intent = new Intent(this, ProfileActivity.class);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/");
        User curUser = converter.getUser(userID);
        curUser.setUsername(username);
        database.getReference().child("Users").child(userID).setValue(curUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ChangeUsernameActivity.this, "Changed username", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                } else {
                    Toast.makeText(ChangeUsernameActivity.this, "Could not change username, try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
