package com.EventHorizon.homeschoolr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements AuthListener{
    Auth auth;
    Functions functions;
    TextView emailView;
    TextView nameView;
    TextView familyNameView;
    TableRow enterEmailDialog;
    EditText inviteEmail;
    String email;
    LinearLayout inviteContainer;


    Person user;
    Family family;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        auth = new Auth(this);
        functions = new Functions(this);

        nameView = findViewById(R.id.name);
        emailView = findViewById(R.id.email);
        familyNameView = findViewById(R.id.familyName);
        enterEmailDialog = findViewById(R.id.EnterEmailDialog);
        inviteEmail = findViewById(R.id.inviteEmail);
        inviteContainer = findViewById(R.id.inviteContainer);
        email = auth.getEmail();
        emailView.setText(email);

        family = Family.load(this);
        user = Person.load(this, email);

        populateInvites();
        familyNameView.setText(family.getFamilyName());
        nameView.setText(user.getName());
    }

    //todo add name pulled from database

    //Logout button clicked
    public void logout(View view){
        auth.signOut();
        functions.goToActivity(LoginActivity.class);
    }

    //Delete Account button clicked
    public void deleteAccount(View view){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.deleteConfirmation))
                .setPositiveButton(getString(R.string.yes),dialogClickListener)
                .setNegativeButton(getString(R.string.no),dialogClickListener).show();
    }

    public void addInvite(View view){
        String inviteEmailString = inviteEmail.getText().toString();
        if(!functions.checkEmail(inviteEmail.getText().toString()))
            return;
        else {
            inviteEmail.setText("");
            family.inviteMember(inviteEmailString, this);
            addInvite(inviteEmailString);
        }
    }

    //Verification for account deletion
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which==DialogInterface.BUTTON_POSITIVE) {
                //auth.deleteAccount();
            }else
                functions.showMessage(getString(R.string.deleteCancel),false);
        }
    };

    public void deleteInvite(View view){
        String email = ((TextView)((TableRow)view.getParent()).getChildAt(0)).getText().toString();
        family.removeInvite(email, this);
        ((ViewGroup)(view.getParent().getParent())).removeView((View)view.getParent());
    }

    public void populateInvites(){
        ArrayList<String> invited = family.getInviteEmailList();
        for(int i = 0; i < invited.size(); i++) {
            addInvite(invited.get(i));
        }
    }
    public void addInvite(String email){
        getLayoutInflater().inflate(R.layout.invite_row, inviteContainer, true);
        ConstraintLayout layout = (ConstraintLayout) inviteContainer.getChildAt(inviteContainer.getChildCount() - 1);
        TableRow inviteRow = (TableRow) layout.getChildAt(0);
        ((ViewGroup)inviteRow.getParent()).removeView(inviteRow);
        inviteContainer.addView(inviteRow);
        TextView emailView = (TextView) inviteRow.getChildAt(0);
        emailView.setText(email);
    }

    @Override
    public void authResult(TaskName result) {

    }
}
