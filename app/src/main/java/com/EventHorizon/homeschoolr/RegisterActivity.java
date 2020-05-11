package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TableRow;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class RegisterActivity extends AppCompatActivity implements DatabaseListener{
    Google google;
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
        setContentView(R.layout.activity_register);

        registerButton = findViewById(R.id.registerButton);
        familyIDView = findViewById(R.id.familyID);
        nameView = findViewById(R.id.nameView);
        isParent = findViewById(R.id.amParentSwitch);
        amParentSwitched(isParent);
        joiningFamily = findViewById(R.id.joiningFamilySwitch);
        loadingSymbol = findViewById(R.id.loadingSymbol);

        google = new Google(this);
        database = new Database();
    }

    @Override
    public void onBackPressed(){
        google.signOut(this, LoginActivity.class);
    }

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
        Functions.loadingView(true,this);

        //check if already signed in and log in if not
        if(google.isSignedIn(this))
            database.checkEmail(google.getEmail(), this);
        else{
            Functions.loadingView(false,this);
            google.signIn(this);
        }

        Functions.loadingView(false, this);
    }

    private void registerUser(){
        //Check for invalid syntax on familyID
        if(familyIDView.getText().length() < 1) {
            Functions.showMessage(getString(R.string.familyIDError), this, true);
            return;
        }

        database.checkFamilyID(familyIDView.getText().toString(), this);
    }

    @Override
    public void onDatabaseResultR(DatabaseTask taskName, Task<DocumentSnapshot> task) {
        if(taskName == DatabaseTask.CHECK_EMAIL_EXISTS)
            if(database.checkEmail(task, this)) { //If the user is already registered: log them in
                Functions.showMessage(getString(R.string.emailExists), this, true);
                Functions.goToActivity(SettingsActivity.class, this);
            }else{
                registerUser();
            }

        //Result checking if a family ID exists
        if(taskName == DatabaseTask.CHECK_FAMILY_ID_EXISTS){
            boolean idExists = database.checkFamilyID(task, this);
            boolean toJoinFamily = joiningFamily.isChecked();
            boolean parent = isParent.isChecked();
            String name = nameView.getText().toString();
            String familyID = familyIDView.getText().toString();

            if(idExists && toJoinFamily);
            else if(idExists && !toJoinFamily) //ID exists and not joining a family, show error
                Functions.showMessage(getString(R.string.familyIDExists),this,true);
            else if(!idExists && toJoinFamily);
            else if(!idExists && !toJoinFamily)
                database.addUser(google.getEmail(),name, familyID, parent, this);
        }
        Functions.loadingView(false,this); //turn off spinner
    }//end onDatabaseResult

    @Override
    public void onDatabaseResultW(DatabaseTask taskName, Task<Void> task){
        if(taskName == DatabaseTask.CHECK_ADD_USER){
            if(database.addUser(task, this))
                Functions.goToActivity(LoginActivity.class, this);
        }
    }
    //What the google thing returns
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // result from google sign in request
        if (requestCode == 1) {
            if (google.handleSignInResult(data, this)) { //login successful
                database.checkEmail(google.getEmail(), this); //email in database
            }
        }
    }
}
