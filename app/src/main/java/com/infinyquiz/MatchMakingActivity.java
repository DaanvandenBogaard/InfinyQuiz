package com.infinyquiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.infinyquiz.auth.LoginActivity;
import com.infinyquiz.auth.RegisterActivity;
import com.infinyquiz.datarepresentation.Lobby;
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
    private final int DELAY = 1000; //1 second

    //Handler object for using delay
    Handler handler = new Handler();
    //Runnable object for using delay
    Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaker);

        Button backToHomeBtn = (Button) findViewById(R.id.backToHomeBtn);
        backToHomeBtn.setOnClickListener(this);

        //Let matchmaker find a lobby
        matchMaker.lookForLobby();
        //update UI after every {@code DELAY} seconds, handled in "onResume" and "onPause"
    }

    @Override
    protected void onResume() {
        handler.postDelayed(runnable = new Runnable(){
            public void run(){
               handler.postDelayed(runnable,DELAY);
               if(matchMaker.getLobby() != null) {
                   System.out.println("onResume()" + matchMaker.getLobby().getUsers().toString());
               }
               Lobby lobby = matchMaker.getLobby();
               //Update UI:
                updateUI(lobby);
                //Check if game can be started
                if(lobby != null){
                    //Check if match can start.
                    if(lobby.getLobbySize() == Lobby.MAX_PEOPLE || matchMaker.gameHasStarted()){
                        startNewGameActivity();
                    }
                }
            }
        },DELAY);
        super.onResume();
    }

    //Put this in a seperate class to be able to access from onResume
    private void startNewGameActivity(){
        Intent intent = new Intent(this, GameActivity.class);
        Lobby lobby = matchMaker.getLobby();
        matchMaker.startGame();
        //Add values to intent
        intent.putExtra("lobbyID", lobby.getId());
        intent.putExtra("gameID", lobby.getGameID());
        matchMaker.closeLobby();
        matchMaker.updateFirebaseLobby(matchMaker.getLobby());
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //Stop the handler when the activity is not visible.
        super.onPause();
    }

    /* Update the UI elements according to a given Lobby object.
     *
     * @param {@code Lobby lobby}
     * @pre {@code lobby != null}
     * @post UI is set according to "current" lobby information
     * @throws none
     */
    private void updateUI(Lobby lobby){
        if(lobby == null){
            return;
        }
        TextView userListTV = (TextView) findViewById(R.id.displayUsers);

        userListTV.setText(lobby.getUsers().toString().trim());
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
        }
    }
}
