package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class CalendarActivity extends AppCompatActivity {
    Functions functions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        functions = new Functions(this);
    }

    public void goToAddSubjectActivity(View view){
        functions.goToActivity(AddSubject.class);
    }
    public void goToSettingsActivity(View view){
        functions.goToActivity(SettingsActivity.class);
    }
}