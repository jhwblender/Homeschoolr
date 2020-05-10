package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity implements DatabaseListener {
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
        if(google.isSignedIn(this))
            database.checkEmail(google.getEmail(),this);
    }

    @Override
    public void onBackPressed(){}

    //Google sign in button clicked
    public void googleSignIn(View view){
        loadingSymbol.setVisibility(View.VISIBLE);
        google.signIn(this);
    }

    //What the google thing returns
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // result from google sign in request
        if (requestCode == 1) {
            if (google.handleSignInResult(data)) { //login successful
                database.checkEmail(google.getEmail(), this); //email in database
            }else{
                //todo send message to user that there was a login problem
            }
        }
    }

    //Database returned something
    public void onDatabaseResult(DatabaseTask taskName, Task<DocumentSnapshot> task){
        if(taskName == DatabaseTask.CHECK_EMAIL)
            try {
                if (database.checkEmail(task))
                    startSettingsActivity();
                else {
                    startRegisterActivity(signInButton);
                }
            }catch(Exception e){
                Log.e("LoginActivity",e.toString());
                //todo send message to user that there was a database problem
            }
    }

    private void startSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void startRegisterActivity(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
