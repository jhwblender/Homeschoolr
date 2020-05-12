package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

public class RegisterActivity extends AppCompatActivity implements DatabaseListener{
    Auth auth;
    EditText emailView;
    EditText passwordView;
    EditText passwordVerifyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = new Auth();
        emailView = findViewById(R.id.email);
        passwordView = findViewById(R.id.password);
        passwordVerifyView = findViewById(R.id.passwordVerify);
    }

    public void register(View view){
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String passwordVerify = passwordVerifyView.getText().toString();
        if(!Functions.checkEmail(email,this));
        else if(!Functions.checkPassword(passwordView.getText().toString(),this));
        else if(!password.equals(passwordVerify)) {
            Functions.showMessage(getString(R.string.passwordNotMatch),this,true);
            return;
        }else {
            Functions.loadingView(true, this);
            auth.createUser(email, password, this);
        }
    }

    @Override
    public void onDatabaseResultR(DatabaseTask taskName, Task<DocumentSnapshot> task) {}

    @Override
    public void onDatabaseResultW(DatabaseTask taskName, Task<Void> task) {}

    @Override
    public void onDatabaseResultA(DatabaseTask taskName, Task<AuthResult> task) {
        Functions.loadingView(false, this);
        if(taskName == DatabaseTask.AUTH_CREATE_USER)
            if(auth.createUser(task, this))
                Functions.goToActivity(RegisterMoreActivity.class,this);
    }
}
