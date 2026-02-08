package com.spamblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneStateReceiver extends BroadcastReceiver {
    private static final String TAG = "PhoneStateReceiver";
    private static String lastIncomingNumber = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!CallDetectionService.isRunning()) {
            return;
        }

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        Log.d(TAG, "Phone state changed: " + state + ", Number: " + incomingNumber);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            handleIncomingCall(context, incomingNumber);
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            // Call answered - start fax tone if this was a spam call
            if (lastIncomingNumber != null && isSpamCall(context, lastIncomingNumber)) {
                FaxTonePlayer.playFaxTone(context);
            }
        } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            // Call ended
            lastIncomingNumber = null;
            FaxTonePlayer.stopFaxTone();
        }
    }

    private void handleIncomingCall(Context context, String phoneNumber) {
        if (phoneNumber == null) {
            return;
        }

        lastIncomingNumber = phoneNumber;

        if (isSpamCall(context, phoneNumber)) {
            Log.d(TAG, "Detected spam call from: " + phoneNumber);
            CallAnswerer.answerCall(context);
        }
    }

    private boolean isSpamCall(Context context, String phoneNumber) {
        // Check if number is in contacts
        if (isInContacts(context, phoneNumber)) {
            return false;
        }

        // Basic spam detection rules
        return isUnknownNumber(phoneNumber) || 
               isRobocallPattern(phoneNumber) ||
               isTelemarketingPattern(phoneNumber);
    }

    private boolean isInContacts(Context context, String phoneNumber) {
        try {
            String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME};
            String selection = ContactsContract.PhoneLookup.NUMBER + " = ?";
            String[] selectionArgs = {phoneNumber};

            Cursor cursor = context.getContentResolver().query(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI.buildUpon()
                    .appendPath(phoneNumber).build(),
                projection, selection, selectionArgs, null
            );

            if (cursor != null) {
                boolean found = cursor.getCount() > 0;
                cursor.close();
                return found;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking contacts", e);
        }
        return false;
    }

    private boolean isUnknownNumber(String phoneNumber) {
        // Block calls from numbers that look suspicious
        return phoneNumber.startsWith("1") && phoneNumber.length() == 11 ||
               phoneNumber.startsWith("+1") && phoneNumber.length() == 12 ||
               phoneNumber.matches(".*[0-9]{3}-[0-9]{3}-[0-9]{4}.*");
    }

    private boolean isRobocallPattern(String phoneNumber) {
        // Common robocall patterns
        String cleanNumber = phoneNumber.replaceAll("[^0-9]", "");
        
        // Same first 6 digits as your number (neighbor spoofing)
        // This would need to be configured with user's actual number
        
        // Sequential or repeated digits
        return cleanNumber.matches(".*([0-9])\\1{3,}.*") || // 4+ same digits in a row
               cleanNumber.matches(".*0123.*") ||
               cleanNumber.matches(".*1234.*") ||
               cleanNumber.matches(".*2345.*");
    }

    private boolean isTelemarketingPattern(String phoneNumber) {
        String cleanNumber = phoneNumber.replaceAll("[^0-9]", "");
        
        // Common telemarketing area codes (this is a simplified list)
        String[] spamAreaCodes = {
            "800", "888", "877", "866", "855", "844", "833", "822",
            "900", "976", "456", "123"
        };

        for (String areaCode : spamAreaCodes) {
            if (cleanNumber.startsWith("1" + areaCode) || cleanNumber.startsWith(areaCode)) {
                return true;
            }
        }

        return false;
    }
}