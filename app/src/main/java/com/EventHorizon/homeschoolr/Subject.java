package com.EventHorizon.homeschoolr;

public class Subject {
    String subjectName;
    boolean[] numLessons;
    int days;
    int hrLength;
    int minLength;

    Subject(String subjectName, boolean[] numLessons, int days,int hrLength,int minLength){
        this.subjectName = subjectName;
        this.numLessons = numLessons;
        this.days = days;
        this.hrLength = hrLength;
        this.minLength = minLength;
    }
}
