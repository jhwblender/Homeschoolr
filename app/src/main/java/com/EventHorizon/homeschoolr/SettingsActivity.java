package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements DatabaseListener{
    Auth auth;
    Database database;
    Functions functions;
    TextView emailView;
    TextView nameView;
    TextView familyNameView;
    HashMap<String, Object> personalInfo;
    Context context = this;
    Map<String, Object> info;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        auth = new Auth(this);
        database = new Database(this);
        functions = new Functions(this);

        nameView = findViewById(R.id.name);
        emailView = findViewById(R.id.email);
        familyNameView = findViewById(R.id.familyName);
        email = auth.getEmail();
        emailView.setText(email);
        database.getName();
        database.getFamilyName();
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

    //Verification for account deletion
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which==DialogInterface.BUTTON_POSITIVE) {
                auth.deleteAccount();
            }else
                functions.showMessage(getString(R.string.deleteCancel),false);
        }
    };

    @Override
    public void onDatabaseResultR(DatabaseTask taskName, Task<DocumentSnapshot> task, int step) {
        try {
            switch (taskName) {
                case DB_DELETE_USER:
                    if (step == 1)
                        database.deleteAccount(task, step, auth.getEmail());
                    else
                        database.deleteAccount(task, step, null);
                    break;
                case DB_GET_USER_NAME:
                    if(step == 0)
                        database.getName(task, email);
                    else if(step == 1)
                        nameView.setText(database.getName(task));
                    break;
                case DB_GET_FAMILY_NAME:
                    if(step == 0)
                        database.getFamilyName(task, email);
                    else if(step == 1)
                        familyNameView.setText(database.getFamilyName(task));
                    break;

            }
        }catch(Exception e){
            Log.e("SettingsActivity",e.getMessage());
            functions.showMessage(e.getLocalizedMessage(),true);
        }
    }

    @Override
    public void onDatabaseResultW(DatabaseTask taskName, Task<Void> task, int step) {
        Log.d("SettingsActivity","Finished database deletion");
        if(taskName == DatabaseTask.AUTH_DELETE_USER)
            if(auth.deleteAccount(task))
                database.deleteAccount();
            else {
                functions.showMessage(getString(R.string.pleaseReSignIn),true);
                auth.signOut();
                Log.d("SettingsActivity", "User needs to re-sign in to delete account");
            }
        else{ //if(taskName == DatabaseTask.DB_DELETE_USER) {
            if(database.deleteAccount(task)) {
                Log.d("SettingsActivity","Database Successfully Deleted");
            }
        }
    }

    @Override
    public void onDatabaseResultA(DatabaseTask taskName, Task<AuthResult> task) {}
}
