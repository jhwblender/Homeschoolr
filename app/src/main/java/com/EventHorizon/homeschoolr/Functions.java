package com.EventHorizon.homeschoolr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class Functions {

    public static void showMessage(String message, Context context, boolean lengthLong){
        if(message == null)
            message = "null";
        int duration = lengthLong? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message,duration);
        toast.show();
    }
    public static void goToActivity(Class activity, Context context){
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }

    //displays loading
    public static void loadingView(boolean isLoading, Activity context){
        context.findViewById(R.id.loadingSymbol)
                .setVisibility(isLoading? View.VISIBLE : View.INVISIBLE);
        if(isLoading)
           context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        else
            context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static boolean checkEmail(String email, Context context){
        if(!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return true;
        showMessage(context.getString(R.string.badEmailFormat), context, false);
        return false;
    }
    public static boolean checkPassword(String password, Context context){
        if(password.length() > 6)
            return true;
        else
            showMessage(context.getString(R.string.badPasswordFormat),context,false);
        return false;
    }
}
