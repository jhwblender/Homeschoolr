package com.EventHorizon.homeschoolr;

public class Subject {
    String subjectName;
    boolean[] lessonDays;
    int days;
    int hrLength;
    int minLength;

    Subject(String subjectName, boolean[] lessonDays, int days,int hrLength,int minLength){
        this.subjectName = subjectName;
        this.lessonDays = lessonDays;
        this.days = days;
        this.hrLength = hrLength;
        this.minLength = minLength;
    }
}
