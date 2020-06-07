package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CalendarActivity extends AppCompatActivity {
    Functions functions;
    Auth auth;
    Button addSubjectButton;
    Family family;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        auth = new Auth(this);
        addSubjectButton = findViewById(R.id.addSubjectButton);
        functions = new Functions(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        family = Family.load(this);
        //hide add-subject if they are a child
        if(!family.getMember(this, auth.getEmail()).getIsParent())
            addSubjectButton.setVisibility(View.GONE);
    }

    public void goToAddSubjectActivity(View view){
        functions.goToActivity(AddSubject.class);
    }
    public void goToSettingsActivity(View view){
        functions.goToActivity(SettingsActivity.class);
    }
}