package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
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
    ArrayList<Person> timerPeople;

    String exportString = "";

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_subjects);

        theList = findViewById(R.id.theList);
        timers = new ArrayList<>();
        timerText = new ArrayList<>();
        timerSubjects = new ArrayList<>();
        timerPeople = new ArrayList<>();

        auth = new Auth(this);
        family = Family.load(this);
        user = family.getMember(this, auth.getEmail());
        functions = new Functions(this);
        functions.adInit(user);

        if(user.getIsParent()) {
            loadChildrenSubjects();
        }else {
            loadSubjects(auth.getEmail());
        }
        timer();
    }

    @Override
    protected void onStart() {
        super.onStart();

        context = this;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        for(int i = 0; i < timers.size(); i++)
            if(timers.get(i).isChecked())
                clockOut(i);
        functions.goToActivity(CalendarActivity.class);
    }

    public void share(View view){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, exportString);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
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
        Space space = new Space(this);
        space.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , 30));
        theList.addView(space);

        child = members.get(index);
        TextView personText = new TextView(this);
        personText.setText(child.getName());
        personText.setTextSize(30);
        theList.addView(personText);
        exportString += "-------------------\n" + child.getName() + "\n"
                + "-------------------\n";

        final ArrayList<Subject> subjects = child.getSubjects();
        for(int subject = 0; subject < subjects.size(); subject++){
            Space space2 = new Space(this);
            space2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , 15));
            theList.addView(space2);

            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));

            //Setting subject text
            final TextView subjectText = new TextView(this);
            subjectText.setText(subjects.get(subject).subjectName);
            final int tempSubject = subject;
            final String childEmail = child.getEmail();
            if(user.getIsParent()) {
                subjectText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, EditSubject.class);
                        intent.putExtra("child", childEmail);
                        intent.putExtra("subjectIndex", tempSubject);
                        startActivity(intent);
                    }
                });
            }
            subjectText.setTextSize(20);
            tableRow.addView(subjectText);
            exportString += "----------\n" + subjects.get(subject).subjectName + "\n"
                    + "----------\n";

            //Setting start time text
            final TextView startTimeText = new TextView(this);
            String startTime = "Start Time: ";
            startTime += Scheduler.floatToHrMin(subjects.get(subject).start);
            startTimeText.setText(startTime);
            startTimeText.setTextSize(15);
            exportString += startTime + "\n";

            //Setting days text
            final TextView dayText = new TextView(this);
            dayText.setTextSize(15);
            String[] days = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};
            String text = "days: ";
            Boolean[] lessonDays = subjects.get(subject).weekdays;
            for (int day = 0; day < lessonDays.length; day++) {
                if (lessonDays[day])
                    text += days[day] + ", ";
            }
            dayText.setText(text);
            exportString += text + "\n";

            //Setting numLessons
            final TextView numLessonsText = new TextView(this);
            String numLessonsString = "Number of lessons: "+subjects.get(subject).numLessons;
            numLessonsText.setText(numLessonsString);
            exportString += numLessonsString + "\n";

            //Lessons Completed Text
            final TextView lessonsCompleted = new TextView(this);
            String lessonsCompletedString = "Lessons Completed: "+subjects.get(subject).lessonsCompleted;
            lessonsCompleted.setText(lessonsCompletedString);
            exportString += lessonsCompletedString + "\n";

            //Setting hour and minute length
            final TextView hourMinText = new TextView(this);
            String timePerLessonString = "Time per lesson: "+subjects.get(subject).hrLength+":"
                    +subjects.get(subject).minLength;
            hourMinText.setText(timePerLessonString);
            exportString += timePerLessonString + "\n";

            //Time Clock text
            final TextView timeClock = new TextView(this);
            timeClock.setTextSize(17);
            float time = subjects.get(subject).timeWorked;
            String timeStr = Scheduler.floatToHrMinSec(time);
            String timeClockString = "Time Clocked: "+timeStr;
            timeClock.setText(timeClockString);
            exportString += timeClockString + "\n";

            //Completed Button
            if(subjects.get(subject).completedChild == null)
                subjects.get(subject).completedChild = false;
            if(!user.getIsParent() || subjects.get(subject).completedChild || child.getIsNoAccountChild()) {
                final Button completedButton = new Button(this);
                completedButton.setText("Completed");
                tableRow.addView(completedButton);
                if(!user.getIsParent() && subjects.get(subject).completedChild)
                    completedButton.setEnabled(false);
                final int finalSubject = subject;
                final int finalIndex = index;
                completedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (user.getIsParent()){
                            subjects.get(finalSubject).lessonsCompleted++;
                            subjects.get(finalSubject).completedChild = false;
                            String lessonsCompletedString = "Lessons Completed: "+subjects
                                    .get(finalSubject).lessonsCompleted;
                            lessonsCompleted.setText(lessonsCompletedString);
                            if(!child.getIsNoAccountChild())
                                completedButton.setVisibility(View.GONE);
                        }else{
                            subjects.get(finalSubject).completedChild = true;
                            completedButton.setEnabled(false);
                        }
                        members.get(finalIndex).save(context);
                    }
                });
            }

            if(!user.getIsParent() || child.getIsNoAccountChild()) {
                //Timer Toggle
                Switch timerToggle = new Switch(this);
                timers.add(timerToggle);
                timerPeople.add(child);
                timerSubjects.add(subjects.get(subject));
                timerText.add(timeClock);
                timerToggle.setChecked(false);
                final Context context = this;
                final int timerIndex = timerSubjects.size() - 1;
                //final int subjectIndex = subject;
                timerToggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!((Switch)v).isChecked()) {
                            clockOut(timerIndex);
                        }else{
                            clockIn(timerIndex);
                        }
                    }
                });
                tableRow.addView(timerToggle);
            }

            theList.addView(tableRow);
            theList.addView(timeClock);
            theList.addView(dayText);
            theList.addView(numLessonsText);
            theList.addView(hourMinText);
            theList.addView(startTimeText);
            theList.addView(lessonsCompleted);
//            subjectText.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    functions.goToActivity(AddSubject.class);
//                }
//            });
        }
    }

    private void clockIn(final int timerIndex){
        functions.showMessage(getString(R.string.clockInTo)+" "+timerSubjects.get(timerIndex).subjectName, false);
    }
    private void clockOut(final int timerIndex){
        timerPeople.get(timerIndex).save(context);
        functions.showMessage(getString(R.string.clockOutOf)+" "+timerSubjects.get(timerIndex).subjectName,false);
    }

    private void timer(){
        final Handler handler = new Handler();
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                for(int i = 0; i < timers.size(); i++){
                    if(timers.get(i).isChecked())
                        timerSubjects.get(i).timeWorked += (float)(1.0/3600.0);
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