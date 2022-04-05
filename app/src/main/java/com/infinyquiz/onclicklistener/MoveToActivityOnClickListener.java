package com.infinyquiz.onclicklistener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MoveToActivityOnClickListener implements View.OnClickListener {

    Intent intent;
    Context context;

    //Constructor with destionation and context ("to" - "from") notation.
    public MoveToActivityOnClickListener(AppCompatActivity destination, Context context) {
        this.intent = new Intent(context, destination.getClass());
        this.context = context;
    }

    //Constructor with explicit content (usefull in case we want to pass date)
    public MoveToActivityOnClickListener(Intent intent) {
        this.intent = intent;
    }


    @Override
    public void onClick(View v) {
        context.startActivity(intent);
    }

}
