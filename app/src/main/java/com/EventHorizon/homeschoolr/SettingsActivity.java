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
    HashMap<String, Object> personalInfo;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        auth = new Auth(this);
        database = new Database(this);
        functions = new Functions(this);

        nameView = findViewById(R.id.nameText);
        emailView = findViewById(R.id.emailText);
        String nameViewText = nameView.getText()+": ";
        nameView.setText(nameViewText);
        String emailViewText = emailView.getText()+": ";
        emailView.setText(emailViewText);
    }

    //todo add delete account
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
            if (taskName == DatabaseTask.DB_DELETE_USER)
                if (step == 1)
                    database.deleteAccount(task, step, auth.getEmail());
                else
                    database.deleteAccount(task, step, null);
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
                Log.d("SettingsActivity","shouldn't arrive here I think");
            }
        }
    }

    @Override
    public void onDatabaseResultA(DatabaseTask taskName, Task<AuthResult> task) {}
}
