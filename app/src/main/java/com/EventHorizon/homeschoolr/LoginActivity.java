package com.EventHorizon.homeschoolr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity implements AuthListener{
    Auth auth;
    Functions functions;
    Button signInButton;
    ProgressBar loadingSymbol;
    EditText usernameView;
    EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onStart() {
        super.onStart();

        auth = new Auth(this);
        functions = new Functions(this);
        signInButton = findViewById(R.id.signInButton);
        loadingSymbol = findViewById(R.id.loadingSymbol);
        usernameView = findViewById(R.id.nameText);
        passwordView = findViewById(R.id.password);

        if (auth.isSignedIn() && auth.getEmail() != null) {
            functions.loadingView(true);
            Log.d("LoginActivity","Already signed in to: "+auth.getEmail());
            authResult(TaskName.AUTH_SIGN_IN_SUCCESSFUL);
        }
    }

    @Override
    public void onBackPressed(){}

    //Google sign in button clicked
    public void signIn(View view){
        String email = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        if(!functions.checkEmail(email));
        else if(password.length() == 0)
            functions.showMessage(getString(R.string.noPasswordEntered));
        else
            auth.signIn(email, password);
    }

    public void registerButtonClicked(View view){
        functions.goToActivity(RegisterActivity.class);
    }
    public void resetPasswordButtonClicked(View view){
        if(functions.checkEmail(getEmail())) {
            functions.loadingView(true);
            auth.resetPassword(getEmail());
        }
    }

    private String getEmail(){
        if(auth.isSignedIn())
            return auth.getEmail();
        else
            return usernameView.getText().toString();
    }

    private void checkIfEmailInDatabase(final String email){
        Log.d("LoginActivity","Email2: "+email);
        final Context context = this;
        DocumentReference ref = FirebaseFirestore.getInstance().document("data/user");
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snap = task.getResult();
                    String userData = (String)snap.get(functions.formatEmail(email));
                    if(userData != null){
                        //todo save user to SharedPreferences
                        Person person = Person.save(getApplicationContext(), userData, email);
                        Family.download(getApplicationContext(), person.getFamilyName(), (AuthListener)context);
                    }else{
                        functions.goToActivity(RegisterMoreActivity.class);
                    }
                }else{ //Database Read problem
                    functions.showMessage(task.getException().getMessage());
                }
            }
        });
    }

    @Override
    public void authResult(TaskName result) {
        switch(result){
            case AUTH_RESET_PASSWORD_SENDING:
                functions.showMessage(getString(R.string.passwordResetGood));
                break;
            case AUTH_SIGN_IN_SUCCESSFUL:
                checkIfEmailInDatabase(getEmail());
                break;
            case DB_FAMILY_LOADED_SUCCESSFULLY:
                functions.goToActivity(SettingsActivity.class);
                break;
        }
    }
}
