package com.EventHorizon.homeschoolr;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database{
    private String userPathPrefix = "users/";
    private String familyIDPathPrefix = "test/";
    private String familyMembersPathSuffix = "/members";
    //private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/");
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    WriteBatch writeBatch = database.batch();
    private CollectionReference getC(String prefix, String middle, String suffix){
        return database.collection(prefix+middle+suffix);
    }
    private DocumentReference getD(String prefix, String middle, String suffix){
        return database.document(prefix+middle+suffix);
    }
    private DocumentReference getD(String prefix, String middle){
        return database.document(prefix+middle);
    }

//    void doSomething(Google google){
//        Map<String, Object> dataToSave = new HashMap<String, Object>();
//        dataToSave.put("email_key",google.getEmail());
//        dataToSave.put("givenName_key",google.getGivenName());
//        mDocRef = FirebaseFirestore.getInstance().document("users/"+google.getEmail());
//        mDocRef.set(dataToSave).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful())
//                    Log.d("LoginActivity","Document saved successfully!");
//                else
//                    Log.w("LoginActivity",task.getException());
//            }
//        });
//    }

    //Asks database to check for email
    public void checkEmail(String email, Context context){
        final DatabaseListener listener = (DatabaseListener) context;
        getD(userPathPrefix,email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                listener.onDatabaseResultR(DatabaseTask.CHECK_EMAIL_EXISTS, task);
            }
        });
    }
    //Checks email after result
    public boolean checkEmail(Task<DocumentSnapshot> task, Context context){
        if(task.isSuccessful()) {
            return task.getResult().exists();
        }
        else {
            Log.e("Database",task.getException().toString());
            Functions.showMessage(task.getException().toString(), context , true);
            return false;
        }
    }

    //Asks database to check for familyID
    public void checkFamilyID(String id, Context context){
        final DatabaseListener listener = (DatabaseListener) context;
        getD(familyIDPathPrefix,id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                listener.onDatabaseResultR(DatabaseTask.CHECK_FAMILY_ID_EXISTS, task);
            }
        });
    }
    //Checks familyID after result
    public boolean checkFamilyID(Task<DocumentSnapshot> task, Context context){
        if(task.isSuccessful()) {
            return task.getResult().exists();
        }
        else {
            Log.e("Database",task.getException().toString());
            Functions.showMessage(task.getException().toString(), context , true);
            return false;
        }
    }
    //Adds user needing a new FamilyID
    public void addUser(String email, String name, String familyId, boolean isParent, Context context){
        //saving family ID first
        Map<String, Object> familyIDSave = new HashMap<String, Object>();
        List<String> emails = new ArrayList<>();
        emails.add(email);
        familyIDSave.put("members",emails);
        Map<String, Object> userSave = new HashMap<String, Object>();
        userSave.put("familyID",familyId);
        userSave.put("isParent",isParent);
        userSave.put("name",name);
        writeBatch.set(getD(familyIDPathPrefix,familyId),familyIDSave);
        writeBatch.set(getD(userPathPrefix,email),userSave);
        final DatabaseListener listener = (DatabaseListener) context;
        writeBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 listener.onDatabaseResultW(DatabaseTask.CHECK_ADD_USER, task);
             }
         }
        );
    }
    public boolean addUser(Task<Void> task, Context context){
        if(task.isSuccessful()) {
            Log.d("LoginActivity", "User Successfully Made!");
            Functions.showMessage("Sign up successful!",context, true);
            return true;
        }
        else {
            Log.w("LoginActivity", task.getException());
            Functions.showMessage(task.getException().toString(), context, true);
            return false;
        }
    }
}
