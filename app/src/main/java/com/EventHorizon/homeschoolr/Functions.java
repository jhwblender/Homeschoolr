package com.EventHorizon.homeschoolr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class Functions {
    private Activity context;

    Functions(Activity context){
        this.context = context;
    }

    public void showMessage(String message, boolean lengthLong){
        if(message == null)
            message = "null";
        Log.d("Functions","Message displayed: "+message);
        int duration = lengthLong? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message,duration);
        toast.show();
    }
    public void showMessage(String message){
        showMessage(message, true);
    }
    public static void showMessage(String message, Context context){
        Functions functions = new Functions((Activity)context);
        functions.showMessage(message);
    }

    public void goToActivity(Class activity){
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }

    //displays loading
    public void loadingView(boolean isLoading){
        try {
            context.findViewById(R.id.loadingSymbol)
                    .setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
            if (isLoading)
                context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            else
                context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }catch(Exception e){
            //Nothing
        }
    }

    public boolean checkEmail(String email){
        if(!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return true;
        showMessage(context.getString(R.string.badEmailFormat), false);
        return false;
    }
    public String formatEmail(String email){
        return email.toLowerCase().replace(".",",");
    }
    public static String formatEmail2(String email){return email.toLowerCase().replace(".",",");}
    public static String namesToFakeEmail(String name, String familyName){
        return (name+"@"+familyName+".homeschoolr").replace(" ","_");
    }

    public boolean checkPassword(String password){
        if(password.length() > 6)
            return true;
        else
            showMessage(context.getString(R.string.badPasswordFormat),false);
        return false;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = context.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(context);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void adInit(boolean show){
        AdView mAdView;
        mAdView = context.findViewById(R.id.adView);
        if(show){
            //String appID = "ca-app-pub-6579091093181482~2242781779";
            //String adUnitID = "ca-app-pub-6579091093181482/9371296902";
            //String testID = "ca-app-pub-3940256099942544/6300978111";

            MobileAds.initialize(context, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }else{
            mAdView.setVisibility(View.GONE);
        }
    }
    public void adInit(Person user){
        if(user != null)
            adInit(user.getShowAds());
    }
}
