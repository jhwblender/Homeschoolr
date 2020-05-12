package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
        database = new Database();
    }

    @Override
    protected void onStart() {
        signInButton = findViewById(R.id.signInButton);
        loadingSymbol = findViewById(R.id.loadingSymbol);
        usernameView = findViewById(R.id.email);
        passwordView = findViewById(R.id.password);

        super.onStart();
        if (auth.isSignedIn()) {
            Functions.goToActivity(SettingsActivity.class, this);
        }
    }

    @Override
    public void onBackPressed(){}

    //Google sign in button clicked
    public void signIn(View view){
        String email = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        if(Functions.checkEmail(getEmail(),this));
        else if(!password.equals("")) {
            Functions.loadingView(true, this);
            auth.signIn(email, password, this);
        }else
            Functions.showMessage(getString(R.string.noPasswordEntered), this, true);
    }

    //Database returned something
    public void onDatabaseResultW(DatabaseTask taskName, Task<Void> task){
        Functions.loadingView(false, this);
        if(taskName == DatabaseTask.RESET_PASSWORD)
            auth.resetPassword(task, this);
    }
    public void onDatabaseResultR(DatabaseTask taskName, Task<DocumentSnapshot> task){}
    public void onDatabaseResultA(DatabaseTask taskName, Task<AuthResult> task){
        Functions.loadingView(false, this);
        if(taskName == DatabaseTask.AUTH_LOGIN)
            if(auth.signIn(task, this))
                Functions.goToActivity(SettingsActivity.class, this);
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

    private String getEmail(){return usernameView.getText().toString();}
}
