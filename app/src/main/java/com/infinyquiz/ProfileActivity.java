package com.infinyquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infinyquiz.FriendsManagement.FriendsActitivity;
import com.infinyquiz.datarepresentation.Question;
import com.infinyquiz.datarepresentation.User;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    //firebase database
    private DatabaseReference databaseReference;

    //int for requesting image capture
    private static final int REQUEST_IMAGE_CAPTURE = 111;

    //Image button containing profile picture
    private ImageButton profilePicture;

    //Firebase authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //retrieves image from firebase and updates profile picture
        updateImage();

        //When clicked on managefriendsBtn we move to FriendsActivity
        Button manageFriendsBtn = (Button) findViewById(R.id.ManageFriendsBtn);
        manageFriendsBtn.setOnClickListener(new MoveToActivityOnClickListener(new FriendsActitivity(), this));

        //When clicked on profilepicture Image button the camera will be opened
        profilePicture = (ImageButton) findViewById(R.id.profilePic);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        //TODO: Change account data Activity and set button.
        //TODO: Add log out etc.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profilePicture.setImageBitmap(imageBitmap);
            uploadToFirebase(imageBitmap);
        }
    }

    /* A function that launches the camera
     *
     * @pre none
     * @modifies none
     * @post camera is launched
     */
    public void launchCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    /* A function that uploads the encoded image to firebase
     *
     * @pre {@code bitmap != null}
     * @modifies none
     * @post encoded image is inside firebase realtime database
     */
    public void uploadToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        databaseReference.child("Users")
                .child(mAuth.getCurrentUser().getUid())
                .child("imageUrl").setValue(imageEncoded);
    }

    /* A function that uploads the encoded image to firebase
     *
     * @pre none
     * @modifies none
     * @post image is decoded from base64 to bitmap
     */
    private void updateImage() {
        databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("imageUrl").getValue().toString();
                if (image != null) {
                    Bitmap imageBitmap = decodeImage(image);
                    profilePicture.setImageBitmap(imageBitmap);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("could not read data", "The read failed: " + databaseError.getCode());
            }
        });
    }

    /* A function that uploads the encoded image to firebase
     *
     * @pre {@code String != null}
     * @returns Bitmap
     * @post image is decoded from base64 to bitmap
     */
    public static Bitmap decodeImage(String image) {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }
}