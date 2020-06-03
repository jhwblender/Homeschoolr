package com.EventHorizon.homeschoolr;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.HashMap;

public class Person
{
    private String name;
    private String email;
    private String familyName;
    private boolean isParent;

    public Person(String email, boolean isParent, String name, String familyName){
        this.email = email;
        this.isParent = isParent;
        this.name = name;
        this.familyName = familyName;
        upload();
    }

    public String getEmail(){
        return email;
    }
    public String getName(){
        return  name;
    }
    public boolean getIsParent(){
        return isParent;
    }
    public String getFamilyName(){
        return familyName;
    }

    public void deleteAccount(String email, final Context context){
        unsave(context);
        DocumentReference ref = FirebaseFirestore.getInstance().document("data/user");
        HashMap<String, Object> data = new HashMap<>();
        data.put(Functions.formatEmail2(email), FieldValue.delete());
        ref.update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ((AuthListener)context).authResult(TaskName.DB_DELETE_USER_SUCCESSFUL);
                }else{
                    Functions.showMessage(task.getException().getMessage(), context);
                }
            }
        });
    }

    public static Person save(Context context, String userFile, String email){
        SharedPreferences sharedPreferences = context.getSharedPreferences("User",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(email, userFile).apply();
        Gson gson = new Gson();
        return gson.fromJson(userFile, Person.class);
    }
    public void save(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("User",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String userFile = gson.toJson(this);
        editor.putString(email, userFile).apply();
        upload();
    }
    public void unsave(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("User",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
    }
    public static Person load(Context context, String email){
        SharedPreferences sharedPreferences = context.getSharedPreferences("User",Context.MODE_PRIVATE);
        String allData = sharedPreferences.getString(email, null);
        Gson gson = new Gson();
        return gson.fromJson(allData, Person.class);
    }
    public void upload(){
        Gson gson = new Gson();
        String thisUser = gson.toJson(this);
        DocumentReference ref = FirebaseFirestore.getInstance().document("data/user");
        HashMap<String, Object> data = new HashMap<>();
        data.put(Functions.formatEmail2(email),thisUser);
        ref.update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful())
                    Log.e("Person",task.getException().getMessage());
            }
        });
    }
}
