package com.EventHorizon.homeschoolr;

import android.util.Log;

import java.util.Calendar;

public class Subject {
    String subjectName;
    boolean[] weekdays;
    int numLessons;
    int hrLength;
    int minLength;
    Float length = null;
    Float start = null;

    Subject(String subjectName, int numLessons, boolean[] weekDays, int hrLength,int minLength){
        this.subjectName = subjectName;
        this.weekdays = weekDays;
        this.numLessons = numLessons;
        this.hrLength = hrLength;
        this.minLength = minLength;
        this.length = Scheduler.hrMinToFloat(hrLength, minLength);
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
