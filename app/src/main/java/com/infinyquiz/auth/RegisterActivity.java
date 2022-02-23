package com.infinyquiz.auth;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.FirebaseDatabase;
import com.infinyquiz.MoveToActivityOnClickListener;
import com.infinyquiz.R;
import com.infinyquiz.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameET, mailET, passwordET, passwordRepeatET;
    private Button confirmBtn;

    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Get FireBase instance:
        mAuth = FirebaseAuth.getInstance();

        Button button = findViewById(R.id.toLogin);
        button.setOnClickListener(new MoveToActivityOnClickListener(
                new LoginActivity(), this));


        usernameET = (EditText) findViewById(R.id.registerUsernameInput);
        mailET = (EditText) findViewById(R.id.registerMailInput);
        passwordET = (EditText) findViewById(R.id.registerPasswordInput);
        passwordRepeatET = (EditText) findViewById(R.id.registerPasswordInputRepeat);

        confirmBtn = (Button) findViewById(R.id.registerConfirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v.getId() == R.id.registerConfirmBtn) {
                    String[] input = RetrieveInput();
                    if (isValidInput(input)) {
                        User thisUser = new User(input[0],input[1]);
                        createAccount(input[1], input[2], thisUser);
                    }
                }
            }
        });
    }


    /* Returns an array of lentgh 4 with the (by the user entered) information.
     * The array is of the following form:
     * [username,email,password,passwordRepeat]
     *
     * @pre {@code usernameET != null & mailET != null & password != null & passwordRepeat != null}
     * @modifies none
     * @Throws IllegalArgumentException if pre condition violated.
     * @Return Array with 4 Strings. Where:
     * {@code @result[0] == usernameET.getText().toString().trim() &
     *        @result[1] == mailET.getText().toString().trim() &
     *        @result[2] == passwordET.getText().toString().trim() &
     *        @result[3] == passwordRepeatET.getText().toString().trim()}
     */

    private String[] RetrieveInput() throws IllegalArgumentException {
        if (usernameET == null || mailET == null || passwordET == null || passwordRepeatET == null) {
            throw new IllegalArgumentException("One of the views is not instantiated correctly.");
        }
        String[] result = new String[4];
        result[0] = usernameET.getText().toString().trim();
        result[1] = mailET.getText().toString().trim();
        result[2] = passwordET.getText().toString().trim();
        result[3] = passwordRepeatET.getText().toString().trim();
        return result;
    }


    /* Checks whether or not the input is valid (fullfills both firebase's requirements and our own).
     * @pre {@code input.length == 4 &
     * (\forall i; input.has(i); input[i].getClass() == String.getClass())}
     *
     * @modifies none
     *
     * @returns {@result == (the input satisfies our conditions)}
     */
    public boolean isValidInput(String[] input) {
        //Convert string array to 4 different strings.
        String username = input[0];
        String mail = input[1];
        String password = input[2];
        String passwordRepeat = input[3];
        //checks for empty fields
        if (username.isEmpty()) {
            usernameET.setError("Username is required.");
            usernameET.requestFocus();
            return false;
        }
        if (mail.isEmpty()) {
            mailET.setError("Mail is required.");
            mailET.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            passwordET.setError("Password is required.");
            passwordET.requestFocus();
            return false;
        }
        if (passwordRepeat.isEmpty()) {
            passwordRepeatET.setError("Password is required");
            passwordET.requestFocus();
            return false;
        }

        //check for valid email
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            mailET.setError("Please submit a valid email");
            mailET.requestFocus();
            return false;
        }

        //check for minimal password length
        if (password.length() < 6) {
            passwordET.setError("A password of at least 6 characters is required.");
            passwordET.requestFocus();
            return false;
        }

        //check if repeated password is the same as the password
        if (!password.equals(passwordRepeat)) {
            passwordRepeatET.setError("Passwords are not the same.");
            passwordRepeatET.requestFocus();
            return false;
        }
        return true;
    }

    //A method used to make an account. Copied from google so no contract nor unit tests.
    //We assume google's code is correct.
    public void createAccount(String email, String password, User user) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { //if user is registered succesfully (authentication not database)
                            //Put user object into real-time database (firebase)
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()) //Get user ID to put into database
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() { //Test whether this has gone succesfully or not.
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){ //If user has been added to realtime database
                                                Toast.makeText(RegisterActivity.this, "User has been registered succesfully!", Toast.LENGTH_LONG).show();

                                                //TODO redirect to login!
                                            }
                                            else{
                                                Toast.makeText(RegisterActivity.this, "Failed to put user into real time database", Toast.LENGTH_LONG).show();
                                            }
                                        }
                            });
                        } else{ //If user could not be registered to authentication
                            Toast.makeText(RegisterActivity.this, "Failed to register user", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}