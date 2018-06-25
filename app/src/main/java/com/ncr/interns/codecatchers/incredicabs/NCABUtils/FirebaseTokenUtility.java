package com.ncr.interns.codecatchers.incredicabs.NCABUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.NcabSQLiteHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ag250497 on 20-Mar-18.
 */

public class FirebaseTokenUtility {
    private static final String MY_PREFERENCES = "MyPrefs";

    private final String BASE_URL = "http://ec2-18-219-151-75.us-east-2.compute.amazonaws.com:8080/NCAB";
    private final String SAVE_TOKEN_URL = BASE_URL + "/EmployeeService/set-push-token-android";
    private final String GET_TOKEN_URL = BASE_URL + "/EmployeeService/get-push-token-android";

    private final String TAG = "FirebaseTokenUtility";

    private String firebaseToken;

    private Context context;

    private NcabSQLiteHelper ncabSQLiteHelper;
    private SQLiteDatabase sqLiteDatabase;

    public static String currentToken;

    public FirebaseTokenUtility(Context context){
        this.context = context;
        ncabSQLiteHelper = new NcabSQLiteHelper(this.context);
        sqLiteDatabase = ncabSQLiteHelper.getWritableDatabase();
    }

    public void saveTokenToDB(String qlid, String token){
        Log.d(TAG, "saveTokenToDB: saving: qlid: "+ qlid + " token: " + token);
        SQLiteDatabase sqLiteDatabase = ncabSQLiteHelper.getWritableDatabase();

        JSONObject jsonBodyRequest = new JSONObject();
        try {
            jsonBodyRequest.put("qlid", qlid);
            jsonBodyRequest.put("pushToken", token);
        }catch(JSONException e){
            e.printStackTrace();
        }

//        String baseUrl = "http://localhost:8080/EmployeeService/set-push-token-android";

        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, SAVE_TOKEN_URL, jsonBodyRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("VOLLEY", "inside onResponse method: Login");
                        Log.i("VOLLEY", response.toString());
                        try {
                            if (response.getString("success").equalsIgnoreCase("true")) {
//                                Toast.makeText(FirebaseTokenUtility.this, "Token set!", Toast.LENGTH_SHORT).show();
                            } else {
//                                Toast.makeText(FirebaseTokenUtility.this, "Error in setting token!", Toast.LENGTH_LONG).show();
                            }
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
                        error.printStackTrace();
                    }
        });

        RESTService.getInstance(this.context).addToRequestQueue(jsonObjRequest);

//        return false;
    }

    public String getTokenFromDB(String qlid){
        JSONObject req = new JSONObject();

        try{
            req.put("qlid", qlid);
        }catch(Exception e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, GET_TOKEN_URL, req,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("VOLLEY", "inside onResponse method: Login");
                        Log.i("VOLLEY", response.toString());
                        try {
                            if (response.getString("success").equalsIgnoreCase("true")) {
                                FirebaseTokenUtility.currentToken = response.getString("loginRefreshToken");
//                                Toast.makeText(FirebaseTokenUtility.this, "Token set!", Toast.LENGTH_SHORT).show();
                            } else {
//                                Toast.makeText(FirebaseTokenUtility.this, "Error in setting token!", Toast.LENGTH_LONG).show();
                            }
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
                        error.printStackTrace();
                    }
                });
        RESTService.getInstance(this.context).addToRequestQueue(jsonObjRequest);

        this.firebaseToken = currentToken;

        return this.firebaseToken;
    }
}
