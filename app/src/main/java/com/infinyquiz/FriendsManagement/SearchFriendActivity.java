package com.infinyquiz.FriendsManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.infinyquiz.R;
import com.infinyquiz.auth.RegisterActivity;
import com.infinyquiz.datarepresentation.User;

import java.util.ArrayList;

public class SearchFriendActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String selectedUserEmail;
    User selectedUser;
    Button sendRequestBtn;
    DatabaseReference friendRequestRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        ListView usersFound = (ListView) findViewById(R.id.friendSearchListView);
        SearchView friendSearch = (SearchView) findViewById(R.id.searchFriendsView);
        sendRequestBtn = (Button) findViewById(R.id.sendRequestBtn);
        friendRequestRef =  firebaseDatabase.getReference().child("FriendRequest");

        friendSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                updateQuery(s, usersFound);

                return false;
            }
        });

        usersFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                usersFound.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                usersFound.setSelector(android.R.color.darker_gray);
                selectedUserEmail = usersFound.getAdapter().getItem(i).toString();
            }
        });

        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedUserEmail != null) {
                    User reciever = getUserByMail(selectedUserEmail);

                    if (sendRequestBtn.getText().equals("Send request")) {
                        sendRequest(reciever);
                    }
                    else if (sendRequestBtn.getText().equals("Cancel request")) {
                        cancelRequest(reciever);
                    }
                }
            }
        });
    }

    private void updateQuery(String s, ListView usersFound) {
        DatabaseReference mRef = firebaseDatabase.getReference();
        ArrayList<User> orderList = new ArrayList<>();
        mRef.child("Users").orderByChild("mail").startAt(s).endAt(s+"\uf8ff").limitToFirst(20).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> updatedQuery = new ArrayList<String>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    orderList.add(user);
                }

                for (User user : orderList) {
                    updatedQuery.add(user.getMail());
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter<>(SearchFriendActivity.this, android.R.layout.simple_list_item_1, updatedQuery);
                usersFound.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("could not read data", "The read failed: " + databaseError.getCode());
            }
        });
    }
    
    private void sendRequest(User reciever) {

        if(!requestIsValid(reciever)) {
            DatabaseReference friendRequestRef =  firebaseDatabase.getReference().child("FriendRequest");
            String id_sender = mAuth.getUid();
            friendRequestRef.child(id_sender).child(reciever.getId()).child("RequestType").setValue("Sent")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                friendRequestRef.child(reciever.getId()).child(id_sender).child("RequestType").setValue("received")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    sendRequestBtn.setText("Cancel request");
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    private void cancelRequest(User reciever) {
        if(!requestIsValid(reciever)) {
            String id_sender = mAuth.getUid();

            friendRequestRef.child(id_sender).child(reciever.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot requestSnapshot: snapshot.getChildren()) {
                        requestSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            friendRequestRef.child(reciever.getId()).child(id_sender).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot requestSnapshot: snapshot.getChildren()) {
                        requestSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private boolean requestIsValid(User reciever) {
        if (getUserByMail(mAuth.getCurrentUser().getEmail()).friendList.contains(reciever.getId())) {
            Toast.makeText(SearchFriendActivity.this, "Already friends with user!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private User getUserByMail(String selectedUserEmail) {
        DatabaseReference mRef =  firebaseDatabase.getReference();
        mRef.child("Users").orderByChild("mail").equalTo(selectedUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> updatedQuery = new ArrayList<String>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    selectedUser = userSnapshot.getValue(User.class);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("could not read data", "The read failed: " + databaseError.getCode());
            }
        });

        return  selectedUser;
    }
}