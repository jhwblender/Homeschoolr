package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity implements DatabaseListener{
    Auth auth;
    Database database;
    Button signInButton;
    ProgressBar loadingSymbol;
    EditText usernameView;
    EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = new Auth();
        database = new Database(this);
    }

    @Override
    protected void onStart() {
        signInButton = findViewById(R.id.signInButton);
        loadingSymbol = findViewById(R.id.loadingSymbol);
        usernameView = findViewById(R.id.email);
        passwordView = findViewById(R.id.password);

        super.onStart();
        if (auth.isSignedIn()) {
            Functions.loadingView(true,this);
            database.getUserID();
        }
    }

    @Override
    public void onBackPressed(){}

    //Google sign in button clicked
    public void signIn(View view){
        String email = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        if(!Functions.checkEmail(email,this));
        else if(password.length() == 0)
            Functions.showMessage(getString(R.string.noPasswordEntered),this,true);
        else
            auth.signIn(email, password,this);
    }

    //Database returned something
    public void onDatabaseResultW(DatabaseTask taskName, Task<Void> task){
        Functions.loadingView(false, this);
        if(taskName == DatabaseTask.AUTH_RESET_PASSWORD)
            auth.resetPassword(task, this);
    }
    public void onDatabaseResultR(DatabaseTask taskName, Task<DocumentSnapshot> task){
        Functions.loadingView(false, this);
        try {
            switch(taskName){
                case DB_GET_USER_ID:
                    if(database.getUserID(task, getEmail()) != null)
                        Functions.goToActivity(SettingsActivity.class, this);
                    else
                        Functions.goToActivity(RegisterMoreActivity.class, this);
                    break;
            }
        }catch(Exception e){
            Functions.showMessage(e.getLocalizedMessage(), this, true);
        }
    }
    public void onDatabaseResultA(DatabaseTask taskName, Task<AuthResult> task){
        Functions.loadingView(false, this);
        if(taskName == DatabaseTask.AUTH_LOGIN)
            if(auth.signIn(task, this)) {
                Functions.loadingView(true, this);
                database.getUserID();
            }
    }

    public void registerButtonClicked(View view){
        Functions.goToActivity(RegisterActivity.class, this);
    }
    public void resetPasswordButtonClicked(View view){
        if(Functions.checkEmail(getEmail(),this)) {
            Functions.loadingView(true, this);
            auth.resetPassword(getEmail(),this);
        }
    }

    private String getEmail(){
        if(auth.isSignedIn())
            return auth.getEmail();
        else
            return usernameView.getText().toString();
    }
}
