package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

public class RegisterMoreActivity extends AppCompatActivity implements DatabaseListener{
    Auth auth;
    Database database;

    Button registerButton;
    EditText familyIDView;
    Switch isParent;
    Switch joiningFamily;
    EditText nameView;

    ProgressBar loadingSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_more);

        registerButton = findViewById(R.id.signInButton);
        familyIDView = findViewById(R.id.password);
        nameView = findViewById(R.id.email);
        isParent = findViewById(R.id.amParentSwitch);
        amParentSwitched(isParent);
        joiningFamily = findViewById(R.id.joiningFamilySwitch);
        loadingSymbol = findViewById(R.id.loadingSymbol);

        auth = new Auth();
        database = new Database();
    }

    @Override
    public void onBackPressed(){}

    //changing text to say if they are a parent or a child
    public void amParentSwitched(View view){
        if(isParent.isChecked()) {
            isParent.setText(getString(R.string.amParent));
            if(joiningFamily != null) //have to add this for some weird reason
                joiningFamily.setEnabled(true);
        }
        else {
            isParent.setText(getString(R.string.amLearner));
            joiningFamily.setChecked(true);
            joiningFamily.setEnabled(false);
        }
    }

    public void registerButton(View view){
    }

    private void registerUser(){
        //Check for invalid syntax on familyID
        if(familyIDView.getText().length() < 1) {
            Functions.showMessage(getString(R.string.familyIDError), this, true);
            return;
        }

        if(nameView.getText().length() < 1){
            Functions.showMessage(getString(R.string.nameError),this,true);
            return;
        }
    }

    @Override
    public void onDatabaseResultR(DatabaseTask taskName, Task<DocumentSnapshot> task) {

    }//end onDatabaseResult

    @Override
    public void onDatabaseResultW(DatabaseTask taskName, Task<Void> task){

    }

    @Override
    public void onDatabaseResultA(DatabaseTask taskName, Task<AuthResult> task) {

    }
}
