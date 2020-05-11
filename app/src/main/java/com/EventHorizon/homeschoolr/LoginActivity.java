package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity implements DatabaseListener{
    Google google;
    Database database;
    Button signInButton;
    ProgressBar loadingSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        google = new Google(this);
        database = new Database();
    }

    @Override
    protected void onStart() {
        signInButton = findViewById(R.id.signInButton);
        loadingSymbol = findViewById(R.id.loadingSymbol);

        super.onStart();
        Functions.loadingView(true,this);
        if(google.isSignedIn(this))
            database.checkEmail(google.getEmail(),this);
        else
            Functions.loadingView(false,this);
    }

    @Override
    public void onBackPressed(){}

    //Google sign in button clicked
    public void googleSignIn(View view){
        Functions.loadingView(true,this);
        google.signIn(this);
    }

    //What the google thing returns
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Functions.loadingView(false,this);
        super.onActivityResult(requestCode, resultCode, data);

        // result from google sign in request
        if (requestCode == 1) {
            if (google.handleSignInResult(data, this)) { //login successful
                database.checkEmail(google.getEmail(), this); //email in database
            }
        }
    }

    //Database returned something
    public void onDatabaseResultW(DatabaseTask taskName, Task<Void> task){}
    public void onDatabaseResultR(DatabaseTask taskName, Task<DocumentSnapshot> task){
        Functions.loadingView(false,this);
        if(taskName == DatabaseTask.CHECK_EMAIL_EXISTS)
            if (database.checkEmail(task, this))
                Functions.goToActivity(SettingsActivity.class, this);
            else {
                Functions.goToActivity(RegisterActivity.class, this);
            }
    }

    public void registerButtonClicked(View view){
        Functions.goToActivity(RegisterActivity.class, this);
    }
}
