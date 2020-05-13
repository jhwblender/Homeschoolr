package com.EventHorizon.homeschoolr;

import android.app.Activity;
import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firestore.v1.Write;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database{
    String index = "index";
    String familyIndex = index+"/family";
    String toDeleteIndex = index+"/toDelete";
    String userIndex = index+"/user";
    String user = "user";
    String family = "family";
    Context context;
    Functions functions;
    Database(Context context){
        this.context = context;
        functions = new Functions((Activity)context);
    }

    private void loadD(final DatabaseTask taskName, String path){
        DocumentReference dRef = FirebaseFirestore.getInstance().document(path);
        final DatabaseListener listener = (DatabaseListener) context;
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                listener.onDatabaseResultR(taskName, task);
            }
        });
    }

    private Object read(Task<DocumentSnapshot> task, String key) throws Exception {
        if(task.isSuccessful()){
            try {
                return task.getResult().get(key);
            }catch(NullPointerException e){
                return null;
            }
        }else{
            Log.w("Database",task.getException().getLocalizedMessage());
            throw new Exception("Database read error");
        }
    }

    private String makeDoc(String collection){
        return FirebaseFirestore.getInstance().collection(collection).document().getId();
    }
    private WriteBatch write(Map<String, Object> data, String path, WriteBatch batch){
        batch = (batch != null)? batch : FirebaseFirestore.getInstance().batch();
        DocumentReference doc = FirebaseFirestore.getInstance().document(path);
        batch.set(doc, data);
        return batch;
    }
    private void writeCommit(final DatabaseTask taskName, WriteBatch batch){
        final DatabaseListener listener = (DatabaseListener) context;
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onDatabaseResultW(taskName, task);
            }
        });
    }


    public void getFamilyID(){loadD(DatabaseTask.DB_GET_FAMILY_ID, familyIndex);}
    public String getFamilyID(Task<DocumentSnapshot> task, String familyName) throws Exception {
        return (String)read(task, familyName);}

    public void getUserID(){loadD(DatabaseTask.DB_GET_USER_ID, userIndex);}
    public String getUserID(Task<DocumentSnapshot> task, String email) throws Exception {
        return (String)read(task, functions.formatEmail(email));
    }

    public void createUserAndFamily(String email, String name, boolean isParent, String familyName){
        WriteBatch batch = null;
        Map<String, Object> data = new HashMap<>();
        ArrayList<String> list = new ArrayList<>();
        String familyID = makeDoc(family);
        String userID = makeDoc(user);
        //write to family
        data = new HashMap<>();
        data.put("name",familyName);
        list.add(userID);
        data.put("members", list);
        list = new ArrayList<>();
        data.put("invitedMembers", list);
        batch = write(data, family+"/"+familyID,null);
        //write to user
        data = new HashMap<>();
        data.put("name",name);
        data.put("isParent",isParent);
        data.put("email",email);
        data.put("familyName",familyName);
        batch = write(data, user+"/"+userID, batch);
        //write to userIndex
        data = new HashMap<>();
        data.put(functions.formatEmail(email),userID);
        batch = write(data, userIndex, batch);
        //write to familyIndex
        data = new HashMap<>();
        data.put(familyName, familyID);
        batch = write(data, familyIndex, batch);

        writeCommit(DatabaseTask.DB_CREATE_USER_AND_FAMILY, batch);
    }
    public boolean createUserAndFamily(Task<Void> task){
        if(task.isSuccessful()){
            Log.d("Database",context.getString(R.string.userCreationSuccessful));
            functions.showMessage(context.getString(R.string.userCreationSuccessful), true);
        }else{
            Log.d("Database",task.getException().getMessage());
            functions.showMessage(task.getException().getLocalizedMessage(), true);
        }
        return task.isSuccessful();
    }

    public void deleteAccount(String email){
        getFamilyID();
        getUserID();
    }
    public void deleteAccount(){

    }
}
