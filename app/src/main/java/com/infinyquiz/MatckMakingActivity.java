package com.infinyquiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.infinyquiz.auth.LoginActivity;
import com.infinyquiz.auth.RegisterActivity;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

/* In the matchmaker, we will find a lobby to join, either by joining an existing lobby or by
 * creating one.
 * Furthermore, once a lobby has enough players to start ({@code MIN_PEOPLE}) we start a counter of
 * {@code WAITING_TIME} seconds. If this counter ends or if we reach {@code MAX_PEOPLE} players,
 * we will move the game to a new place in the database, and start the match.
 *
 * This matchmaker activity is only for global matchmaking games. Custom matches (i.e. matches with
 * groups of friends) will be added via a separate mechanic.
 */
public class MatckMakingActivity extends AppCompatActivity {

    //Constant: maximal number of people in lobby
    private final int MAX_PEOPLE = 6;

    //Constant: minimal number of people to start game
    private final int MIN_PEOPLE = 2;

    //Constant: amount of seconds after which a lobby which has the minimal number of people
    //will start
    private final int WAITING_TIME = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaker);

        Button backToHomeBtn = (Button) findViewById(R.id.backToHomeBtn);
        backToHomeBtn.setOnClickListener(new MoveToActivityOnClickListener(this, new HomeActivity()));

        //Make matchmaker
        MatchMaker matchMaker = new MatchMaker();
    }


}
