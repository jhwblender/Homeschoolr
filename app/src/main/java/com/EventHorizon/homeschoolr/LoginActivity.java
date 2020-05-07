package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class LoginActivity extends AppCompatActivity {
    Google google;
    Database database;
    Button signInButton;
    ProgressBar loadingSymbol;

    @Override
    protected void onStart() {
        signInButton = findViewById(R.id.google_button);
        loadingSymbol = findViewById(R.id.loadingSymbol);

        super.onStart();
        if(google.isLoggedIn(this))
            startSettingsActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        google = new Google(this);
        database = new Database();
    }



    //Google sign in button clicked
    public void googleSignIn(View view){
        loadingSymbol.setVisibility(View.VISIBLE);
        google.signIn(this);
    }

    //What the google thing returns
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // result from google sign in request
        if (requestCode == 1){ //login successful
            if(google.handleSignInResult(data)) {
                database.doSomething(google);
                startSettingsActivity();
            }else{ //login failed

            }
        }
    }

    private void startSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void startRegisterActivity(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
