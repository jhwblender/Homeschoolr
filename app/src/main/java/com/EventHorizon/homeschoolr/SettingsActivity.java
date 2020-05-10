package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    Google google;
    TextView emailView;
    TextView nameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        google = new Google(this);

        nameView = findViewById(R.id.name);
        nameView.setText("Email: "+google.getEmail());
        emailView = findViewById(R.id.email);
        emailView.setText("Name: "+google.getGivenName());
    }

    //Logout button clicked
    public void logout(View view){
        google.signOut(this, LoginActivity.class);
    }
}
