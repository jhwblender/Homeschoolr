package com.EventHorizon.homeschoolr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.firestore.auth.User;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CalendarDraw extends View {
    private Paint drawPaint;
    int touchX,touchY;
    boolean touching;
    boolean transitioning = false;
    int dayTouch;
    int width, height;
    int numDays;
    int dayOfWeek;
    Calendar startDate;
    boolean isManager;
    ArrayList<Integer> colors;
    ArrayList<Boolean> filter;
    ArrayList<String> filterNames;
    final String[] dayNames = {"Sat","Sun","Mon","Tue","Wed","Thurs","Fri"};

    ArrayList<Person> children;
    Auth auth;
    Person user;

    //drawing values
    int textWidth;
    int dayWidth;
    int hourHeight;
    Context context;

    @Override
    //this is where we can draw everything
    protected void onDraw(Canvas canvas)
    {
        drawBackground(canvas);
        drawSubjects(canvas);
    }

    private void drawBackground(Canvas canvas){
        //set paint properties
        drawPaint.setStrokeWidth(2);
        hourHeight = height/26;
        drawPaint.setTextSize(hourHeight);
        textWidth = (int)drawPaint.measureText("00:00",0,"00:00".length()) + 10;

        //Drawing days
        if(numDays>0 && startDate != null) {
            dayOfWeek = startDate.get(Calendar.DAY_OF_WEEK);
            dayWidth = (width - textWidth) / numDays;
            canvas.drawLine(textWidth, 0, textWidth, height, drawPaint);
            drawPaint.setTextAlign(Paint.Align.CENTER);
            for (int day = 0; day < numDays; day++) {
                drawPaint.setColor(Color.WHITE);

                canvas.drawText(dayNames[(dayOfWeek + day)%7], textWidth + dayWidth * (day+.5f), hourHeight,drawPaint);
                canvas.drawLine(textWidth + dayWidth * day, 0, textWidth + dayWidth * day, height, drawPaint);

                Calendar date = (Calendar)startDate.clone();
                date.add(Calendar.DAY_OF_MONTH,day);
//                if(isManager) {
//                    drawManagerDay(Company.loadCompany(context), date, day, canvas);
//                }else{
//                    drawEmployeeDay(User.loadUser(context), date, day, canvas);
//                }
            }
        }

        //Drawing hours
        drawPaint.setTextAlign(Paint.Align.LEFT);
        drawPaint.setColor(Color.WHITE);
        for(int i = 0; i < 25; i++){
            canvas.drawText(String.format("%02d:00",i%24),0,i*hourHeight+2*hourHeight,drawPaint);
            canvas.drawLine(textWidth,i*hourHeight+2*hourHeight,width,i*hourHeight+2*hourHeight,drawPaint);
        }
    }

    private void drawSubjects(Canvas canvas){
        int count = 0;
        if(user.getIsParent()){
            for(int c = 0; c < children.size(); c++){
                ArrayList<Subject> subjects = children.get(c).getSubjects();
                for(int s = 0; s < subjects.size(); s++){
                    for(int day = 0; day < numDays; day++){
                        if(subjects.get(s).weekdays[(dayOfWeek + day - 1)%7] && filter.get(count + 1))
                            drawSubject(subjects.get(s), day, count, canvas);
                    }
                    count++;
                }
                count++;
            }
        }else {
            ArrayList<Subject> subjects = user.getSubjects();
            for(int s = 0; s < subjects.size(); s++){
                for(int day = 0; day < numDays; day++){
                    if(subjects.get(s).weekdays[(dayOfWeek + day)%7] && filter.get(count + 1))
                        drawSubject(subjects.get(s), day, count, canvas);
                }
                count++;
            }
        }
    }
    private void drawSubject(Subject subject, int day, int count, Canvas canvas){
        drawPaint.setColor(colors.get(count + 1));
        drawPaint.setStyle(Paint.Style.FILL);
        Rect rectangle = new Rect();
        float startHour = subject.start;
        float endHour = subject.start + Scheduler.hrMinToFloat(subject.hrLength,subject.minLength);
        rectangle.set(textWidth + dayWidth * day, hourHeight * 2 + (int) (startHour * hourHeight), textWidth + dayWidth * (day + 1), hourHeight * 2 + (int) (endHour * hourHeight));
        canvas.drawRect(rectangle, drawPaint);
    }

    private void drawPreference(Canvas canvas, int offset, int filterIndex, Calendar startTime, Calendar endTime, int numEmployees) {
//        float startHour = (float) startTime.get(Calendar.HOUR_OF_DAY) + (float) startTime.get(Calendar.MINUTE) / 60;
//        float endHour = (float) endTime.get(Calendar.HOUR_OF_DAY) + (float) endTime.get(Calendar.MINUTE) / 60;
//        drawPaint.setColor(colors.get(filterIndex));
//        drawPaint.setStyle(Paint.Style.FILL);
//        Rect rectangle = new Rect();
//        rectangle.set(textWidth + dayWidth * offset, hourHeight * 2 + (int) (startHour * hourHeight), textWidth + dayWidth * (offset + 1), hourHeight * 2 + (int) (endHour * hourHeight));
//        canvas.drawRect(rectangle, drawPaint);
//        if (isManager && filterIndex == 0) {
//            drawPaint.setTextAlign(Paint.Align.CENTER);
//            drawPaint.setColor(Color.BLACK);
//            drawPaint.setStrokeWidth(5);
//            drawPaint.setStyle(Paint.Style.STROKE);
//            canvas.drawRect(rectangle, drawPaint);
//            canvas.drawText(Integer.toString(numEmployees), textWidth + 0.5f * dayWidth + dayWidth * offset, hourHeight * 2 + (int) (.5f * (startHour + endHour) * hourHeight + 0.5f * hourHeight), drawPaint);
//            drawPaint.setStrokeWidth(0);
//            drawPaint.setColor(Color.WHITE);
//            canvas.drawText(Integer.toString(numEmployees), textWidth + 0.5f * dayWidth + dayWidth * offset, hourHeight * 2 + (int) (.5f * (startHour + endHour) * hourHeight + 0.5f * hourHeight), drawPaint);
//        }
    }

    private Calendar stringToCalendar(String string) {
        SimpleDateFormat formatter2 = new SimpleDateFormat("\"MMM d, yyyy h:mm:ss a\"");//Mar 31, 2020 4:05:00 PM
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(formatter2.parse(string));
        } catch (ParseException e) {
            Log.e("CalendarDraw", "ERROR on " + string);
            Log.e("CalendarDraw", "Error Code: " + e.toString());
        }
        return calendar;
    }

    public void resetTransitioning(){
        transitioning = false;
        touching = false;
    }

//    @Override
//    //This function grabs any touch events for our own purposes
//    public boolean dispatchTouchEvent(MotionEvent event)
//    {
//        touchX = (int)event.getX();
//        touchY = (int)event.getY();
//        touching = true;
//
//        postInvalidate();
//        return true;
//    }

    void updateIfManager(boolean isManager, Context context){
        this.isManager = isManager;
        this.context = context;
        postInvalidate();
    }

    public void updateColors(ArrayList<Integer> colors){
        this.colors = colors;
        postInvalidate();
    }
    public void updateFilter(ArrayList<Boolean> filter, ArrayList<String> filterNames, ArrayList<Integer> filterColors){
        this.filter = filter;
        this.filterNames = filterNames;
        this.colors = filterColors;
        postInvalidate();
    }

//    //this is called when a touch occurs on a day
//    void editWorkDay(int day){
//        Intent intent;
////        if(isManager)
////            intent = new Intent(this.getContext(), TimePreferencesManagerActivity.class);
////        else
////            intent = new Intent(this.getContext(), TimePreferencesEmployeeActivity.class);
//        Calendar toEdit = (Calendar)startDate.clone();
//        toEdit.add(Calendar.DAY_OF_MONTH, day);
//        Gson gson = new Gson();
//        String date = gson.toJson(toEdit.getTime());
////        intent.putExtra("date",date);
////        this.getContext().startActivity(intent);
//    }

    public CalendarDraw(final Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                height = bottom - top;
                width = right - left;
                Log.d("CalendarDraw","Layout Changed: ("+width+", "+height+")");
                postInvalidate();
            }
        });
//        family = Family.load(context);
//        auth = new Auth((Activity)context);
//        user = family.getMember(context, auth.getEmail());
    }

    public void givePeople(Person user, ArrayList<Person> children){
        this.user = user;
        this.children = children;
    }

    //called when the view changes size
    public void viewChange(int width, int height){
        this.width = width;
        this.height = height;
        postInvalidate();
    }

    //number of days change
    public void daysChange(int days){
        numDays = days;
        postInvalidate();
    }

    public void startDateChange(Calendar startDate){
        this.startDate = startDate;
        postInvalidate();
    }

    private void setupPaint(){
        drawPaint = new Paint();
        drawPaint.setColor(Color.WHITE);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(2);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }
}
