package com.infinyquiz.auth;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.support.test.rule.ActivityTestRule;

import com.infinyquiz.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginActivityTest {
    //Code to run activity during test:
    @Rule
    public ActivityTestRule<LoginActivity> loginActivityRule = new ActivityTestRule<>(LoginActivity.class);

    //The activity itself
    private LoginActivity activity;

    @Before
    public void initTestEnvironement() {
        activity = loginActivityRule.getActivity();
    }

    //Test if correct input will be accepted
    @Test
    public void testIsvalidInput1() {
        String email = "Miguel.vd.Anker@gmail.com";
        String password = "Password";
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("Correct input is being accepted", activity.isValidLoginInput(email, password));
            }
        });
    }

    //Test if empty email will be rejected
    @Test
    public void testIsvalidInput2() {
        String email = "";
        String password = "Password";
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("Empty email rejected", !activity.isValidLoginInput(email, password));
            }
        });
    }

    //Test if empty password will be rejected
    @Test
    public void testIsvalidInput3() {
        String email = "Miguel.vd.Anker@gmail.com";
        String password = "";
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("Empty password rejected", !activity.isValidLoginInput(email, password));
            }
        });
    }

    //Test if wrong email format will be rejected
    @Test
    public void testIsvalidInput4() {
        String email = "Miguel.vd.Ankergmail.com";
        String password = "";
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("Wrong email format rejected", !activity.isValidLoginInput(email, password));
            }
        });
    }

    //Test if too short password will be rejected
    @Test
    public void testIsvalidInput5() {
        String email = "Miguel.vd.Ankergmail.com";
        String password = "passw";
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("Too short password rejected", !activity.isValidLoginInput(email, password));
            }
        });
    }

    //Test by simulating user input if the correct input is being retrieved
    @Test
    public void testRetrieveInput() {
        //Write email
        onView(withId(R.id.loginEmailInput)).perform(typeText("Miguel.vd.Anker@gmail.com"), closeSoftKeyboard());
        //Write password
        onView(withId(R.id.loginPasswordInput)).perform(typeText("Miguellito123"), closeSoftKeyboard());

        //RetrieveInput:
        String[] retrievedInput = activity.retrieveInput(); //[email,password]
        assertEquals("Email correctly retrieved", "Miguel.vd.Anker@gmail.com", retrievedInput[0]);
        assertEquals("Password correctly retrieved", "Miguellito123", retrievedInput[1]);
    }
}

