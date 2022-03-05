package com.infinyquiz.onclicklistener;

import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class logOutOnClickListener extends MoveToActivityOnClickListener {

    public logOutOnClickListener(AppCompatActivity destination, Context context){
        super(destination,context);
    }

    @Override
    public void onClick(View view) {
        FirebaseAuth.getInstance().signOut();
        super.onClick(view);
    }
}
