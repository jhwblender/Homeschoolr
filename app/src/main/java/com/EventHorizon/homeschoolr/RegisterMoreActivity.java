package com.EventHorizon.homeschoolr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterMoreActivity extends AppCompatActivity{
    Auth auth;
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
        nameView = findViewById(R.id.nameText);
        isParentView = findViewById(R.id.amParentSwitch);
        amParentSwitched(isParentView);
        joiningFamilyView = findViewById(R.id.joiningFamilySwitch);
        loadingSymbol = findViewById(R.id.loadingSymbol);

        auth = new Auth(this);
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
            functions.showMessage(getString(R.string.familyNameError), true);
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
        checkFamilyName(familyName);
    }

    private void checkFamilyName(final String familyName){
        DocumentReference ref = FirebaseFirestore.getInstance().document("data/family");
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                functions.loadingView(false);
                if(task.isSuccessful()){
                    String result = (String)task.getResult().get(familyName);
                    if(result == null){ //familyName does not exist
                        if(!joiningFamily)
                            createUser();
                        else{
                            functions.showMessage(getString(R.string.familyNameExpected));
                        }
                    }else{ //familyName exists
                        if(!joiningFamily)
                            functions.showMessage(getString(R.string.familyNameExists));
                        else{ //todo check invites

                        }
                    }
                }else{
                    functions.showMessage(task.getException().getMessage());
                }
            }
        });
    }

    private void createUser(){
        //todo create user
        Person user = new Person(auth.getEmail(), isParent, name, familyName);
        Family fam = new Family(familyName, auth.getEmail());
    }
}
