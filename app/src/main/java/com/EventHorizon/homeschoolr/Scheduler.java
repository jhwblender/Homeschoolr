package com.EventHorizon.homeschoolr;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class Scheduler implements TaskListener{
    Family family;
    Context context;

    public Scheduler(Context context, Family family){
        this.family = family;
        this.context = context;

        ArrayList<Person> members = family.getMembers(context);
        for(int i = 0; i < members.size(); i++){
            if(!members.get(i).getIsParent()) {
                if(checkTimes(members.get(i)))
                    members.get(i).save(context);
            }
        }
    }

    public static float hrMinToFloat(int hr, int min){
        return (float)hr + (float)min/60.0f;
    }
    public static int floatToHr(float time){
        return (int)time;
    }
    public static int floatToMin(float time){
        return (int)(60 * (time - floatToHr(time)));
    }
    public static int floatToSec(float time){
        return (int)(3600 * ((time - floatToHr(time)) - floatToMin(time)/60.0));
    }
    public static String floatToHrMinSec(float time){
        int hr = floatToHr(time);
        int min = floatToMin(time);
        int sec = floatToSec(time);
        String hrStr = (hr < 10)? "0"+String.valueOf(hr) : String.valueOf(hr);
        String minStr = (min < 10)? "0"+String.valueOf(min) : String.valueOf(min);
        String secStr = (sec < 10)? "0"+String.valueOf(sec) : String.valueOf(sec);
        return hrStr+":"+minStr+":"+secStr;
    }
    public static String floatToHrMin(float time){
        int hr = floatToHr(time);
        int min = floatToMin(time);
        String hrStr = (hr < 10)? "0"+String.valueOf(hr) : String.valueOf(hr);
        String minStr = (min < 10)? "0"+String.valueOf(min) : String.valueOf(min);
        return hrStr+":"+minStr;
    }

    private boolean checkForConflict(ArrayList<Float> startTimes, ArrayList<Float> endTimes, float startTry){
        boolean good = true;
        for(int i = 0; i < startTimes.size(); i++)
            if(startTry > startTimes.get(i) && startTry < endTimes.get(i))
                return false;
        return true;
    }

    private boolean checkTimes(Person user){
        ArrayList<Subject> subjects = user.getSubjects();
        ArrayList<Float> startTimes = new ArrayList<>();
        ArrayList<Float> endTimes = new ArrayList<>();
        //check used times
        for(int i = 0; i < subjects.size(); i++){
            if(subjects.get(i).start != null){
                startTimes.add(subjects.get(i).start);
                endTimes.add(subjects.get(i).start + hrMinToFloat(subjects.get(i).hrLength, subjects.get(i).minLength));
            }
        }
        //help null ones
        boolean changed = false;
        for(int i = 0; i < subjects.size(); i++){
            if(subjects.get(i).start == null){
                changed = true;
                Random random = new Random();
                float startTry;
                boolean good;
                do {

                    startTry = random.nextFloat() * (24 - hrMinToFloat(subjects.get(i).hrLength, subjects.get(i).minLength));
                    good = checkForConflict(startTimes, endTimes, startTry);
                }while(!good);
                subjects.get(i).start = startTry;
            }
        }
        return changed;
    }

    @Override
    public void authResult(TaskName result) {}
}
