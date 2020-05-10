package com.EventHorizon.homeschoolr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Google {
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions googleSignIn;
    private GoogleSignInAccount account;
    Activity activity;

    Google(Context context){
        isSignedIn(context);}

    boolean isSignedIn(Context context){
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(context);
        return (account != null);
    }

    void init(Context context){
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        googleSignIn = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(context, googleSignIn);
    }

    void signIn(Activity activity) {
        if(mGoogleSignInClient == null)
            init(activity);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, 1);
    }

    void signOut(final Activity activity, final Class goTo) {
        if(isSignedIn(activity)) {
            init(activity);
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(activity, goTo);
                            activity.startActivity(intent);
                        }
                    });
        }
    }

    boolean handleSignInResult(Intent data) {
        Task<GoogleSignInAccount> completedTask = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            account = completedTask.getResult(ApiException.class);
            return true;
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google", "signInResult:failed code=" + e.getStatusCode());
            return false;
        }
    }

    public String getEmail(){return (account != null)? account.getEmail() : null;}
    public String getGivenName(){return (account != null)? account.getGivenName() : null; }
    public String getFamilyName(){return (account != null)? account.getFamilyName() : null; }
    public String getDisplayName(){return (account != null)? account.getDisplayName() : null; }
    public String getId(){return (account != null)? account.getId() : null;}
    public Uri getPhoto(){return (account != null)? account.getPhotoUrl() : null; }
}
