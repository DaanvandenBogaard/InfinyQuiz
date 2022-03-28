package com.infinyquiz.userquestions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infinyquiz.HomeActivity;
import com.infinyquiz.R;
import com.infinyquiz.datarepresentation.Question;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CreateQuestionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    //The firebase database
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/");

    //The string of the category will be stored here.
    private String category;

    //Views where the user will be able to set values:
    private TextView questionTV, answerATV, answerBTV, answerCTV, answerDTV;
    private SeekBar difficultySlider;

    private Button cameraBtn, galleryBtn;

    //int for requesting image capture and selectior
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private static final int SELECT_IMAGE = 222;

    //Encoded bitmap for the question
    private String image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createquestion);

        //Set button to go back to home
        Button backToHomeBtn = (Button) findViewById(R.id.quitToHomeBtn);
        backToHomeBtn.setOnClickListener(new MoveToActivityOnClickListener(new HomeActivity(), this));

        setSpinner();

        //We initialise our views:
        questionTV = (TextView) findViewById(R.id.questionTextInput);
        answerATV = (TextView) findViewById(R.id.questionAnswerAInput);
        answerBTV = (TextView) findViewById(R.id.questionAnswerBInput);
        answerCTV = (TextView) findViewById(R.id.questionAnswerCInput);
        answerDTV = (TextView) findViewById(R.id.questionAnswerDInput);
        difficultySlider = (SeekBar) findViewById(R.id.difficultySlider);

        //Set buttons:
        Button cameraBtn = (Button) findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera(); //implemented from Mike
            }
        });

        Button galleryBtn = (Button) findViewById(R.id.galleryBtn);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGallery(); //Implemented from Mike
            }
        });

        //Finally, we set the submit button
        Button submitButton = (Button) findViewById(R.id.saveQuestionBtn);
        submitButton.setOnClickListener(this);
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

    @Override //Method for OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        category = adapterView.getItemAtPosition(i).toString(); //update category
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        String[] arr = getResources().getStringArray(R.array.categories);
        category = arr[0];
    }

    /* A function to retrieve the input
     * It returns a Question object
     *
     * @pre none
     * @returns a Question object as specified above
     * @modifies none
     */
    public Question getQuestionFromInput() {
        ArrayList<String> options = new ArrayList<String>();
        options.add(answerATV.getText().toString().trim());
        options.add(answerBTV.getText().toString().trim());
        options.add(answerCTV.getText().toString().trim());
        options.add(answerDTV.getText().toString().trim());
        return new Question((String) questionTV.getText().toString().trim(), category,
                (int) difficultySlider.getProgress(), options, answerATV.getText().toString().trim(),
                image);
    }

    /* A function that will test whether or not all fields are nonempty and if all our requirements hold.
     *
     * @pre none
     * @modifies none
     * @returns {@result == (the input satisfies our conditions)}
     */
    public boolean isValidInput(){
        //Test for being non empty
        if(questionTV.getText().toString().trim().isEmpty()){
            questionTV.setError("Question must be entered.");
            questionTV.requestFocus();
            return false;
        }
        if(answerATV.getText().toString().trim().isEmpty()){
            answerATV.setError("Correct answer must be entered.");
            answerATV.requestFocus();
            return false;
        }
        if(answerBTV.getText().toString().trim().isEmpty()){
            answerBTV.setError("option must be entered.");
            answerBTV.requestFocus();
            return false;
        }
        if(answerCTV.getText().toString().trim().isEmpty()){
            answerCTV.setError("option must be entered.");
            answerCTV.requestFocus();
            return false;
        }
        if(answerDTV.getText().toString().trim().isEmpty()){
            answerDTV.setError("option must be entered.");
            answerDTV.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if(isValidInput()){
            Question question = getQuestionFromInput();
            //Submit question to database
            database.getReference().child("NotValidatedQuestions").push().setValue(question).addOnCompleteListener(new OnCompleteListener<Void>() { //Test whether this has gone succesfully or not.
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){ //If user has been added to realtime database
                        Toast.makeText(CreateQuestionActivity.this, "Question succesfully submitted!!", Toast.LENGTH_LONG).show();
                        //Reload page
                        startActivity(new Intent(CreateQuestionActivity.this, CreateQuestionActivity.class));
                    }
                    else{
                        Toast.makeText(CreateQuestionActivity.this, "Something went wrong with your submission, please try again!", Toast.LENGTH_LONG).show();
                    }
                }
            });
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

    //Reacts to the 2 methods above:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            encodeBitmap(imageBitmap);
        }
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                encodeBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
        }
    }

    /* A function that uploads the encoded image to firebase
     *
     * @pre {@code bitmap != null}
     * @modifies none
     * @post encoded image is inside firebase realtime database
     */
    public void encodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

}
