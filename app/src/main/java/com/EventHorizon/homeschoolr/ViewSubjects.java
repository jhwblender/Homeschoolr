package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.firestore.auth.User;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ViewSubjects extends AppCompatActivity implements TaskListener{

    Auth auth;
    Functions functions;
    Family family;
    Person user;

    LinearLayout theList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_subjects);

        theList = findViewById(R.id.theList);

        auth = new Auth(this);
        functions = new Functions(this);
        family = Family.load(this);
        user = family.getMember(this, auth.getEmail());

        if(user.getIsParent())
            loadChildrenSubjects();
        else
            loadSubjects(auth.getEmail());
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void loadChildrenSubjects(){
        ArrayList<Person> members = family.getMembers(this);
        for(int i = 0; i < members.size(); i++)
            if(!members.get(i).getIsParent())
                loadSubjects(members.get(i).getEmail());
    }
    private void loadSubjects(String email){
        user = family.getMember(this, email);
        TextView personText = new TextView(this);
        personText.setText(user.getName());
        personText.setTextSize(30);
        theList.addView(personText);

        final ArrayList<Subject> subjects = user.getSubjects();
        for(int i = 0; i < subjects.size(); i++){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            final Button button = new Button(this);
            button.setText("Remove");
            button.setTextColor(Color.RED);
            final int finalI = i;
            final Context context = this;
            final TextView subjectText = new TextView(this);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subjects.remove(finalI);
                    user.save(context);
                    button.setVisibility(View.GONE);
                    subjectText.setVisibility(View.GONE);
                }
            });
            subjectText.setText(subjects.get(i).subjectName);
            Log.d("ViewSubjects",subjects.get(i).subjectName);
            subjectText.setTextSize(20);
            tableRow.addView(subjectText);
            tableRow.addView(button);
            theList.addView(tableRow);
            subjectText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    functions.goToActivity(AddSubject.class);
                }
            });
        }
    }

    @Override
    public void authResult(TaskName result) {

    }
}