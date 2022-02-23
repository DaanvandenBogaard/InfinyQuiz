package com.infinyquiz;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MoveToActivityOnClickListener implements View.OnClickListener {

    Intent switchActivityIntent;
    Context context;

    public MoveToActivityOnClickListener(AppCompatActivity destination, Context context){
        this.switchActivityIntent = new Intent(context, destination.getClass());
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        context.startActivity(switchActivityIntent);
    }

}
