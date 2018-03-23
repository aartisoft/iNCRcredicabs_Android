package com.ncr.interns.codecatchers.incredicabs.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ncr.interns.codecatchers.incredicabs.RESTService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pg250235 on 3/5/2018.
 */

public class Reject extends BroadcastReceiver {
    MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();
    String reqId;
    SharedPreferences sharedPreferences;
    private static final String MY_PREFERENCES = "MyPrefs";
    String url = "http://192.168.43.108:8522/NCAB/AndroidService/approval";
    @Override
    public void onReceive(final Context context, Intent intent) {

        sharedPreferences = context.getSharedPreferences(MY_PREFERENCES,Context.MODE_PRIVATE);
        reqId = sharedPreferences.getString("reqId",null);

        Toast.makeText(context, "Processing for Request ID "+reqId, Toast.LENGTH_SHORT).show();
        JSONObject jsonBodyRequest = new JSONObject();
        try {
            jsonBodyRequest.put("request_id", reqId);
            jsonBodyRequest.put("Approval","REJECTED");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                jsonBodyRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i("VOLLEY", "inside onResponse method:login");
                        Log.i("VOLLEY", response.toString());
                        try {
                            if (response.getString("status").equalsIgnoreCase("success")) {
                                Toast.makeText(context, "Your response is Submitted", Toast.LENGTH_LONG).show();
                            } else {if (response.getString("status").equalsIgnoreCase("Already")) {
                                Toast.makeText(context, "Response is Already Submitted", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(context, "Failed to submit", Toast.LENGTH_LONG).show();
                            }}
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something when error occurred
                        Log.d("VOLLEY", "Something went wrong");
                        error.printStackTrace();                            }
                });

        RESTService.getInstance(context).addToRequestQueue(jsonObjRequest);

    }
}
