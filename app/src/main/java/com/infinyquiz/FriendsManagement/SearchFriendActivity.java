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
import com.infinyquiz.datarepresentation.UserDataConverter;

import java.util.ArrayList;

public class SearchFriendActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String selectedUserEmail;
    User selectedUser;
    Button sendRequestBtn;
    DatabaseReference databaseReference;
    UserDataConverter converter = new UserDataConverter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        ListView usersFound = (ListView) findViewById(R.id.friendSearchListView);
        SearchView friendSearch = (SearchView) findViewById(R.id.searchFriendsView);
        sendRequestBtn = (Button) findViewById(R.id.sendRequestBtn);
        databaseReference =  firebaseDatabase.getReference();

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
                    addFriend();
                }
            }
        });
    }

    private void addFriend() {
        if (converter.isReady()) {
           databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("friendList").child(converter.getID(selectedUserEmail)).setValue(selectedUserEmail);
        }
    }

    private void updateQuery(String s, ListView usersFound) {
        databaseReference.child("Users").orderByChild("mail").startAt(s).endAt(s+"\uf8ff").limitToFirst(20).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> updatedQuery = new ArrayList<String>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String mail = userSnapshot.child("email").getValue().toString();
                    updatedQuery.add(mail);
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
}