package com.EventHorizon.homeschoolr;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class Family {
    private String familyName;
    private ArrayList<String> memberEmails = new ArrayList<>();
    private ArrayList<String> inviteEmails = new ArrayList<>();

    public Family(String familyName, String email, Context context){
        memberEmails.add(email);
        this.familyName = familyName;
        save(context);
    }

    public ArrayList<String> getInviteEmailList(){
        return inviteEmails;
    }
    public ArrayList<String> getMemberEmails(){return memberEmails;}

    public void downloadMembers(Context context, TaskListener listener){
        for(int i = 0; i < memberEmails.size(); i++){
            Person.download(context, memberEmails.get(i),listener);
        }
    }
    public int getNumMembers(){
        return memberEmails.size();
    }
    public Person getMember(Context context, String email){
        return Person.load(context, email);
    }
    public ArrayList<Person> getMembers(Context context){
        ArrayList<Person> users = new ArrayList<>();
        for(int i = 0; i < memberEmails.size(); i++){
            users.add(Person.load(context, memberEmails.get(i)));
        }
        return users;
    }
    public Person getMemberByName(Context context, String name){
        for(int i = 0; i < memberEmails.size(); i++){
            Person temp = Person.load(context,memberEmails.get(i));
            if(temp.getName().equals(name))
                return temp;
        }
        return null;
    }
    public void inviteMember(String email, Context context){
        if(inviteEmails.contains(email)){
            Functions.showMessage("Invite already exists",context);
            return;
        }
        final Functions functions = new Functions((Activity)context);
        DocumentReference ref = FirebaseFirestore.getInstance().document("data/invites");
        HashMap<String, Object> data = new HashMap<>();
        data.put(functions.formatEmail(email),familyName);
        ref.update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful())
                    functions.showMessage(task.getException().getMessage());
                else{
                    functions.showMessage("Invite successfully sent");
                }
            }
        });
        if(inviteEmails == null)
            inviteEmails = new ArrayList<>();
        inviteEmails.add(email);
        save(context);
    }

    public void addMember(String email, Context context){
        if(!memberEmails.contains(email)) {
            memberEmails.add(email);
            save(context);
        }
    }
    public void removeMember(String email, Context context){
        memberEmails.remove(email);
        save(context);
        if(memberEmails.size() == 0) {
            for(int i = 0; i < inviteEmails.size(); i++)
                removeInvite(inviteEmails.get(i),context);
            deleteFamily(context);
        }
    }

    private void deleteFamily(Context context){
        unsave(context);
        DocumentReference ref = FirebaseFirestore.getInstance().document("data/family");
        HashMap<String, Object> data = new HashMap<>();
        data.put(familyName, FieldValue.delete());
        ref.update(data);
    }

    public void removeInvite(final String email, final Context context){
        inviteEmails.remove(email);
        save(context);
        DocumentReference ref = FirebaseFirestore.getInstance().document("data/invites");
        HashMap<String, Object> data = new HashMap<>();
        data.put(Functions.formatEmail2(email), FieldValue.delete());
        ref.update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Log.d("Family",email+" deleted Successfully");
                else
                    Functions.showMessage(task.getException().getMessage(), context);
            }
        });
    }

    public String getFamilyName(){
        return familyName;
    }

    public static void download(final Context context, final String familyName, final TaskListener listener){
        DocumentReference ref = FirebaseFirestore.getInstance().document("data/family");
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String familyData = (String)task.getResult().get(familyName);
                    if(familyData != null){
                        SharedPreferences sharedPreferences = context.getSharedPreferences("Family",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("Family", familyData).apply();
                        listener.authResult(TaskName.DB_FAMILY_LOADED_SUCCESSFULLY);
                    }else{
                        Log.e("Family","FamilyData empty");
                    }
                }else{
                    Log.e("Family",task.getException().getMessage());
                }
            }
        });
    }
    public static Family load(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Family",Context.MODE_PRIVATE);
        String allData = sharedPreferences.getString("Family", null);
        Gson gson = new Gson();
        return gson.fromJson(allData, Family.class);
    }
    public void save(Context context){
        Gson gson = new Gson();
        String allData = gson.toJson(this);
        SharedPreferences sharedPreferences = context.getSharedPreferences("Family",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Family", allData).apply();
        upload();
    }
    public void unsave(Context context){
        Person.unsave(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("Family",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
    }

    public void upload(){
        Gson gson = new Gson();
        String thisFamily = gson.toJson(this);
        DocumentReference ref = FirebaseFirestore.getInstance().document("data/family");
        HashMap<String, Object> data = new HashMap<>();
        data.put(familyName,thisFamily);
        ref.update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful())
                    Log.e("Family",task.getException().getMessage());
            }
        });
    }
}
