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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infinyquiz.FriendsManagement.FriendsActitivity;
import com.infinyquiz.auth.ChangeUsernameActivity;
import com.infinyquiz.datarepresentation.Question;
import com.infinyquiz.datarepresentation.User;
import com.infinyquiz.datarepresentation.UserDataConverter;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    //firebase database
    private DatabaseReference databaseReference;

    //int for requesting image capture and selectior
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private static final int SELECT_IMAGE = 222;

    //Image button containing profile picture
    private ImageView profilePicture;

    //Firebase authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //instantiate the firebase database and authentication
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //set profile picture
        profilePicture = (ImageView) findViewById(R.id.profilePic);

        Button changeUsernameBtn = (Button) findViewById(R.id.changeUsernameBtn);
        changeUsernameBtn.setOnClickListener(new MoveToActivityOnClickListener(new ChangeUsernameActivity() , this));

        //retrieves image from firebase and updates profile picture
        updateImage();

        //When clicked on managefriendsBtn we move to FriendsActivity
        Button manageFriendsBtn = (Button) findViewById(R.id.ManageFriendsBtn);
        manageFriendsBtn.setOnClickListener(new MoveToActivityOnClickListener(new FriendsActitivity(), this));

        //When clicked on button move back to the home activity
        Button homeButton = (Button) findViewById(R.id.toHomeBtn);
        homeButton.setOnClickListener(new MoveToActivityOnClickListener(new HomeActivity(), this));

        //When clicked on button the gallery will be opened
        Button galleryBtn = (Button) findViewById(R.id.galleryBtn);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGallery();
            }
        });

        //When clicked on button the camera will be opened
        Button cameraBtn = (Button) findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
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
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                profilePicture.setImageBitmap(bitmap);
                uploadToFirebase(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
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

    /* A function that launches the gallery
     *
     * @pre none
     * @modifies none
     * @post gallery is launched
     */
    public void launchGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
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
                if (dataSnapshot.child("imageUrl").getValue() != null) {
                    String image = dataSnapshot.child("imageUrl").getValue().toString();
                    Bitmap imageBitmap = UserDataConverter.decodeImage(image);
                    profilePicture.setImageBitmap(imageBitmap);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("could not read data", "The read failed: " + databaseError.getCode());
            }
        });
    }
}