package com.infinyquiz.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.infinyquiz.MoveToActivityOnClickListener;
import com.infinyquiz.R;

public class LoginActivity extends AppCompatActivity {

    private EditText emailET, passwordET;

    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button registerBtn = (Button) findViewById(R.id.toRegister);
        registerBtn.setOnClickListener(new MoveToActivityOnClickListener(new RegisterActivity(), this));

        emailET = (EditText) findViewById(R.id.loginEmailInput);
        passwordET = (EditText) findViewById(R.id.loginPasswordInput);

        mAuth = FirebaseAuth.getInstance();

        Button loginBtn = (Button) findViewById(R.id.LoginInputBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] input = retrieveInput();
                String email = input[0];
                String password = input[1];
                validateLoginInput(email, password);
                login(email, password);
            }
        });
    }

    private String[] retrieveInput() throws IllegalArgumentException {
        if (emailET == null || passwordET == null) {
            throw new IllegalArgumentException("One of the views is not instantiated correctly.");
        }
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        return new String[] {email, password};
    }

    private void validateLoginInput(String email, String password) {

        //check if email is empty
        if (email.isEmpty()) {
            emailET.setError("Email is required.");
            emailET.requestFocus();
            return;
        }

        //check if email is of correct format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("Please enter a valid mail.");
            emailET.requestFocus();
            return;
        }

        //check if password is empty
        if (password.isEmpty()) {
            passwordET.setError("Password is required");
            passwordET.requestFocus();
            return;
        }

        //check password length smaller than 6
        if (password.length() < 6) {
            passwordET.setError("Password is at least 6 characters");
            passwordET.requestFocus();
            return;
        }
    }

    private void login(String email, String password) {

        //sign in with Firebase
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //if user has successfully signed in
                if (task.isSuccessful()) {
                    //TODO: redirect to home activity
                    //TODO: remove temporary Toast
                    Toast.makeText(LoginActivity.this, "Successfully signed up!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to login. Please check credentials", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}