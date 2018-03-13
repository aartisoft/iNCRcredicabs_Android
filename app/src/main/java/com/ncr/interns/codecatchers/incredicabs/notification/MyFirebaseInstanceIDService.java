package com.ncr.interns.codecatchers.incredicabs.notification;

/**
 * Created by pg250235 on 2/25/2018.
 */

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService  extends FirebaseInstanceIdService {
    public static final String FIREBASE_TOKEN = "Firebase token";
    private static final String TAG = "MyFirebaseIDService";
    private static final String MY_PREFERENCES = "MyPrefs";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // TODO: 3/12/2018 Add this Token to the Database Team_A
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //<editor-fold desc="Replace this code with The Code to enter the Token into Database">
        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFERENCES,MODE_PRIVATE);
        sharedPreferences.edit().putString(FIREBASE_TOKEN, refreshedToken).apply();
        //</editor-fold>
    }



}
