package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class EditSubject extends AppCompatActivity implements TaskListener{

    Spinner childrenSpinner, hrSpinner, minSpinner, startHrSpinner, startMinSpinner;
    ArrayList<String> childrenNames;
    ArrayList<CheckBox> weekDays = new ArrayList<>();

    Auth auth;
    Functions functions;
    Family family;
    Person user;

    String childEmail = null;
    Person child;
    int subjectIndex = -1;
    Subject subject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_subject);

        functions = new Functions(this);
        auth = new Auth(this);
        family = Family.load(this);
        user = Person.load(this, auth.getEmail());
        functions.adInit(user);

        childrenSpinner = findViewById(R.id.childrenSpinner);
        hrSpinner = findViewById(R.id.hrSpinner);
        minSpinner = findViewById(R.id.minSpinner);
        startHrSpinner = findViewById(R.id.startHrSpinner);
        startMinSpinner = findViewById(R.id.startMinSpinner);


        childEmail = getIntent().getStringExtra("child");
        child = family.getMember(this, childEmail);
        subjectIndex = getIntent().getIntExtra("subjectIndex",-1);
        subject = child.getSubjects().get(subjectIndex);
        Log.d("EditSubject","Child email: "+childEmail+", subject index: "+String.valueOf(subjectIndex));
    }

    @Override
    protected void onStart() {
        super.onStart();
        populateChildList();
        setChild();
        setSubject();
        setNumOfLessons();
        setLessonsCompleted();
        setTimeClock();
        setWeekDays();
        populateHrSpinner();
        setHr();
        populateMinSpinners();
        setMin();
        populateStartHrSpinner();
        setStartHr();
        setStartMin();
    }

    private void populateChildList(){
        childrenNames = new ArrayList<>();
        ArrayList<Person> members = family.getMembers(this);
        for(int i = 0; i < members.size(); i++)
            if(!members.get(i).getIsParent())
                childrenNames.add(members.get(i).getName());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, childrenNames);
        childrenSpinner.setAdapter(spinnerArrayAdapter);
    }
    private void setChild(){
        for(int i = 0; i < childrenNames.size(); i++){
            if(childrenNames.get(i).equals(child.getName()))
                childrenSpinner.setSelection(i);
        }
    }

    private void setSubject(){
        ((EditText)findViewById(R.id.subject)).setText(subject.subjectName);
    }
    private void setNumOfLessons(){
        ((EditText)findViewById(R.id.numLessons)).setText(String.valueOf(subject.numLessons));
    }
    private void setLessonsCompleted(){
        ((EditText)findViewById(R.id.lessonsCompleted)).setText(String.valueOf(subject.lessonsCompleted));
    }
    private void setTimeClock(){
        int hr = Scheduler.floatToHr(subject.timeWorked);
        int min = Scheduler.floatToMin(subject.timeWorked);
        int sec = Scheduler.floatToSec(subject.timeWorked);
        ((EditText)findViewById(R.id.clockedHr)).setText(String.valueOf(hr));
        ((EditText)findViewById(R.id.clockedMin)).setText(String.valueOf(min));
        ((EditText)findViewById(R.id.clockedSec)).setText(String.valueOf(sec));
    }
    private void setWeekDays(){
        weekDays.add((CheckBox)findViewById(R.id.sundayCheckBox));
        weekDays.add((CheckBox)findViewById(R.id.mondayCheckBox));
        weekDays.add((CheckBox)findViewById(R.id.tuesdayCheckBox));
        weekDays.add((CheckBox)findViewById(R.id.wednesdayCheckBox));
        weekDays.add((CheckBox)findViewById(R.id.thursdayCheckBox));
        weekDays.add((CheckBox)findViewById(R.id.fridayCheckBox));
        weekDays.add((CheckBox)findViewById(R.id.saturdayCheckBox));
        for(int i = 0; i < weekDays.size(); i++){
            weekDays.get(i).setChecked(subject.weekdays[i]);
        }
    }

    private void populateHrSpinner(){
        ArrayList<String> hours = new ArrayList<>();
        for(int i = 0; i <= 8; i+=1)
            hours.add(String.valueOf(i));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, hours);
        hrSpinner.setAdapter(spinnerArrayAdapter);
    }
    private void populateMinSpinners(){
        ArrayList<String> minutes = new ArrayList<>();
        for(int i = 0; i < 60; i+=5)
            minutes.add(String.valueOf(i));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, minutes);
        minSpinner.setAdapter(spinnerArrayAdapter);
        startMinSpinner.setAdapter(spinnerArrayAdapter);
    }
    private void setHr(){
        ((Spinner)findViewById(R.id.hrSpinner)).setSelection(Scheduler.floatToHr(subject.length));
    }
    private void setMin(){
        ((Spinner)findViewById(R.id.minSpinner)).setSelection(Scheduler
                .floatToMin(subject.length)/5);
    }

    private void populateStartHrSpinner(){
        ArrayList<String> hours = new ArrayList<>();
        for(int i = 0; i <= 23; i+=1)
            hours.add(String.valueOf(i));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, hours);
        startHrSpinner.setAdapter(spinnerArrayAdapter);
    }
    private void setStartHr(){
        ((Spinner)findViewById(R.id.startHrSpinner)).setSelection(Scheduler
                .floatToHr(subject.start));
    }
    private void setStartMin(){
        ((Spinner)findViewById(R.id.startMinSpinner)).setSelection(Scheduler
                .floatToMin(subject.start)/5);
    }

    public void doneEditing(View view){
        String newName = ((Spinner)findViewById(R.id.childrenSpinner)).getSelectedItem().toString();
        if(!child.getName().equals(newName)){
            Person oldChild = child;
            child = family.getMemberByName(this, newName);
            child.subjects.add(subject);
            oldChild.subjects.remove(subject);
            oldChild.save(this);
        }
        subject.subjectName = ((EditText)findViewById(R.id.subject)).getText().toString();
        subject.numLessons = Integer.parseInt(((EditText)findViewById(R.id.numLessons)).getText().toString());
        subject.lessonsCompleted = Integer.parseInt(((EditText)findViewById(R.id.lessonsCompleted)).getText().toString());
        subject.weekdays[0] = ((CheckBox)findViewById(R.id.sundayCheckBox)).isChecked();
        subject.weekdays[1] = ((CheckBox)findViewById(R.id.mondayCheckBox)).isChecked();
        subject.weekdays[2] = ((CheckBox)findViewById(R.id.tuesdayCheckBox)).isChecked();
        subject.weekdays[3] = ((CheckBox)findViewById(R.id.wednesdayCheckBox)).isChecked();
        subject.weekdays[4] = ((CheckBox)findViewById(R.id.thursdayCheckBox)).isChecked();
        subject.weekdays[5] = ((CheckBox)findViewById(R.id.fridayCheckBox)).isChecked();
        subject.weekdays[6] = ((CheckBox)findViewById(R.id.saturdayCheckBox)).isChecked();
        subject.hrLength = Integer.parseInt(((Spinner)findViewById(R.id.hrSpinner)).getSelectedItem().toString());
        subject.minLength = Integer.parseInt(((Spinner)findViewById(R.id.minSpinner)).getSelectedItem().toString());
        int startHr = Integer.parseInt(((Spinner)findViewById(R.id.startHrSpinner)).getSelectedItem().toString());
        int startMin = Integer.parseInt(((Spinner)findViewById(R.id.startMinSpinner)).getSelectedItem().toString());
        subject.start = Scheduler.hrMinToFloat(startHr, startMin);
        int workedHr = Integer.parseInt(((EditText)findViewById(R.id.clockedHr)).getText().toString());
        int workedMin = Integer.parseInt(((EditText)findViewById(R.id.clockedMin)).getText().toString());
        int workedSec = Integer.parseInt(((EditText)findViewById(R.id.clockedSec)).getText().toString());
        subject.timeWorked = Scheduler.hrMinSecToFloat(workedHr, workedMin, workedSec);

        functions.loadingView(true);
        child.save(this);
        functions.goToActivity(CalendarActivity.class);
    }

    public void deleteSubject(View view){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.deleteSubjectConfirmation))
                .setPositiveButton(getString(R.string.yes), deleteSubjectPopup)
                .setNegativeButton(getString(R.string.no), deleteSubjectPopup).show();
    }
    //Verification for subject deletion
    Context context = this;
    DialogInterface.OnClickListener deleteSubjectPopup = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which==DialogInterface.BUTTON_POSITIVE) {
                functions.loadingView(true);
                child.subjects.remove(subject);
                child.save(context);
                functions.goToActivity(CalendarActivity.class);
            }else
                functions.showMessage(getString(R.string.deleteSubjectCancel),false);
        }
    };

    @Override
    public void authResult(TaskName result) {
        if(result == TaskName.DB_USER_SAVED_SUCCESSFULLY)
            functions.showMessage(subject.subjectName + " saved successfully");
    }
}