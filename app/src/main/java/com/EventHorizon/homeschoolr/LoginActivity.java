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
    Functions functions;
    Button signInButton;
    ProgressBar loadingSymbol;
    EditText usernameView;
    EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = new Auth(this);
        database = new Database(this);
        functions = new Functions(this);
    }

    @Override
    protected void onStart() {
        signInButton = findViewById(R.id.signInButton);
        loadingSymbol = findViewById(R.id.loadingSymbol);
        usernameView = findViewById(R.id.email);
        passwordView = findViewById(R.id.password);

        super.onStart();
        if (auth.isSignedIn()) {
            functions.loadingView(true);
            database.getUserID();
        }
    }

    @Override
    public void onBackPressed(){}

    //Google sign in button clicked
    public void signIn(View view){
        String email = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        if(!functions.checkEmail(email));
        else if(password.length() == 0)
            functions.showMessage(getString(R.string.noPasswordEntered),true);
        else
            auth.signIn(email, password);
    }

    //Database returned something
    public void onDatabaseResultW(DatabaseTask taskName, Task<Void> task){
        functions.loadingView(false);
        if(taskName == DatabaseTask.AUTH_RESET_PASSWORD)
            auth.resetPassword(task);
    }
    public void onDatabaseResultR(DatabaseTask taskName, Task<DocumentSnapshot> task){
        functions.loadingView(false);
        try {
            switch(taskName){
                case DB_GET_USER_ID:
                    if(database.getUserID(task, getEmail()) != null)
                        functions.goToActivity(SettingsActivity.class);
                    else
                        functions.goToActivity(RegisterMoreActivity.class);
                    break;
            }
        }catch(Exception e){
            functions.showMessage(e.getLocalizedMessage(), true);
        }
    }
    public void onDatabaseResultA(DatabaseTask taskName, Task<AuthResult> task){
        functions.loadingView(false);
        if(taskName == DatabaseTask.AUTH_LOGIN)
            if(auth.signIn(task)) {
                functions.loadingView(true);
                database.getUserID();
            }
    }

    public void registerButtonClicked(View view){
        functions.goToActivity(RegisterActivity.class);
    }
    public void resetPasswordButtonClicked(View view){
        if(functions.checkEmail(getEmail())) {
            functions.loadingView(true);
            auth.resetPassword(getEmail());
        }
    }

    private String getEmail(){
        if(auth.isSignedIn())
            return auth.getEmail();
        else
            return usernameView.getText().toString();
    }
}
