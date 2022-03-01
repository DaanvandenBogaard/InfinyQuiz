package com.infinyquiz.userquestions;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;
import com.infinyquiz.HomeActivity;
import com.infinyquiz.R;
import com.infinyquiz.auth.RegisterActivity;
import com.infinyquiz.datarepresentation.Question;
import com.infinyquiz.onclicklistener.MoveToActivityOnClickListener;

public class CreateQuestionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    //TODO: Implement the adding of pictures

    //The string of the category will be stored here.
    private String category;

    //The string that will be the pictureID
    private String pictureID = "";

    //Views where the user will be able to set values:
    private TextView questionTV, answerATV, answerBTV, answerCTV, answerDTV;
    private Slider difficultySlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createquestion);

        //Set button to go back to home
        Button backToHomeBtn = (Button) findViewById(R.id.backToHomeBtn);
        backToHomeBtn.setOnClickListener(new MoveToActivityOnClickListener(new HomeActivity(), this));

        setSpinner();

        //We initialise our views:
        questionTV = (TextView) findViewById(R.id.questionTextInput);
        answerATV = (TextView) findViewById(R.id.questionAnswerAInput);
        answerBTV = (TextView) findViewById(R.id.questionAnswerBInput);
        answerCTV = (TextView) findViewById(R.id.questionAnswerCInput);
        answerDTV = (TextView) findViewById(R.id.questionAnswerDInput);
        difficultySlider = (Slider) findViewById(R.id.difficultySlider);

        //Finally, we set the submit button
        Button submitButton = (Button) findViewById(R.id.saveQuestionBtn);
        submitButton.setOnClickListener(this);
    }

    //A method to set the values of our spinner (drop down menu for categories)
    //Got this method from API https://developer.android.com/guide/topics/ui/controls/spinner#java
    private void setSpinner() {
        //Start by finding spinner
        Spinner spinner = (Spinner) findViewById(R.id.questionCategorySpinner);
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
        String[] options = new String[4];
        options[0] = (String) answerATV.getText();
        options[1] = (String) answerBTV.getText();
        options[2] = (String) answerCTV.getText();
        options[3] = (String) answerDTV.getText();
        for (int i = 0; i < options.length; i++) {
            options[i] = options[i].trim();
        }
        return new Question((String) questionTV.getText(), category, (int) difficultySlider.getValue(), options, options[0], pictureID);
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
        }
    }
}
