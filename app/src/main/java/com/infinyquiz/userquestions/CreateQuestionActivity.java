package com.infinyquiz.userquestions;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import com.infinyquiz.R;

public class CreateQuestionActivity extends AppCompatActivity {

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
    }

}
