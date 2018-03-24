package com.ncr.interns.codecatchers.incredicabs.notification;

/**
 * Created by pg250235 on 2/25/2018.
 */

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.EmployeeContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.EmployeeData;


public class MyFirebaseInstanceIDService  extends FirebaseInstanceIdService {
    public static final String FIREBASE_TOKEN = "Firebase token";
    private static final String TAG = "MyFirebaseIDService";
    private static final String MY_PREFERENCES = "MyPrefs";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "Refreshed token: " + refreshedToken);
        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFERENCES,MODE_PRIVATE);
        sharedPreferences.edit().putString(FIREBASE_TOKEN, refreshedToken).apply();

    }



}
