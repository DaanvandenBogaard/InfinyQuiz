package com.infinyquiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
public class MatchMakingActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    //Constant: amount of seconds after which a lobby which has the minimal number of people
    //will start
    private final int WAITING_TIME = 10;
    private final MatchMaker matchMaker = new MatchMaker();
    private final int DELAY = 1000; //1 second
    private String selectedCategory;

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

        //Set UI for spinner
        setSpinner();

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
        intent.putExtra("category", selectedCategory);
        matchMaker.closeLobby();
        matchMaker.updateFirebaseLobby(matchMaker.getLobby());
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //Stop the handler when the activity is not visible.
        super.onPause();
    }

    //A method to set the values of our spinner (drop down menu for categories)
    //Got this method from API https://developer.android.com/guide/topics/ui/controls/spinner#java
    private void setSpinner() {
        //Start by finding spinner
        Spinner spinner = (Spinner) findViewById(R.id.selectedCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    //Overiding methods for spinner:
    @Override //Method for OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedCategory = adapterView.getItemAtPosition(i).toString(); //update category
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        String[] arr = getResources().getStringArray(R.array.categories);
        selectedCategory = arr[0];
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backToHomeBtn) {
            matchMaker.leaveLobby();
            startActivity(new Intent(this, HomeActivity.class));
        }
    }
}
