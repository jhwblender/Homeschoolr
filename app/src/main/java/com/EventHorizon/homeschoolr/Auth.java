package com.EventHorizon.homeschoolr;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Auth {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Activity context;
    private Functions functions;

    public Auth(Activity context){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        this.context = context;
        functions = new Functions(context);
    }

    private void handleResult(Task task, TaskName resultName){
        functions.loadingView(false);
        if(task.isSuccessful()) {
            Log.d("Auth", "Task "+resultName+" successful");
            ((AuthListener)context).authResult(resultName);
        }else{
            functions.showMessage(task.getException().getLocalizedMessage(),true);
            Log.e("Auth",task.getException().getMessage());
        }
    }

    public boolean isSignedIn(){
        if(user == null)
            auth.getCurrentUser();
        return user != null;
    }

    public String getEmail(){
        if(isSignedIn())
            return user.getEmail();
        else
            return null;
    }

    public void createUser(final String email, String password){
        final AuthListener listener = (AuthListener) context;
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        handleResult(task, TaskName.AUTH_CREATE_USER_SUCCESSFUL);
                    }
                });
    }

    public void signIn(String email, String password){
        final AuthListener listener = (AuthListener) context;
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        handleResult(task, TaskName.AUTH_SIGN_IN_SUCCESSFUL);
                    }
                });
    }

    public void signOut(){
        auth.signOut();
        functions.goToActivity(LoginActivity.class);
    }

    public void deleteAccount(){
        Log.d("Auth","Deleting Auth account");
        final AuthListener listener = (AuthListener) context;
        //delete from authentication
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                handleResult(task, TaskName.AUTH_DELETE_ACCOUNT_SUCCESSFUL);
            }
        });
    }

    public void resetPassword(String email){
        final AuthListener listener = (AuthListener) context;
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                handleResult(task, TaskName.AUTH_RESET_PASSWORD_SENDING);
            }
        });
    }
}
