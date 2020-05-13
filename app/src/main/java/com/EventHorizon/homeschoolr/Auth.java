package com.EventHorizon.homeschoolr;

import android.app.Activity;
import android.content.Context;
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

    public boolean isSignedIn(){
        if(user == null)
            auth.getCurrentUser();
        return user != null;
    }

    public String getEmail(){
        return user.getEmail();
    }

    public String getID(){
        return user.getUid();
    }

    public void createUser(final String email, String password){
        final DatabaseListener listener = (DatabaseListener) context;
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        listener.onDatabaseResultA(DatabaseTask.AUTH_CREATE_USER,task);
                    }
                });
    }
    public boolean createUser(Task<AuthResult> task){
        if(task.isSuccessful()){
            Log.d("Auth","User was created successfully");
            functions.showMessage("Welcome to Homeschoolr",true);
            return true;
        }else{
            Log.w("Auth", "Create user failed: "+task.getException().getLocalizedMessage());
            functions.showMessage(task.getException().getLocalizedMessage(),false);
            return false;
        }
    }

    public void signIn(String email, String password){
        final DatabaseListener listener = (DatabaseListener) context;
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        listener.onDatabaseResultA(DatabaseTask.AUTH_LOGIN, task);
                    }
                });
    }
    public boolean signIn(Task<AuthResult> task){
        if(task.isSuccessful()){
            Log.w("Auth",context.getString(R.string.signInSuccessful));
            functions.showMessage(context.getString(R.string.signInSuccessful), false);
            return true;
        }else {
            Log.w("Auth",task.getException().getLocalizedMessage());
            functions.showMessage(task.getException().getLocalizedMessage(), false);
            return false;
        }
    }

    public void signOut(){
        auth.signOut();
    }

    public void deleteAccount(Context context){
        final DatabaseListener listener = (DatabaseListener) context;
        String id = user.getUid();

        //delete from authentication
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onDatabaseResultW(DatabaseTask.AUTH_DELETE_USER,task);
            }
        });
    }
    public boolean deleteAccount(Task task, Context context){
        if(task.isSuccessful()){
            Log.w("Auth","Deleting Auth Account Successful");
            functions.showMessage(context.getString(R.string.acctDelSuccessful),true);
            functions.goToActivity(LoginActivity.class);
            return true;
        }else {
            Log.w("Auth","Deleting Auth Account Failed: " + task.getException().getLocalizedMessage());
            functions.showMessage(task.getException().getLocalizedMessage(),true);
            return false;
        }
    }

    public void resetPassword(String email){
        final DatabaseListener listener = (DatabaseListener) context;
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onDatabaseResultW(DatabaseTask.AUTH_RESET_PASSWORD, task);
            }
        });
    }
    public boolean resetPassword(Task<Void> task){
        if(task.isSuccessful()){
            Log.w("Auth",context.getString(R.string.passwordResetGood));
            functions.showMessage(context.getString(R.string.passwordResetGood),true);
            return true;
        }else {
            Log.w("Auth",task.getException().getLocalizedMessage());
            functions.showMessage(task.getException().getLocalizedMessage(),true);
            return false;
        }
    }
}
