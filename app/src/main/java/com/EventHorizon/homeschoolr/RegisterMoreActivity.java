package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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
    Functions functions;

    ProgressBar loadingSymbol;
    Button registerButton;
    EditText familyIDView;
    Switch isParentView;
    Switch joiningFamilyView;
    EditText nameView;

    String name;
    String familyName;
    boolean isParent;
    boolean joiningFamily;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_more);

        registerButton = findViewById(R.id.signInButton);
        familyIDView = findViewById(R.id.password);
        nameView = findViewById(R.id.email);
        isParentView = findViewById(R.id.amParentSwitch);
        amParentSwitched(isParentView);
        joiningFamilyView = findViewById(R.id.joiningFamilySwitch);
        loadingSymbol = findViewById(R.id.loadingSymbol);

        auth = new Auth(this);
        database = new Database(this);
        functions = new Functions(this);
    }

    @Override
    public void onBackPressed(){
        auth.signOut();
        functions.goToActivity(LoginActivity.class);
    }

    //changing text to say if they are a parent or a child
    public void amParentSwitched(View view){
        if(isParentView.isChecked()) {
            isParentView.setText(getString(R.string.amParent));
            if(joiningFamilyView != null) //have to add this for some weird reason
                joiningFamilyView.setEnabled(true);
        }
        else {
            isParentView.setText(getString(R.string.amLearner));
            joiningFamilyView.setChecked(true);
            joiningFamilyView.setEnabled(false);
        }
    }

    //Check the user has put information in
    public void registerButton(View view){
        if(familyIDView.getText().length() < 1) {
            functions.showMessage(getString(R.string.familyIDError), true);
            return;
        }
        if(nameView.getText().length() < 1){
            functions.showMessage(getString(R.string.nameError),true);
            return;
        }
        registerUser();
    }

    private void registerUser(){
        name = nameView.getText().toString();
        familyName = familyIDView.getText().toString();
        isParent = isParentView.isChecked();
        joiningFamily = joiningFamilyView.isChecked();

        functions.loadingView(true);
        database.getFamilyID();
    }
    private void registerUser(String familyID){
        //Log.d("RegisterMoreActivity",familyID);
        //functions.showMessage(familyID, this, true);
        if(!joiningFamily) { //not joining family
            if (familyID == null)
                database.createUserAndFamily(auth.getEmail(), name, isParent, familyName);
            else {
                functions.showMessage(getString(R.string.familyIDExists), true);
                return;
            }
        }else{ //joining family

        }
    }

    @Override
    public void onDatabaseResultR(DatabaseTask taskName, Task<DocumentSnapshot> task) {
        functions.loadingView(false);
        try {
            switch (taskName) {
                case DB_GET_FAMILY_ID:
                    registerUser(database.getFamilyID(task, familyIDView.getText().toString()));
                    break;
            }
        }catch(Exception e){
            functions.showMessage(e.getLocalizedMessage(), true);
        }
    }//end onDatabaseResult

    @Override
    public void onDatabaseResultW(DatabaseTask taskName, Task<Void> task){
        functions.loadingView(false);
        switch (taskName){
            case DB_CREATE_USER_AND_FAMILY:
                if(database.createUserAndFamily(task))
                    functions.goToActivity(SettingsActivity.class);
        }
    }

    @Override
    public void onDatabaseResultA(DatabaseTask taskName, Task<AuthResult> task) {}
}