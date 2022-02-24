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

public class RegisterActivityTest {
    //Code to run activity during test:
    @Rule
    public ActivityTestRule<RegisterActivity> registerActivtyRule = new ActivityTestRule<>(RegisterActivity.class);


    //This test will set the values of the input fields, and see if the correct input is being returned.
    @Test
    public void testRetrieveInput(){
        //Write user name
        onView(withId(R.id.registerUsernameInput)).perform(typeText("Miguel"), closeSoftKeyboard());
        //Write email
        onView(withId(R.id.registerMailInput)).perform(typeText("Miguel.vd.Anker@gmail.com"), closeSoftKeyboard());
        //Write password
        onView(withId(R.id.registerPasswordInput)).perform(typeText("Miguellito123"), closeSoftKeyboard());
        //Write repeatpassword
        onView(withId(R.id.registerPasswordInputRepeat)).perform(typeText("Miguellito123"), closeSoftKeyboard());

        //RetrieveInput:
        String[] retrievedInput = registerActivtyRule.getActivity().RetrieveInput();
        //Now we test to see if these are all the same:
        assertEquals("Username correctly retrieved" ,"Miguel", retrievedInput[0]);
        assertEquals("Email correctly retrieved" ,"Miguel.vd.Anker@gmail.com", retrievedInput[1]);
        assertEquals("Password correctly retrieved" ,"Miguellito123", retrievedInput[2]);
        assertEquals("RepeatPassword correctly retrieved" ,"Miguellito123", retrievedInput[3]);
    }

    @Test
    public void testIsValidInput1(){

    }

}
