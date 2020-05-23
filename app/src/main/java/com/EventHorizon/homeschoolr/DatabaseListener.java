package com.EventHorizon.homeschoolr;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

public interface DatabaseListener{
    public void onDatabaseResultR(DatabaseTask taskName, Task<DocumentSnapshot> task, int step); //read
    public void onDatabaseResultW(DatabaseTask taskName, Task<Void> task, int step); //write
    public void onDatabaseResultA(DatabaseTask taskName, Task<AuthResult> task); //authentication
}
