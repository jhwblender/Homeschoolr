package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    Auth auth;
    TextView emailView;
    TextView nameView;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = new Auth();

        nameView = findViewById(R.id.email);
        nameView.setText("Email: "+email);
        emailView = findViewById(R.id.email);
    }

    //todo add delete account
    //todo add name pulled from database

    //Logout button clicked
    public void logout(View view){
        auth.signOut();
        Functions.goToActivity(LoginActivity.class,this);
    }

    //Delete Account button clicked
    public void deleteAccount(View view){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.deleteConfirmation))
                .setPositiveButton(getString(R.string.yes),dialogClickListener)
                .setNegativeButton(getString(R.string.no),dialogClickListener).show();
    }

    //Verification for account deletion
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which==DialogInterface.BUTTON_POSITIVE)
                //todo delete database account
                auth.deleteAccount(getApplicationContext());
            else
                Functions.showMessage(getString(R.string.deleteCancel),getApplicationContext(),false);
        }
    };
}
