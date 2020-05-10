package com.EventHorizon.homeschoolr;

import android.content.Context;
import android.widget.Toast;

public class PopupMessage {
    public static void showMessage(String message, Context context, boolean lengthLong){
        int duration = lengthLong? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message,duration);
        toast.show();
    }
}
