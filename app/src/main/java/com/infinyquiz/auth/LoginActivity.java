package com.infinyquiz.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.infinyquiz.HomeActivity;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;
import com.infinyquiz.R;

public class LoginActivity extends AppCompatActivity {

    private EditText emailET, passwordET;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Set the button to move to the RegisterActivity
        Button registerBtn = (Button) findViewById(R.id.toRegister);
        registerBtn.setOnClickListener(new MoveToActivityOnClickListener(new RegisterActivity(), this));

        //Set button to move to the ResetPasswordActivity
        Button resetPasswordBtn = (Button) findViewById(R.id.forgotPasswordBtn);
        resetPasswordBtn.setOnClickListener(new MoveToActivityOnClickListener(new ResetPasswordActivity(), this));

        //Set the edit text elements of the UI
        emailET = (EditText) findViewById(R.id.loginEmailInput);
        passwordET = (EditText) findViewById(R.id.loginPasswordInput);

        //Set the OnClickListener that is responsible for our submit button.
        Button loginBtn = (Button) findViewById(R.id.LoginInputBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] input = retrieveInput();
                String email = input[0];
                String password = input[1];
                if (isValidLoginInput(email, password)) {
                    login(email, password);
                }

            }
        });
        //See if we have some data to automatically fill in.
        setOldValues();
    }


    /* Returns an array of length 2 with the (by the user entered) information.
     * The array is of the following form:
     * [email, password]
     *
     * @pre {@code emailET != null &  password != null}
     * @modifies none
     * @Throws IllegalArgumentException if pre condition violated.
     * @Return Array with 4 Strings. Where:
     * {@code @result[0] == emailET.getText().toString().trim() &
     *        @result[1] == passwordET.getText().toString().trim() }
     */
    public String[] retrieveInput() throws IllegalArgumentException {
        if (emailET == null || passwordET == null) {
            throw new IllegalArgumentException("One of the views is not instantiated correctly.");
        }
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        return new String[]{email, password};
    }

    /* Checks whether or not the input is valid (fullfills both firebase's requirements and our own).
     * @pre {@code input.length() == 2 &&
     *      (\forall i; input.has(i); input[i].getClass() == String.getClass())}
     *
     * @modifies none
     *
     * @returns {@result == (the input satisfies our conditions)}
     */
    public boolean isValidLoginInput(String email, String password) {

        //check if email is empty
        if (email.isEmpty()) {
            emailET.setError("Email is required.");
            emailET.requestFocus();
            return false;
        }

        //check if email is of correct format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("Please enter a valid mail.");
            emailET.requestFocus();
            return false;
        }

        //check if password is empty
        if (password.isEmpty()) {
            passwordET.setError("Password is required");
            passwordET.requestFocus();
            return false;
        }

        //check password length smaller than 6
        if (password.length() < 6) {
            passwordET.setError("Password is at least 6 characters");
            passwordET.requestFocus();
            return false;
        }
        return true;
    }

    //Login code for firebase
    private void login(String email, String password) {

        //sign in with Firebase
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //if user has successfully signed in
                if (task.isSuccessful()) {

                    FirebaseUser fireBaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (fireBaseUser.isEmailVerified()) {
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    } else {
                        fireBaseUser.sendEmailVerification();
                        Toast.makeText(LoginActivity.this, "Check your email to verify your account", Toast.LENGTH_LONG).show();
                    }

                    Toast.makeText(LoginActivity.this, "Successfully signed up!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to login. Please check credentials", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /* A function to set the values according to the intent.
     *
     * @pre none
     * @post if {@code getIntent.getExtras() != null} then:
     * ({@code Bundle extras = getIntent().getExtras()}) &
     * (if {@code extras.getString("email") != null} then the value in the UI field is set accordingly) &
     * (if {@code extras.getString("password") != null} then the value in the UI field is set accordingly)
     */
    private void setOldValues(){
        //Retrieve (possible) info from register activity
        Bundle info = getIntent().getExtras();
        if (info != null) {
            String email = info.getString("email");
            String password = info.getString("password");
            if(email != null){
                emailET.setText(email);
            }
            if(password != null){
                passwordET.setText(password);
            }
        }
    }

}