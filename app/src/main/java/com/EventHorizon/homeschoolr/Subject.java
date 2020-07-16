package com.EventHorizon.homeschoolr;

import android.util.Log;

import java.util.Calendar;

public class Subject {
    String subjectName;
    Boolean[] weekdays;
    Integer numLessons;
    Integer hrLength;
    Integer minLength;
    Integer lessonsCompleted = 0;
    Float length = null;
    Float start = null;
    Float timeWorked = 0.0f;
    Boolean completedChild = false;

    Subject(String subjectName, Integer numLessons, Boolean[] weekDays, Integer hrLength, Integer minLength, Float timeWorked, Integer lessonsCompleted){
        this.subjectName = subjectName;
        this.weekdays = weekDays;
        this.numLessons = numLessons;
        this.hrLength = hrLength;
        this.minLength = minLength;
        this.length = Scheduler.hrMinToFloat(hrLength, minLength);
        this.timeWorked = timeWorked;
        this.lessonsCompleted = lessonsCompleted;
    }

    public String toString(){
        String[] weekdays = {"sun","mon","tue","wed","thu","fri","sat"};
        String output = "Subject Name: "+subjectName;
        try {
            output += "Lesson days: ";
            for (int i = 0; i < this.weekdays.length; i++)
                if (this.weekdays[i])
                    output += weekdays[i] + " ";
        }catch (Exception e){
            Log.e("Subject",e.getMessage());
        }
        output += "Days: "+ numLessons;
        output += "Length: "+hrLength+":"+minLength;
        return output;
    }
}
