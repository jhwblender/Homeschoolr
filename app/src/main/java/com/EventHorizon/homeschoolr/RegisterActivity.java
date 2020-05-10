package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TableRow;

public class RegisterActivity extends AppCompatActivity {
    Google google;

    Button registerButton;
    EditText familyIDView;
    TableRow familyIDRow;
    Switch isParent;
    Switch joiningFamily;

    ProgressBar loadingSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = findViewById(R.id.registerButton);
        familyIDView = findViewById(R.id.familyID);
        familyIDRow = findViewById(R.id.familyIDRow);
        isParent = findViewById(R.id.amParentSwitch);
        amParentSwitched(isParent);
        joiningFamily = findViewById(R.id.joiningFamilySwitch);
        loadingSymbol = findViewById(R.id.loadingSymbol);

        google = new Google(this);
    }

    @Override
    public void onBackPressed(){
        google.signOut(this, LoginActivity.class);
    }

    public void amParentSwitched(View view){
        if(isParent.isChecked())
            isParent.setText(getString(R.string.textAmParent));
        else
            isParent.setText(getString(R.string.textAmLearner));
    }

    public void joiningFamilySwitched(View view){
        if(joiningFamily.isChecked())
            familyIDRow.setVisibility(View.VISIBLE);
        else
            familyIDRow.setVisibility(View.INVISIBLE);
    }

    public void registerButton(View view){
        loadingSymbol.setVisibility(View.VISIBLE);

    }
}
