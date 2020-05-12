package com.EventHorizon.homeschoolr;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private void write(final DatabaseTask taskName, String path){

    }


    public void getFamilyID(){
        loadD(DatabaseTask.DB_GET_FAMILY_ID, familyIndex);}
    public String getFamilyID(Task<DocumentSnapshot> task, String familyName) throws Exception {
        return (String)read(task, familyName);}

    public void createUserAndFamily(String name, boolean isParent, boolean joiningFamily){

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
