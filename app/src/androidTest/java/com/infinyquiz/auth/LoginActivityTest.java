package com.infinyquiz.auth;


import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;

public class LoginActivityTest {
    //Code to run activity during test:
    @Rule
    public ActivityTestRule<LoginActivity> registerActivtyRule = new ActivityTestRule<>(LoginActivity.class);

    //The activity itself
    private LoginActivity activity;

    @Before
    public void initTestEnvironement() {
        activity = registerActivtyRule.getActivity();
    }
}

