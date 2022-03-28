package com.infinyquiz.auth.userquestions;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.support.test.rule.ActivityTestRule;

import com.infinyquiz.R;
import com.infinyquiz.auth.LoginActivity;
import com.infinyquiz.datarepresentation.Question;
import com.infinyquiz.userquestions.CreateQuestionActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

public class CreateQuestionActivityTest {

    //Variables to hold strings:
    final String empty = "";
    final String question = "Is this a question?";
    final String optionA = "Yes!";
    final String optionB = "Not sure";
    final String optionC = "Maybe";
    final String optionD = "No way!";

    //Code to run activity during test:
    @Rule
    public ActivityTestRule<CreateQuestionActivity> createQuestionActivityRule = new ActivityTestRule<>(CreateQuestionActivity.class);

    //The activity itself
    private CreateQuestionActivity activity;

    @Before
    public void initTestEnvironement() {
        activity = createQuestionActivityRule.getActivity();
    }

    //A function to simulate the user setting the values in out activity.
    private void setInput(String question, String optionA, String optionB, String optionC, String optionD) {
        //Set input of text views
        onView(withId(R.id.questionTextInput))
                .perform(typeText(question), closeSoftKeyboard());
        onView(withId(R.id.questionAnswerAInput))
                .perform(typeText(optionA), closeSoftKeyboard());
        onView(withId(R.id.questionAnswerBInput))
                .perform(typeText(optionB), closeSoftKeyboard());
        onView(withId(R.id.questionAnswerCInput))
                .perform(typeText(optionC), closeSoftKeyboard());
        onView(withId(R.id.questionAnswerDInput))
                .perform(typeText(optionD), closeSoftKeyboard());

    }

    //Test if correct input will be accepted
    @Test
    public void testIsvalidInput1() {
        setInput(question, optionA, optionB, optionC, optionD);
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("Correct input is being accepted", activity.isValidInput());
            }
        });
    }

    //Tests for empty input being refused.
    @Test
    public void testIsvalidInput2() {
        setInput(empty, optionA, optionB, optionC, optionD);
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("Correct input is being accepted", !activity.isValidInput());
            }
        });
    }

    //Tests for empty input being refused.
    @Test
    public void testIsvalidInput3() {
        setInput(question, empty, optionB, optionC, optionD);
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("Correct input is being accepted", !activity.isValidInput());
            }
        });
    }

    //Tests for empty input being refused.
    @Test
    public void testIsvalidInput4() {
        setInput(question, optionA, empty, optionC, optionD);
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("Correct input is being accepted", !activity.isValidInput());
            }
        });
    }

    //Tests for empty input being refused.
    @Test
    public void testIsvalidInput5() {
        setInput(question, optionA, optionB, empty, optionD);
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("Correct input is being accepted", !activity.isValidInput());
            }
        });
    }

    //Tests for empty input being refused.
    @Test
    public void testIsvalidInput6() {
        setInput(question, optionA, optionB, optionC, empty);
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("Correct input is being accepted", !activity.isValidInput());
            }
        });
    }

    //Test for retrieving question data
    @Test
    public void testGetQuestionFromInput() {
        setInput(question, optionA, optionB, optionC, optionD);
        Question retrievedQuestion = activity.getQuestionFromInput();
        String[] arr = activity.getResources().getStringArray(R.array.categories);
        ArrayList<String> list = new ArrayList<>();
        list.add(optionA.trim());
        list.add(optionB.trim());
        list.add(optionC.trim());
        list.add(optionD.trim());
        Question realQuestion = new Question((String) question.trim(), arr[0].trim(), (int) 3, list, optionA.trim(), null);
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("input correctly retrieved", realQuestion.equals(retrievedQuestion));
                assertTrue("Question is equal" , realQuestion.getQuestion().equals(retrievedQuestion.getQuestion()));
                assertTrue("Category is equal" ,realQuestion.getCategory().equals(retrievedQuestion.getCategory()));
                assertTrue("Difficulity is equal", realQuestion.getDifficulty() == retrievedQuestion.getDifficulty());
                assertTrue("options are the same", realQuestion.getOptions().equals(retrievedQuestion.getOptions()));
                assertTrue("Correct option is the same", realQuestion.getCorrectOption().equals(retrievedQuestion.getCorrectOption()));
            }
        });

    }


}
