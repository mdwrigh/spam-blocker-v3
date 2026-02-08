package com.spamblocker;

import android.content.Context;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.lang.reflect.Method;

public class CallAnswerer {
    private static final String TAG = "CallAnswerer";

    public static void answerCall(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Android 8.0+ method
                TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                if (telecomManager != null) {
                    telecomManager.acceptRingingCall();
                    Log.d(TAG, "Call answered using TelecomManager");
                }
            } else {
                // Fallback for older Android versions
                answerCallReflection(context);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to answer call", e);
            // Try alternative method
            answerCallReflection(context);
        }
    }

    private static void answerCallReflection(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> telephonyClass = Class.forName(telephonyManager.getClass().getName());
            Method method = telephonyClass.getDeclaredMethod("answerRingingCall");
            method.setAccessible(true);
            method.invoke(telephonyManager);
            Log.d(TAG, "Call answered using reflection");
        } catch (Exception e) {
            Log.e(TAG, "Failed to answer call using reflection", e);
            
            // Last resort - simulate headset button press
            try {
                Runtime.getRuntime().exec("input keyevent 79"); // KEYCODE_HEADSETHOOK
                Log.d(TAG, "Attempted to answer call using keyevent");
            } catch (Exception ex) {
                Log.e(TAG, "All call answering methods failed", ex);
            }
        }
    }
}