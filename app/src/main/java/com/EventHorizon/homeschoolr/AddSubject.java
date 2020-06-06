package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AddSubject extends AppCompatActivity {

    Auth auth;
    Family family;
    Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = new Auth(this);
        family = Family.load(this);
        user = Person.load(this, auth.getEmail());
    }
}