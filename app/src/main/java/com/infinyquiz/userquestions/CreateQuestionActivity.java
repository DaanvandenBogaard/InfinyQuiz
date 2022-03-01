package com.infinyquiz.userquestions;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import com.infinyquiz.R;
import com.infinyquiz.datarepresentation.Question;

public class CreateQuestionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //TODO: Implement the adding of pictures

    //The string of the category will be stored here.
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createquestion);

        setSpinner();
    }

    //A method to set the values of our spinner (drop down menu for categories)
    //Got this method from API https://developer.android.com/guide/topics/ui/controls/spinner#java
    private void setSpinner(){
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
    public Question retrieveInput(){
        return null;
    }
}
