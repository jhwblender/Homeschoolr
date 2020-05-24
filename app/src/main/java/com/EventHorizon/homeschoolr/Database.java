package com.EventHorizon.homeschoolr;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database{
    private String index = "index";
    private String familyIndex = index+"/family";
    private String userIndex = index+"/user";
    private String user = "user";
    private String family = "family";

    private Context context;
    private Functions functions;

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
                listener.onDatabaseResultR(taskName, task, 0);
            }
        });
    }
    private void loadD(final DatabaseTask taskName, String path, final int step){
        DocumentReference dRef = FirebaseFirestore.getInstance().document(path);
        final DatabaseListener listener = (DatabaseListener) context;
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                listener.onDatabaseResultR(taskName, task, step);
            }
        });
    }

    private Object read(Task<DocumentSnapshot> task, String key) throws Exception {
        if(task.isSuccessful()){
            try {
                return task.getResult().get(key);
            }catch(NullPointerException e){
                throw new Exception(key+" doesn't exist");
            }
        }else{
            Log.w("Database",task.getException().getLocalizedMessage());
            throw new Exception("Database read error");
        }
    }

    private String makeDoc(String collection){
        return FirebaseFirestore.getInstance().collection(collection).document().getId();
    }
    private WriteBatch write(Map<String, Object> data, String path, WriteBatch batch, boolean overwrite){
        batch = (batch != null)? batch : FirebaseFirestore.getInstance().batch();
        DocumentReference doc = FirebaseFirestore.getInstance().document(path);
        if(overwrite)
            batch.set(doc, data);
        else
            batch.set(doc, data, SetOptions.merge());
        return batch;
    }
    private WriteBatch update(Map<String, Object> data, String path, WriteBatch batch){
        batch = (batch != null)? batch : FirebaseFirestore.getInstance().batch();
        DocumentReference doc = FirebaseFirestore.getInstance().document(path);
        batch.update(doc, data);
        return batch;
    }
    private void writeCommit(final DatabaseTask taskName, WriteBatch batch){
        final DatabaseListener listener = (DatabaseListener) context;
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onDatabaseResultW(taskName, task, 0);
            }
        });
    }

    public void getName(){loadD(DatabaseTask.DB_GET_USER_NAME, userIndex,0);}
    public void getName(Task<DocumentSnapshot> task, String email) throws Exception {
        loadD(DatabaseTask.DB_GET_USER_NAME, user+"/"+(String)read(task, functions.formatEmail(email)),1);}
    public String getName(Task<DocumentSnapshot> task) throws Exception {return (String)read(task, "name");}

    //todo add ability to change name, email, and familyName

    public void getFamilyName(){loadD(DatabaseTask.DB_GET_FAMILY_NAME, userIndex,0);}
    public void getFamilyName(Task<DocumentSnapshot> task, String email) throws Exception {
        loadD(DatabaseTask.DB_GET_FAMILY_NAME, user+"/"+(String)read(task, functions.formatEmail(email)),1);}
    public String getFamilyName(Task<DocumentSnapshot> task) throws Exception {return (String)read(task, "familyName");}

    public void getFamilyID(){loadD(DatabaseTask.DB_GET_FAMILY_ID, familyIndex);}
    public String getFamilyID(Task<DocumentSnapshot> task, String familyName) throws Exception {
        return (String)read(task, familyName);}

    public void getUserID(){loadD(DatabaseTask.DB_GET_USER_ID, userIndex);}
    public String getUserID(Task<DocumentSnapshot> task, String email) throws Exception {
        return (String)read(task, functions.formatEmail(email));}

    public void createUserAndFamily(String email, String name, boolean isParent, String familyName){
        WriteBatch batch = null;
        Map<String, Object> data;
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
        batch = write(data, family+"/"+familyID,null, false);
        //write to user
        data = new HashMap<>();
        data.put("name",name);
        data.put("isParent",isParent);
        data.put("email",email);
        data.put("familyName",familyName);
        batch = write(data, user+"/"+userID, batch, false);
        //write to userIndex
        data = new HashMap<>();
        data.put(functions.formatEmail(email),userID);
        batch = write(data, userIndex, batch, false);
        //write to familyIndex
        data = new HashMap<>();
        data.put(familyName, familyID);
        batch = write(data, familyIndex, batch, false);

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

    //Step 1: get userID from email
    //Step 2: get familyName from user using userID
    //Step 3: get familyID from familyName

    private Map<String, Object> deleteInfo = new HashMap<>();
    public void deleteAccount(){
        loadD(DatabaseTask.DB_DELETE_USER,userIndex,1);
    }
    public void deleteAccount(Task<DocumentSnapshot> task, int step, Object object) throws Exception {
        if(task.isSuccessful()) {
            switch (step) {
                case 1:
                    deleteInfo.put("email", (String)object);
                    deleteInfo.put("userID", read(task, functions.formatEmail((String)object)));
                    loadD(DatabaseTask.DB_DELETE_USER, user+"/"+deleteInfo.get("userID"),2);
                    Log.d("Database","Deletion step 1: got email: "+deleteInfo.get("email"));
                    Log.d("Database","Deletion step 1: got user ID: "+deleteInfo.get("userID"));
                    break;
                case 2:
                    deleteInfo.put("familyName", read(task, "familyName"));
                    loadD(DatabaseTask.DB_DELETE_USER, familyIndex, 3);
                    Log.d("Database","Deletion step 2: got family name: "+deleteInfo.get("familyName"));
                    break;
                case 3:
                    deleteInfo.put("familyID", read(task, (String)deleteInfo.get("familyName")));
                    loadD(DatabaseTask.DB_DELETE_USER, family+"/"+deleteInfo.get("familyID"), 4);
                    Log.d("Database","Deletion step 3: got family ID: "+deleteInfo.get("familyID"));
                    break;
                case 4:
                    deleteInfo.put("members",read(task, "members"));
                    Log.d("Database","Deletion step 4: beginning to delete everything");
                    //Delete everything now
                    Map<String, Object> toDelete;
                    WriteBatch batch;
                    //delete from members of family
                    ArrayList<String> members = (ArrayList<String>)deleteInfo.get("members");
                    toDelete = new HashMap<>();
                    if(members.size() == 1) { //if last member of family
                        toDelete.put("toDelete", true);
                    }else
                        toDelete.put("members",FieldValue.arrayRemove(deleteInfo.get("userID")));
                    batch = write(toDelete,family+"/"+deleteInfo.get("familyID"),null, false);
                    //Delete familyIndex
                    if(members.size() == 1) {
                        toDelete = new HashMap<>();
                        toDelete.put((String) deleteInfo.get("familyName"), FieldValue.delete());
                        batch = update(toDelete, familyIndex, batch);
                    }
                    //Delete from users
                    toDelete = new HashMap<>();
                    toDelete.put("toDelete",true);
                    batch = write(toDelete, user+"/"+deleteInfo.get("userID"),batch, false);
                    //Delete userIndex
                    toDelete = new HashMap<>();
                    toDelete.put(functions.formatEmail((String)deleteInfo.get("email")), FieldValue.delete());
                    batch = update(toDelete, userIndex, batch);
                    writeCommit(DatabaseTask.DB_DELETE_USER, batch);
                    break;
            }
        }else {
            Log.d("Database", task.getException().getMessage());
            functions.showMessage(task.getException().getLocalizedMessage(), true);
        }
    }
    public boolean deleteAccount(Task<Void> task){
        Log.d("Database","last delete account called");
        if(task.isSuccessful()){
            Log.d("Database",context.getString(R.string.accountErased));
            functions.showMessage(context.getString(R.string.accountErased),true);
            return true;
        }else {
            Log.d("database",task.getException().getMessage());
            functions.showMessage(task.getException().getLocalizedMessage(),true);
        }
        return false;
    }
}

