package com.EventHorizon.homeschoolr;

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
    Database(Context context){this.context = context;}

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
        return (String)read(task, Functions.formatEmail(email));
    }

    public void createUserAndFamily(String email, String ID, String name, boolean isParent, String familyName){
        WriteBatch batch = null;
        Map<String, Object> data = new HashMap<>();
        ArrayList<String> list = new ArrayList<>();
        //todo write to family
        data = new HashMap<>();
        data.put("name",familyName);
        list.add(ID);
        data.put("members", list);
        list = new ArrayList<>();
        data.put("invitedMembers", list);
        String familyID = makeDoc(family);
        batch = write(data, family+"/"+familyID,null);
        //todo write to user
        data = new HashMap<>();
        data.put("name",name);
        data.put("isParent",isParent);
        data.put("email",email);
        data.put("familyName",familyName);
        batch = write(data, user+"/"+ID, batch);
        //todo write to userIndex
        data = new HashMap<>();
        data.put(Functions.formatEmail(email),ID);
        batch = write(data, userIndex, batch);
        //todo write to familyIndex
        data = new HashMap<>();
        data.put(familyName, familyID);
        batch = write(data, familyIndex, batch);

        writeCommit(DatabaseTask.DB_CREATE_USER_AND_FAMILY, batch);
    }
    public boolean createUserAndFamily(Task<Void> task){
        if(task.isSuccessful()){
            Log.d("Database",context.getString(R.string.userCreationSuccessful));
            Functions.showMessage(context.getString(R.string.userCreationSuccessful), context, true);
        }else{
            Log.d("Database",task.getException().getMessage());
            Functions.showMessage(task.getException().getLocalizedMessage(), context, true);
        }
        return task.isSuccessful();
    }

//    private String userPathPrefix = "users/";
//    private String familyIDPathPrefix = "family/";
//    private String familyMembersPathSuffix = "/members";
//    //private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/");
//    private FirebaseFirestore database = FirebaseFirestore.getInstance();
//    WriteBatch writeBatch = database.batch();
//    Map<String, Object> userDataForDeletion = new HashMap<>();
//
//    private void populateUserDataForDeletion(){
//        userDataForDeletion.put("familyId", FieldValue.delete());
//        userDataForDeletion.put("isParent", FieldValue.delete());
//        userDataForDeletion.put("name", FieldValue.delete());
//    }
//
//    private String getUserPath(String email){return "users/"+email;}
//
//    private CollectionReference getC(String prefix, String middle, String suffix){
//        return database.collection(prefix+middle+suffix);
//    }
//    private CollectionReference getC(String prefix, String middle){
//        return database.collection(prefix+middle);
//    }
//    private DocumentReference getD(String prefix, String middle, String suffix){
//        return database.document(prefix+middle+suffix);
//    }
//    private DocumentReference getD(String prefix, String middle){
//        return database.document(prefix+middle);
//    }
//
//    //Asks database to check for email
//    public void checkEmail(String email, Context context){
//        final DatabaseListener listener = (DatabaseListener) context;
//        getD(userPathPrefix,email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                listener.onDatabaseResultR(DatabaseTask.CHECK_EMAIL_EXISTS, task);
//            }
//        });
//    }
//    //Checks email after result
//    public boolean checkEmail(Task<DocumentSnapshot> task, Context context){
//        if(task.isSuccessful()) {
//            return task.getResult().exists();
//        }
//        else {
//            Log.e("Database",task.getException().toString());
//            Functions.showMessage(task.getException().toString(), context , true);
//            return false;
//        }
//    }
//
//    //Asks database to check for familyID
//    public void checkFamilyID(String id, Context context){
//        final DatabaseListener listener = (DatabaseListener) context;
//        getD(familyIDPathPrefix,id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                listener.onDatabaseResultR(DatabaseTask.CHECK_FAMILY_ID_EXISTS, task);
//            }
//        });
//    }
//    //Checks familyID after result
//    public boolean checkFamilyID(Task<DocumentSnapshot> task, Context context){
//        if(task.isSuccessful()) {
//            return task.getResult().exists();
//        }
//        else {
//            Log.e("Database",task.getException().toString());
//            Functions.showMessage(task.getException().toString(), context , true);
//            return false;
//        }
//    }
//    //Adds user needing a new FamilyID
//    public void addUser(String email, String name, String familyId, boolean isParent, Context context){
//        //saving family ID first
//        Map<String, Object> familyIDSave = new HashMap<String, Object>();
//        List<String> emails = new ArrayList<>();
//        emails.add(email);
//        familyIDSave.put("members",emails);
//        Map<String, Object> userSave = new HashMap<String, Object>();
//        userSave.put("email",email);
//        userSave.put("familyID",familyId);
//        userSave.put("isParent",isParent);
//        userSave.put("name",name);
//        writeBatch.set(getD(familyIDPathPrefix,familyId),familyIDSave);
//        writeBatch.set(getD(userPathPrefix,email),userSave);
//        final DatabaseListener listener = (DatabaseListener) context;
//        writeBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
//             @Override
//             public void onComplete(@NonNull Task<Void> task) {
//                 listener.onDatabaseResultW(DatabaseTask.CHECK_ADD_USER, task);
//             }
//         }
//        );
//    }
//    public boolean addUser(Task<Void> task, Context context){
//        if(task.isSuccessful()) {
//            Log.d("LoginActivity", "User Successfully Made!");
//            Functions.showMessage("Sign up successful!",context, true);
//            return true;
//        }
//        else {
//            Log.w("LoginActivity", task.getException());
//            Functions.showMessage(task.getException().toString(), context, true);
//            return false;
//        }
//    }
//
//    public void deleteAccount(String email){
//    }
}
