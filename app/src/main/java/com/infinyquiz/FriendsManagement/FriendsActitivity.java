package com.infinyquiz.FriendsManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infinyquiz.FriendsManagement.SearchFriendActivity;
import com.infinyquiz.ProfileActivity;
import com.infinyquiz.R;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

import java.util.ArrayList;

public class FriendsActitivity extends AppCompatActivity {

    //Firebase database and authentication instantiate
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //Listview of the list with friends
    ListView friendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_actitivity);

        //Set button that moves you to the SearchFriendsActivity
        Button addFriendsBtn = (Button) findViewById(R.id.addFriendsBtn);
        addFriendsBtn.setOnClickListener(new MoveToActivityOnClickListener(new SearchFriendActivity(), this));

        //Set Button that moves you back to ProfileActivity
        Button toProfileBtn = (Button) findViewById(R.id.toProfileBtn);
        toProfileBtn.setOnClickListener(new MoveToActivityOnClickListener(new ProfileActivity(), this));

        //Set friendList
        friendList = (ListView) findViewById(R.id.friendList);

        //Update friendlist with friends of current user
        showFriends();
    }

    /* A function that updates the friend list with all the friends of the current user
     *
     * @pre {@code firebaseDatabase != null && mAuth != null && friendList != null}
     * @modifies {@code friendList}
     * @post friendList displays the friends of the current user.
     */
    private void showFriends() {
        firebaseDatabase.getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("friendList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> listOfFriends = new ArrayList<String>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String mail = userSnapshot.getValue().toString();
                    listOfFriends.add(mail);
                }

                ArrayAdapter arrayAdapter = new ArrayAdapter<>(FriendsActitivity.this, android.R.layout.simple_list_item_1, listOfFriends);
                friendList.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("could not read data", "The read failed: " + databaseError.getCode());
            }
        });
    }
}