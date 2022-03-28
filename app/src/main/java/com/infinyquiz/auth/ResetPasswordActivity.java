package com.infinyquiz.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.infinyquiz.R;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

/* An activity where users can request the service to reset their password.
 * In order to realise this service, we user Firebase's authentication tool.
 */
public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        //Set the button to move to the LoginActivity
        Button toLoginBtn = (Button) findViewById(R.id.moveToLoginBtn);
        toLoginBtn.setOnClickListener(new MoveToActivityOnClickListener(new LoginActivity(), this));

        //Set the button to request a new password
        Button thisButton = (Button) findViewById(R.id.resetPasswordBtn);
        thisButton.setOnClickListener(this);
    }

    /* In this method we will ask firebase to reset the user's password
     *
     * @pre {@code email!= null & !email.equals("")}
     * @post we will have requested firebase to use its password reset service for the email
     * that was provided.
     */
    @Override
    public void onClick(View view) {
        //intent which we use to move back to login
        Intent intent = new Intent(this, LoginActivity.class);
        //get text:
        EditText emailTV = (EditText) findViewById(R.id.editTextTextEmailAddress);
        String email = emailTV.getText().toString().trim();
        if (email == null || email.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(intent);
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "Could not reset email, try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}