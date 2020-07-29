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
import android.view.DragEvent;
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
    int touchX,touchY, startTouchX, startTouchY;
    boolean touching;
    boolean transitioning = false;
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

    //Dragging
    float hourDiff;
    int dayStart;
    Subject currentSubject = null;

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
//        if(user.getIsParent()){
            for(int c = 0; c < children.size(); c++){
                ArrayList<Subject> subjects = children.get(c).getSubjects();
                for(int s = 0; s < subjects.size(); s++){
                    for(int day = 0; day < numDays; day++){
                        if(subjects.get(s).weekdays[(dayOfWeek + day - 1)%7]
                                && ((!user.getIsParent() && filter.get(count)) || (user.getIsParent()
                                && filter.get(count + 1))))
                            drawSubject(subjects.get(s), day, count, canvas);
                    }
                    count++;
                }
                count++;
            }
    }
    private void drawSubject(Subject subject, int day, int count, Canvas canvas){
        if(user.getIsParent())
            drawPaint.setColor(colors.get(count + 1));
        else
            drawPaint.setColor(colors.get(count));
        drawPaint.setStyle(Paint.Style.FILL);
        Rect rectangle = new Rect();
        float startHour = subject.start;
        float endHour = subject.start + Scheduler.hrMinToFloat(subject.hrLength,subject.minLength);
        rectangle.set(textWidth + dayWidth * day, hourHeight * 2 + (int) (startHour * hourHeight), textWidth + dayWidth * (day + 1), hourHeight * 2 + (int) (endHour * hourHeight));
        canvas.drawRect(rectangle, drawPaint);
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

    @Override
    //This function grabs any touch events for our own purposes
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        //Log.d("CalendarDraw",String.valueOf(event.getAction()));
        touchX = (int)event.getX();
        touchY = (int)event.getY();

        int touchDay = (touchX - textWidth)/dayWidth;
        float touchHr = (float)(touchY - 2 * hourHeight)/hourHeight;

        if(touchX > textWidth && touchY > 2 * hourHeight && event.getAction() != 2) {
            currentSubject = checkSubjectTouch(touchDay, touchHr, event.getAction());
        }

        if(currentSubject != null && user.getIsParent()){
            if(event.getAction() == 0) {
                dayStart = touchDay;
                hourDiff = touchHr - currentSubject.start;
            }else if(event.getAction() == 2) {
                currentSubject.start = touchHr - hourDiff;
                currentSubject.start = Math.round((currentSubject.start/(5.0/60.0)))*(5.0f/60.0f);
                shiftSchedule(touchDay - dayStart, touchDay);
            }
        }

        postInvalidate();
        return true;
        //return super.dispatchTouchEvent(event);
    }

    private void shiftSchedule(int days, int touchDay){
        days += 7;
        Boolean[] subDays = currentSubject.weekdays.clone();
        for(int i = 0; i < 7; i++){
            subDays[(i + days)%7] = currentSubject.weekdays[i];
        }
        currentSubject.weekdays = subDays;
        dayStart = touchDay;
    }

    private Subject checkSubjectTouch(int touchDay, float touchHr, int action){
        int count = 0;
//        if(user.getIsParent()){
            for(int c = 0; c < children.size(); c++){
                ArrayList<Subject> subjects = children.get(c).getSubjects();
                for(int s = 0; s < subjects.size(); s++){
                    if(subjects.get(s).weekdays[(dayOfWeek + touchDay - 1)%7]
                            && ((!user.getIsParent() && filter.get(count)) || (user.getIsParent()
                            && filter.get(count + 1))) && subjects.get(s).start <= touchHr
                            && subjects.get(s).start + subjects.get(s).length >= touchHr) {
                        if(action == 1 && context != null)
                            children.get(c).save(context);
                        //Log.d("CalendarDraw", "Touching: " + subjects.get(s).subjectName);
                        return subjects.get(s);
                    }
                    count++;
                }
                count++;
            }
        return null;
    }

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

    public CalendarDraw(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
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
    }

    public void givePeople(Person user, ArrayList<Person> children){
        this.user = user;
        this.children = children;
        if(user != null && !user.getIsParent()) {
            this.children = new ArrayList<>();
            this.children.add(user);
        }
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
