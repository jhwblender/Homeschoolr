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
    Functions functions;
    EditText emailView;
    EditText passwordView;
    EditText passwordVerifyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = new Auth(this);
        functions = new Functions(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        emailView = findViewById(R.id.email);
        passwordView = findViewById(R.id.password);
        passwordVerifyView = findViewById(R.id.passwordVerify);
    }

    public void register(View view){
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String passwordVerify = passwordVerifyView.getText().toString();
        if(!functions.checkEmail(email));
        else if(!functions.checkPassword(passwordView.getText().toString()));
        else if(!password.equals(passwordVerify)) {
            functions.showMessage(getString(R.string.passwordNotMatch),true);
            return;
        }else {
            functions.loadingView(true);
            auth.createUser(email, password);
        }
    }

    @Override
    public void onDatabaseResultR(DatabaseTask taskName, Task<DocumentSnapshot> task) {}

    @Override
    public void onDatabaseResultW(DatabaseTask taskName, Task<Void> task) {}

    @Override
    public void onDatabaseResultA(DatabaseTask taskName, Task<AuthResult> task) {
        functions.loadingView(false);
        if(taskName == DatabaseTask.AUTH_CREATE_USER)
            if(auth.createUser(task))
                functions.goToActivity(RegisterMoreActivity.class);
    }
}
