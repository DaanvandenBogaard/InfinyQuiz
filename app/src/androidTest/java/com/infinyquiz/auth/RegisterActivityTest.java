package com.infinyquiz.auth;

import android.support.test.rule.ActivityTestRule;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.infinyquiz.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.*;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class RegisterActivityTest {
    //Code to run activity during test:
    @Rule
    public ActivityTestRule<RegisterActivity> registerActivtyRule = new ActivityTestRule<>(RegisterActivity.class);
    ;
    //The activity itself
    private RegisterActivity activity;

    @Before
    public void initTestEnvironement() {
        activity = registerActivtyRule.getActivity();
    }


    //This test will set the values of the input fields, and see if the correct input is being returned.
    @Test
    public void testRetrieveInput() {
        //Write user name
        onView(withId(R.id.registerUsernameInput)).perform(typeText("Miguel"), closeSoftKeyboard());
        //Write email
        onView(withId(R.id.registerMailInput)).perform(typeText("Miguel.vd.Anker@gmail.com"), closeSoftKeyboard());
        //Write password
        onView(withId(R.id.registerPasswordInput)).perform(typeText("Miguellito123"), closeSoftKeyboard());
        //Write repeatpassword
        onView(withId(R.id.registerPasswordInputRepeat)).perform(typeText("Miguellito123"), closeSoftKeyboard());

        //RetrieveInput:
        String[] retrievedInput = activity.RetrieveInput();
        //Now we test to see if these are all the same:
        assertEquals("Username correctly retrieved", "Miguel", retrievedInput[0]);
        assertEquals("Email correctly retrieved", "Miguel.vd.Anker@gmail.com", retrievedInput[1]);
        assertEquals("Password correctly retrieved", "Miguellito123", retrievedInput[2]);
        assertEquals("RepeatPassword correctly retrieved", "Miguellito123", retrievedInput[3]);
    }

    //First do a check to see if a correct input is seen as correct.
    @Test
    public void testIsValidInput1() {
        String[] input = new String[4];
        input[0] = "Mike"; //userName
        input[1] = "Miguellito.Anker@gmail.com"; //Email
        input[2] = "Password"; //Password
        input[3] = "Password"; //RepeatPassword
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("Correct input accepted", activity.isValidInput(input));
            }
        });
    }

    //Check empty username:
    @Test
    public void testIsValidInput2() {
        String[] input = new String[4];
        input[0] = ""; //userName
        input[1] = "Miguellito.Anker@gmail.com"; //Email
        input[2] = "Password"; //Password
        input[3] = "Password"; //RepeatPassword
        activity.runOnUiThread(new Runnable() {
            public void run() {
                assertTrue("Empty field rejected", !activity.isValidInput(input));
            }
        });
    }

    //Check empty email:
    @Test
    public void testIsValidInput3() {
        String[] input = new String[4];
        input[0] = ""; //userName
        input[1] = "Miguellito.Anker@gmail.com"; //Email
        input[2] = "Password"; //Password
        input[3] = "Password"; //RepeatPassword
        activity.runOnUiThread(new Runnable() {
            public void run() {
                assertTrue("Empty field rejected", !activity.isValidInput(input));
            }
        });
    }

    //Check empty password:
    @Test
    public void testIsValidInput4() {
        String[] input = new String[4];
        input[0] = "Miguel"; //userName
        input[1] = "Miguellito.Anker@gmail.com"; //Email
        input[2] = ""; //Password
        input[3] = "Password"; //RepeatPassword
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("Empty field rejected", !activity.isValidInput(input));
            }
        });
    }

    //Check empty RepeatPassword:
    @Test
    public void testIsValidInput5() {
        String[] input = new String[4];
        input[0] = "Miguel"; //userName
        input[1] = "Miguellito.Anker@gmail.com"; //Email
        input[2] = "Password"; //Password
        input[3] = ""; //RepeatPassword
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("Empty field rejected", !activity.isValidInput(input));
            }
        });
    }

    //Check correct formatting email:
    @Test
    public void testIsValidInput6() {
        String[] input = new String[4];
        input[0] = "Miguel"; //userName
        input[1] = "Miguellito.Ankergmail.com"; //Email
        input[2] = "Password"; //Password
        input[3] = "Password"; //RepeatPassword
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("Not correctly formatted email rejected", !activity.isValidInput(input));
            }
        });
    }

    //Check password length:
    @Test
    public void testIsValidInput7() {
        String[] input = new String[4];
        input[0] = "Miguel"; //userName
        input[1] = "Miguellito.Ankergmail.com"; //Email
        input[2] = "Passw"; //Password
        input[3] = "Passw"; //RepeatPassword
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("Too short password rejected", !activity.isValidInput(input));
            }
        });
    }

    //Check repeatPassword is the same as password :
    @Test
    public void testIsValidInput8() {
        String[] input = new String[4];
        input[0] = "Miguel"; //userName
        input[1] = "Miguellito.Ankergmail.com"; //Email
        input[2] = "Password"; //Password
        input[3] = "PasswordRepeat"; //RepeatPassword
        activity.runOnUiThread(new Runnable() { //isValidInput makes a call to UI, cannot be on the same thread.
            public void run() {
                assertTrue("Password not repeated correctly rejected", !activity.isValidInput(input));
            }
        });
    }
}
