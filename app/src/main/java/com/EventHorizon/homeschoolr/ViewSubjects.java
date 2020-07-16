package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewSubjects extends AppCompatActivity implements TaskListener{

    Auth auth;
    Functions functions;
    Family family;
    Person user;
    Person child;

    LinearLayout theList;

    ArrayList<Switch> timers;
    ArrayList<TextView> timerText;
    ArrayList<Subject> timerSubjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_subjects);

        theList = findViewById(R.id.theList);
        timers = new ArrayList<>();
        timerText = new ArrayList<>();
        timerSubjects = new ArrayList<>();

        auth = new Auth(this);
        functions = new Functions(this);
        family = Family.load(this);
        user = family.getMember(this, auth.getEmail());

        if(user.getIsParent()) {
            loadChildrenSubjects();
        }else {
            loadSubjects(auth.getEmail());
            timer();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        functions.goToActivity(CalendarActivity.class);
    }



    private void loadChildrenSubjects(){
        ArrayList<Person> members = family.getMembers(this);
        for(int i = 0; i < members.size(); i++)
            if(!members.get(i).getIsParent())
                loadSubjects(members, i);
    }
    private  void loadSubjects(String email){
        ArrayList<Person> members = family.getMembers(this);
        for(int i = 0; i < members.size(); i++)
            if(members.get(i).getEmail().equals(email))
                loadSubjects(members, i);
    }
    private void loadSubjects(final ArrayList<Person> members, final int index){
        child = members.get(index);
        TextView personText = new TextView(this);
        personText.setText(child.getName());
        personText.setTextSize(30);
        theList.addView(personText);

        final ArrayList<Subject> subjects = child.getSubjects();
        for(int subject = 0; subject < subjects.size(); subject++){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));

            //Setting subject text
            final TextView subjectText = new TextView(this);
            subjectText.setText(subjects.get(subject).subjectName);
            subjectText.setTextSize(20);
            tableRow.addView(subjectText);

            //Setting start time text
            final TextView startTimeText = new TextView(this);
            String startTime = "Start Time: ";
            startTime += Scheduler.floatToHrMin(subjects.get(subject).start);
            startTimeText.setText(startTime);
            startTimeText.setTextSize(15);

            //Setting days text
            final TextView dayText = new TextView(this);
            try {
                dayText.setTextSize(15);
                String[] days = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};
                String text = "days: ";
                boolean[] lessonDays = subjects.get(subject).weekdays;
                for (int day = 0; day < lessonDays.length; day++) {
                    if (lessonDays[day])
                        text += days[day] + ", ";
                }
                dayText.setText(text);
            }catch(Exception e){
                Log.d("ViewSubjects",e.getMessage());
            }

            //Setting numLessons and Hour/Min Length
            final TextView numLessonsText = new TextView(this);
            final TextView hourMinText = new TextView(this);
            numLessonsText.setText("Number of lessons: "+subjects.get(subject).numLessons);
            hourMinText.setText("Time per lesson: "+subjects.get(subject).hrLength+":"+subjects.get(subject).minLength);

//            //Time Clock
//            final TextView timeClock = new TextView(this);
//            float time = subjects.get(subject).timeWorked;
//            String timeStr = Scheduler.floatToHrMinSec(time);
//            timeClock.setText("Time Clocked: "+timeStr);
//            timeClock.setTextSize(20);
//            subjects.add(subjects.get(subject));

            TextView timeClock = new TextView(this);
            timeClock.setTextSize(17);
            float time = subjects.get(subject).timeWorked;
            String timeStr = Scheduler.floatToHrMinSec(time);
            timeClock.setText("Time Clocked: "+timeStr);

            if(user.getIsParent()) {
                //Setting button settings
                final Button button = new Button(this);
                button.setText("Remove");
                button.setTextColor(Color.RED);
                final int finalI = subject;
                final Context context = this;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subjects.remove(finalI);
                        members.get(index).save(context);
                        button.setVisibility(View.GONE);
                        subjectText.setVisibility(View.GONE);
                        dayText.setVisibility(View.GONE);
                        numLessonsText.setVisibility(View.GONE);
                        hourMinText.setVisibility(View.GONE);
                        startTimeText.setVisibility(View.GONE);
                        //timeClock.setVisibility(View.GONE);
                    }
                });

                tableRow.addView(button);
                //theList.addView(button);
            }else{
                Switch timerToggle = new Switch(this);
                timerToggle.setChecked(false);
                final Context context = this;
                timerToggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!((Switch)v).isChecked()) {
                            user.subjects = timerSubjects;
                            user.save(context);
                        }
                    }
                });
                tableRow.addView(timerToggle);
                timers.add(timerToggle);
                timerSubjects.add(subjects.get(subject));
            }

            theList.addView(tableRow);
            theList.addView(timeClock);
            theList.addView(dayText);
            theList.addView(numLessonsText);
            theList.addView(hourMinText);
            theList.addView(startTimeText);
//            subjectText.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    functions.goToActivity(AddSubject.class);
//                }
//            });
        }
    }

    private void timer(){
        final Handler handler = new Handler();
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                for(int i = 0; i < timers.size(); i++){
                    if(timers.get(i).isChecked())
                        timerSubjects.get(i).timeWorked += (1.0/3600);
                        float time = timerSubjects.get(i).timeWorked;
                        String timeStr = Scheduler.floatToHrMinSec(time);
                        timerText.get(i).setText("Time Clocked: "+timeStr);
                }
                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void authResult(TaskName result) {
        Log.d("ViewSubjects",result.toString());
    }

}