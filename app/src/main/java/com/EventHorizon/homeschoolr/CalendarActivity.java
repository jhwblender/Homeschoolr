package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.flexbox.FlexboxLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {
    Functions functions;
    Auth auth;
    Button addSubjectButton;
    ToggleButton filterToggle;
    FlexboxLayout filterView;
    CalendarDraw drawer;
    TextView startDateView;
    TextView endDateView;
    Spinner numDaysView;

    Calendar startDate;
    Calendar endDate;
    int numDays;

    Family family;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        auth = new Auth(this);
        addSubjectButton = findViewById(R.id.addSubjectButton);
        filterToggle = findViewById(R.id.filterToggle);
        filterView = findViewById(R.id.filterView);
        drawer = findViewById(R.id.drawView);
        startDateView =  findViewById(R.id.startDate);
        endDateView = findViewById(R.id.endDate);
        numDaysView = findViewById(R.id.numDays);
        functions = new Functions(this);

        numDaysView.setSelection(2); //set days to view as 3
        dayChange(numDaysView);

        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_MONTH, numDays);
        updateDates();

        numDaysView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numDays = numDaysView.getSelectedItemPosition() + 1;
                drawer.daysChange(numDays);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("CalendarActivity","Calendar Activity starting");
        family = Family.load(this);
        if(family == null)
            Log.e("CalendarActivity","Family loaded as null");
        //hide add-subject if they are a child
        if(!family.getMember(this, auth.getEmail()).getIsParent())
            addSubjectButton.setVisibility(View.GONE);
    }

    //toggles filter view
    public void filterToggle(View view){
//        if(filterToggle.isChecked())
//            filterView.setVisibility(View.VISIBLE);
//        else
//            filterView.setVisibility(View.GONE);
//        //redraw canvas after change
//        filterView.post(new Runnable(){@Override public void run(){drawer.viewChange(drawer.getWidth(),drawer.getHeight());}});
    }

    //sets the start date to today
    public void resetDates(View view){
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_MONTH,numDaysView.getSelectedItemPosition());
        updateDates();
    }

    /* forward date by 1 block */
    public void dayForward(View view){
        startDate.add(Calendar.DAY_OF_MONTH,numDays);
        endDate = (Calendar)startDate.clone();
        endDate.add(Calendar.DAY_OF_MONTH, numDays-1);
        updateDates();
    }
    //back 1 block
    public void dayBack(View view){
        startDate.add(Calendar.DAY_OF_MONTH,-(numDays));
        endDate = (Calendar)startDate.clone();
        endDate.add(Calendar.DAY_OF_MONTH, numDays-1);
        updateDates();
    }

    public void dayChange(View view){
        numDays = numDaysView.getSelectedItemPosition();
        Log.d("CalendarActivity","Number of days changed to "+numDays);
        drawer.daysChange(numDays);
    }

    public void updateDates(){
        drawer.startDateChange(startDate);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        startDateView.setText(sdf.format(startDate.getTime()));
        endDateView.setText(sdf.format(endDate.getTime()));
    }

    public void goToAddSubjectActivity(View view){
        functions.goToActivity(AddSubject.class);
    }
    public void goToSettingsActivity(View view){
        functions.goToActivity(SettingsActivity.class);
    }
}