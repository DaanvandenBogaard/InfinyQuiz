package com.infinyquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.infinyquiz.auth.LoginActivity;
import com.infinyquiz.auth.RegisterActivity;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

/* In the matchmaker, we will find a lobby to join, either by joining an existing lobby or by
 * creating one.
 * Furthermore, once a lobby has enough players to start ({@code Lobby.MIN_PEOPLE}) we start a counter of
 * {@code WAITING_TIME} seconds. If this counter ends or if we reach {@code Lobby.MAX_PEOPLE} players,
 * we will move the game to a new place in the database, and start the match.
 *
 * This matchmaker activity is only for global matchmaking games. Custom matches (i.e. matches with
 * groups of friends) will be added via a separate mechanic.
 */
public class MatchMakingActivity extends AppCompatActivity implements View.OnClickListener {

    //Constant: amount of seconds after which a lobby which has the minimal number of people
    //will start
    private final int WAITING_TIME = 10;
    private final MatchMaker matchMaker = new MatchMaker();
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaker);

        Button backToHomeBtn = (Button) findViewById(R.id.backToHomeBtn);
        backToHomeBtn.setOnClickListener(this);

        Button debugBtn = (Button) findViewById(R.id.printBtn);
        debugBtn.setOnClickListener(this);
        //Find a lobby.
        matchMaker.lookForLobby();
        //Wait for start match


    }


    //DEBUG FUNCTION TO CLEAR MATCHES
    //TODO DELETE BEFORE RELEASE!
    private void Clearmatches() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/");
        database.getReference().child("Lobbies").removeValue();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backToHomeBtn) {
            matchMaker.leaveLobby();
            startActivity(new Intent(this, HomeActivity.class));
        } else if (view.getId() == R.id.printBtn) {
            System.out.println(matchMaker.getLobby());
        }

    }
}
