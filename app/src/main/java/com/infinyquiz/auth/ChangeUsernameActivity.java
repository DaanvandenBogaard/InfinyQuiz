package com.infinyquiz.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.infinyquiz.ProfileActivity;
import com.infinyquiz.R;
import com.infinyquiz.datarepresentation.User;
import com.infinyquiz.datarepresentation.UserDataConverter;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

import java.util.Timer;
import java.util.TimerTask;

/* This activity is used to give the user the possibility to change the user name.
 * This is done by entering a new username in a text box and then we simply change the username
 * (if it is not empty). Furthermore, this activity should display the current user name so that the
 * user is aware of what he/she is changing.
 */
public class ChangeUsernameActivity extends AppCompatActivity implements View.OnClickListener {

    //The current user ID
    final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //Data converted
    final private UserDataConverter converter = new UserDataConverter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeusername);

        //Set button to go back to the ProfileActivity
        Button moveToProfileActivityBtn = (Button) findViewById(R.id.toProfileActivityBtn);
        moveToProfileActivityBtn.setOnClickListener(new MoveToActivityOnClickListener(new ProfileActivity(), this));

        //Set the submit button (the listener is defined below in onClick).
        Button submitBtn = (Button) findViewById(R.id.submitUsernameBtn);
        submitBtn.setOnClickListener(this);

        //Set current username:
        if (converter.isReady()) {
            setCurUsernameUI();
        } else {
            //We wait one second and then assume that {@code converter.isReady()}.
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    setCurUsernameUI();
                }
            }, 1000);
        }

    }

    /* A function to set the current username in UI
     *
     * @pre {@code converter.isReady()}
     * @post {curUsernameTV.getText = "Current username:  " + CURRENT_USER_NAME}
     * @modifies {@code curUsernameTv}
     */
    private void setCurUsernameUI() {
        TextView curUsernameTV = (TextView) findViewById(R.id.curUsernameTV);
        curUsernameTV.setText("Current username:  " + converter.getUserName(userID));
    }

    /* This function is called if (and only if) the submitBtn is clicked.
     * We then check if the newly entered user name is valid, if it is we change it in firebase.
     *
     * @param {@code view} the view on which this listener acts (must always be submitBtn)
     * @pre {@code view.getId = R.id.submitUsernameBtn && newUsername.getText() != null}
     * @post The username of the user with the ID {@code userID} is changed to the new username
     * @modifies firebase realtime database
     */
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
