package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AddSubject extends AppCompatActivity implements TaskListener{

    Spinner childrenSpinner, hrSpinner, minSpinner;

    Auth auth;
    Functions functions;
    Family family;
    Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);
        functions = new Functions(this);

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

    public void addSubject(View view){
        Person child = family.getMemberByName(this,((Spinner)findViewById(R.id.childrenSpinner)).getSelectedItem().toString());
        String subjectName = ((EditText)findViewById(R.id.subject)).getText().toString();
        int numLessons = Integer.parseInt(((EditText)findViewById(R.id.numLessons)).getText().toString());
        boolean days[] = new boolean[7];
        days[0] = ((CheckBox)findViewById(R.id.sundayCheckBox)).isChecked();
        days[1] = ((CheckBox)findViewById(R.id.mondayCheckBox)).isChecked();
        days[2] = ((CheckBox)findViewById(R.id.tuesdayCheckBox)).isChecked();
        days[3] = ((CheckBox)findViewById(R.id.wednesdayCheckBox)).isChecked();
        days[4] = ((CheckBox)findViewById(R.id.thursdayCheckBox)).isChecked();
        days[5] = ((CheckBox)findViewById(R.id.fridayCheckBox)).isChecked();
        days[6] = ((CheckBox)findViewById(R.id.saturdayCheckBox)).isChecked();
        int hrLength = Integer.parseInt(((Spinner)findViewById(R.id.hrSpinner)).getSelectedItem().toString());
        int minLength = Integer.parseInt(((Spinner)findViewById(R.id.minSpinner)).getSelectedItem().toString());

        functions.loadingView(true);
        Log.d("AddSubjectAddSubject","["+days[0]+","+days[1]+","+days[2]+","+days[3]+","+days[4]+","+days[5]+","+days[6]+"]");
        child.addSubject(this, subjectName, numLessons, days, hrLength, minLength);
    }

    @Override
    public void authResult(TaskName result) {
        functions.loadingView(false);
        switch(result){
            case DB_USER_SAVED_SUCCESSFULLY:
                functions.showMessage("Subject successfully added");
                functions.goToActivity(CalendarActivity.class);
                break;
        }
    }
}