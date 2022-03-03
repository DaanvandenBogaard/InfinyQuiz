package com.infinyquiz;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infinyquiz.datarepresentation.Question;

import java.util.ArrayList;
import java.util.Random;

//A class to retrieve (approved) questions from the database
public class QuestionRetriever {

    //Global variables to represent the context in which a question must be called (needed for resources)
    final private Context context;
    final private Resources res;
    final private String[] categories;

    //Variables for firebase
    private FirebaseDatabase database;
    private DatabaseReference ref;

    //Constructor
    public QuestionRetriever(Context context) {
        this.context = context;
        res = context.getResources();
        categories = res.getStringArray(R.array.categories);
    }

    /*Gets a random category from categories
     *
     * @pre none
     * @post {@code categories.has(\result)}
     */
    public String getRandomCategory() {
        int length = categories.length;
        int i = new Random().nextInt(length);
        return categories[i];
    }

    //Gets a question with a random category
    public Question getQuestion() {
        //Get a random category
        String category = getRandomCategory();
        //retrieve question from database
        return getQuestion(category);
    }

    public static Question getQuestion(String category) {
        //TODO: implement this method
        return new Question();
    }

    public Question getReviewQuestion() {
        ref = FirebaseDatabase.getInstance("https://infinyquiz-a135e-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("NotValidatedQuestions");
        // Attach a listener to read the data at our posts reference
        ArrayList<Question> questions = new ArrayList<>();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Question question = snapshot.getValue(Question.class);
                    questions.add(question);
                    System.out.println("TEST");
                    System.out.println("question == null:" + question.getQuestion() == null );
                    System.out.println("question =" + question.getQuestion());
                    System.out.println("TEST");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("could not read data", "The read failed: " + databaseError.getCode());
            }
        });
        if(questions.size() == 0){
            return null;
        }
        return questions.get(0);
    }


}