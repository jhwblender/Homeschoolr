package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AddSubject extends AppCompatActivity {

    Spinner childrenSpinner, hrSpinner, minSpinner;

    Auth auth;
    Family family;
    Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        childrenSpinner = findViewById(R.id.childrenSpinner);
        hrSpinner = findViewById(R.id.hrSpinner);
        minSpinner = findViewById(R.id.minSpinner);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = new Auth(this);
        family = Family.load(this);
        user = Person.load(this, auth.getEmail());

        populateChildList();
        populateHrSpinner();
        populateMinSpinner();
    }

    private void populateChildList(){
        ArrayList<String> childrenNames = new ArrayList<>();
        ArrayList<Person> members = family.getMembers(this);
        for(int i = 0; i < members.size(); i++)
            if(!members.get(i).getIsParent())
                childrenNames.add(members.get(i).getName());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, childrenNames);
        childrenSpinner.setAdapter(spinnerArrayAdapter);
    }

    private void populateHrSpinner(){
        ArrayList<String> hours = new ArrayList<>();
        for(int i = 0; i <= 8; i+=1)
            hours.add(String.valueOf(i));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, hours);
        hrSpinner.setAdapter(spinnerArrayAdapter);
    }
    private void populateMinSpinner(){
        ArrayList<String> minutes = new ArrayList<>();
        for(int i = 0; i < 60; i+=5)
            minutes.add(String.valueOf(i));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, minutes);
        minSpinner.setAdapter(spinnerArrayAdapter);
    }
}