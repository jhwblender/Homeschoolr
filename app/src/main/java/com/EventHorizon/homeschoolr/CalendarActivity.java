package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class CalendarActivity extends AppCompatActivity implements TaskListener{
    Functions functions;
    Auth auth;
    Button addSubjectButton;
    ToggleButton filterToggle;
    FlexboxLayout filterView;
    CalendarDraw drawer;
    TextView startDateView;
    TextView endDateView;
    Spinner numDaysView;

    ArrayList<Integer> filterColors;
    ArrayList<CheckBox> filterChecks;
    ArrayList<Boolean> filterList;
    ArrayList<String> filterNames;

    Calendar startDate;
    Calendar endDate;
    int numDays;

    Family family;
    Scheduler scheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        auth = new Auth(this);
        family = Family.load(this);
        functions = new Functions(this);

        addSubjectButton = findViewById(R.id.addSubjectButton);
        filterToggle = findViewById(R.id.filterToggle);
        filterView = findViewById(R.id.filterView);
        drawer = findViewById(R.id.drawView);
        startDateView =  findViewById(R.id.startDate);
        endDateView = findViewById(R.id.endDate);
        numDaysView = findViewById(R.id.numDays);


        filterColors = new ArrayList<>();
        filterChecks = new ArrayList<>();
        filterList = new ArrayList<>();
        filterNames = new ArrayList<>();

        numDaysView.setSelection(2); //set days to view as 3
        dayChange(numDaysView);

        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_MONTH, numDays);
        updateDates();
        scheduler = new Scheduler(this, family);
        if(family == null)
            Log.e("CalendarActivity","Family loaded as null");
        //hide add-subject if they are a child
        boolean isParent = family.getMember(this, auth.getEmail()).getIsParent();
        if(!isParent)
            addSubjectButton.setVisibility(View.GONE);
        populateFilters(isParent);
        ArrayList<Person> children = family.getChildren(this);
        drawer.givePeople(family.getMember(this, auth.getEmail()), children);
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

        functions.adInit(family.getMember(this, auth.getEmail()));

        Log.d("CalendarActivity","Calendar Activity starting");
    }


    @Override
    public void onBackPressed(){}

    //toggles filter view
    public void filterToggle(View view){
        if(filterToggle.isChecked())
            filterView.setVisibility(View.VISIBLE);
        else
            filterView.setVisibility(View.GONE);
        //redraw canvas after change
        filterView.post(new Runnable(){@Override public void run(){drawer.viewChange(drawer.getWidth(),drawer.getHeight());}});
    }

    private void populateFilters(boolean isParent) {
        ArrayList<Person> familyMembers = family.getMembers(this);
        if(isParent) {
            for (int i = 0; i < familyMembers.size(); i++)
                if (!familyMembers.get(i).getIsParent()) {
                    addFilter(familyMembers.get(i).getName());
                    for(int subject = 0; subject < familyMembers.get(i).getSubjects().size(); subject++){
                        addFilter(familyMembers.get(i).getSubjects().get(subject).subjectName);
                    }
                }
        }else {
            Person user = family.getMember(this, auth.getEmail());
            for(int i = 0; i < user.getSubjects().size(); i ++)
                addFilter(user.getSubjects().get(i).subjectName);
        }
    }

    private void addFilter(String name){
        Random random = new Random();
        int color = Color.argb(255,random.nextInt(256),random.nextInt(256),random.nextInt(256));
        filterColors.add(color); //adds colors to list
        //creates check boxes and sets color
        CheckBox filt = new CheckBox(this);
        filt.setText(name);
        filt.toggle();
        filt.setButtonTintList(ColorStateList.valueOf(color));
        filterView.addView(filt);
        filterChecks.add(filt);
        filterList.add(true);
        filterNames.add(name);
        drawer.updateFilter(filterList, filterNames, filterColors);
        filt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View checkBox) {
                updateFilter(checkBox);
            }
        });
    }

    private void updateFilter(View checkBox){
        //Known Bug: if name of person matches subject; bad things
        Log.d("CalendarActivity","Filter " + ((CheckBox) checkBox)
                .getText().toString() + " toggled");
        for(int i = 0; i < filterChecks.size(); i++){
            if(checkBox == filterChecks.get(i)) {
                filterList.set(i, ((CheckBox) checkBox).isChecked());
                Person user = family.getMemberByName(this,((CheckBox) checkBox)
                        .getText().toString());
                if(user != null)
                    for(int e = i + 1; e < user.getSubjects().size() + i + 1; e++) {
                        filterChecks.get(e).setChecked(((CheckBox) checkBox).isChecked());
                        updateFilter(filterChecks.get(e));
                    }
            }
        }
        drawer.updateFilter(filterList, filterNames, filterColors);
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

    public void goToViewSubjectsActivity(View view){functions.goToActivity(ViewSubjects.class);}
    public void goToAddSubjectActivity(View view){
        functions.goToActivity(AddSubject.class);
    }
    public void goToSettingsActivity(View view){
        functions.goToActivity(SettingsActivity.class);
    }

    @Override
    public void authResult(TaskName result) {}
}