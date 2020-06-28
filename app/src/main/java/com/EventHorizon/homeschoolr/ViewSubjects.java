package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.firestore.auth.User;

public class ViewSubjects extends AppCompatActivity {

    Auth auth;
    Family family;
    Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_subjects);

        auth = new Auth(this);
        family = Family.load(this);
        user = family.getMember(this, auth.getEmail());


    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}