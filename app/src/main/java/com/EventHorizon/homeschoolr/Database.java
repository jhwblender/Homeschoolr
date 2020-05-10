package com.EventHorizon.homeschoolr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Database{
    //private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/");
    private CollectionReference users = FirebaseFirestore.getInstance().collection("users");

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

    //Asks database for email
    public void checkEmail(String email, Context context){
        final DatabaseListener listener = (DatabaseListener) context;
        users.document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                listener.onDatabaseResult(DatabaseTask.CHECK_EMAIL, task);
            }
        });
    }
    //Checks email after result
    public boolean checkEmail(Task<DocumentSnapshot> task) throws Exception {
        if(task.isSuccessful()) {
            return task.getResult().exists();
        }
        else {
            throw new Exception(task.getException().toString());
        }
    }
}
