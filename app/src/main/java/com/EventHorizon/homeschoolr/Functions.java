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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

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

    public boolean checkPassword(String password){
        if(password.length() > 6)
            return true;
        else
            showMessage(context.getString(R.string.badPasswordFormat),false);
        return false;
    }
}
