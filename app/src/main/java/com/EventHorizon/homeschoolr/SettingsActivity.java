package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

public class SettingsActivity extends AppCompatActivity implements DatabaseListener{
    Auth auth;
    Functions functions;
    TextView emailView;
    TextView nameView;
    String email;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = new Auth(this);
        functions = new Functions(this);

        nameView = findViewById(R.id.email);
        nameView.setText("Email: "+email);
        emailView = findViewById(R.id.email);
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
                //todo delete database account
                auth.deleteAccount(context);
            }else
                functions.showMessage(getString(R.string.deleteCancel),false);
        }
    };

    @Override
    public void onDatabaseResultR(DatabaseTask taskName, Task<DocumentSnapshot> task) {}

    @Override
    public void onDatabaseResultW(DatabaseTask taskName, Task<Void> task) {
        if(taskName == DatabaseTask.AUTH_DELETE_USER)
            auth.deleteAccount(task, this);
    }

    @Override
    public void onDatabaseResultA(DatabaseTask taskName, Task<AuthResult> task) {}
}
