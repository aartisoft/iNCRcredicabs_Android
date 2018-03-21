package com.ncr.interns.codecatchers.incredicabs;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ncr.interns.codecatchers.incredicabs.Adapter.CabMatesAdapter;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.CabMatesContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.EmployeeCabMatesDetails;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.EmployeeContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.EmployeeData;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.NcabSQLiteHelper;
import com.ncr.interns.codecatchers.incredicabs.notification.DeleteFirebaseTokenService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    Button login;
    EditText user, pass;
    String EmployeeQlID;
    String EmployeeFirstName;
    String EmployeeMiddleName;
    String EmployeeLastName;
    String Level1ManagerQlid;
    String Level2ManagerQlid;
    String Level1ManagerName;
    String Level2ManagerName;
    String HomeAddress;
    String OfficeAddress;
    String contactNumber;
    String refreshedToken;
    String emergencyContactNumber;
    String firebaseToken;
    String qlid;

    private static final String MY_PREFERENCES = "MyPrefs_login";
    int role;
    JSONObject jsonBodyRequest;
    NcabSQLiteHelper ncabSQLiteHelper;
    SQLiteDatabase mSqLiteDatabase;
    Context context = this;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String TAG = Login.class.getSimpleName();

    String url = "http://ec2-18-219-151-75.us-east-2.compute.amazonaws.com:8080/NCAB/EmployeeService/login-android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String copyStr = getResources().getString(R.string.login_copy);
        TextView copyTV = (TextView)findViewById(R.id.copy_text);

        copyTV.setText(Html.fromHtml(copyStr, 0));

        user = findViewById(R.id.editText_Qlid);
        pass = findViewById(R.id.editText_password);

        login = findViewById(R.id.button);

        //<editor-fold desc="Code to Create SqLite Database Table">
        ncabSQLiteHelper = new NcabSQLiteHelper(this);
        mSqLiteDatabase = ncabSQLiteHelper.getWritableDatabase();
        //</editor-fold>

        final Intent intent = new Intent(this, DeleteFirebaseTokenService.class);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qlid = user.getText().toString();
                //json for request
                jsonBodyRequest = new JSONObject();
                try {
                    jsonBodyRequest.put("qlid", user.getText().toString());
                    jsonBodyRequest.put("password", pass.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                sharedPreferences = context.getSharedPreferences(MY_PREFERENCES,Context.MODE_PRIVATE);
                firebaseToken = FirebaseInstanceId.getInstance().getToken();
                Log.d(TAG, "firebase Token:- "+firebaseToken);

                String shared_pref_userName = sharedPreferences.getString("user_qlid","");
                if(shared_pref_userName.isEmpty()){
                    login(jsonBodyRequest); //fuction with the code to Hit the Login API
                } else{
                    Intent intent = new Intent(Login.this,Dashboard.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private void parseJSON(JSONObject response) {

        try {
            EmployeeQlID = response.getString("empQlid");
            EmployeeFirstName = response.getString("empFName");
            EmployeeLastName = response.getString("empLName");
            Level1ManagerQlid = response.getString("empMgrQlid1");
            Level2ManagerQlid = response.getString("empMgrQlid2");
            Level1ManagerName = response.getString("mgr1Name");
            Level2ManagerName = response.getString("mgr2Name");
            HomeAddress = response.getString("empAddLine1")+" "+response.getString("empAddLine2");
            contactNumber = response.getString("empMobNbr");
            emergencyContactNumber = response.getString("empEmergNbr");
            role = response.getInt("rolesId");

            //<editor-fold desc="Insert Employee Data into Table">
            ContentValues values = new ContentValues();
            values.put(EmployeeContract.COLUMN_EMP_QLID,EmployeeQlID);
            values.put(EmployeeContract.COLUMN_FIRST_NAME,EmployeeFirstName);
            values.put(EmployeeContract.COLUMN_LAST_NAME,EmployeeLastName);
            values.put(EmployeeContract.COLUMN_LEVEL_1_MANAGER,Level1ManagerQlid);
            values.put(EmployeeContract.COLUMN_LEVEL_2_MANAGER,Level2ManagerQlid);
            values.put(EmployeeContract.COLUMN_LEVEL_1_MANAGER_NAME,Level1ManagerName);
            values.put(EmployeeContract.COLUMN_LEVEL_2_MANAGER_NAME,Level2ManagerName);
            values.put(EmployeeContract.COLUMN_HOME_ADDRESS,HomeAddress);
            values.put(EmployeeContract.COLUMN_OFFICE_ADDRESS,"NCR Corporation, 5th Floor, Vipul Plaza, Suncity, Sector 54,Gurgoan");
            values.put(EmployeeContract.COLUMN_CONTACT_NUMBER,contactNumber);
            values.put(EmployeeContract.COLUMN_EMERGENCY_CONTACT_NUMBER,emergencyContactNumber);
            values.put(EmployeeContract.COLUMN_EMP_ROLE,role);
            mSqLiteDatabase.insert(EmployeeContract.DB_TABLE,null,values);
            Log.d(TAG, "parseJSON: Data Inserted to Employee Table");
            //</editor-fold>


            //<editor-fold desc="Yet to Implement">
            JSONArray cabMates = response.getJSONArray("rosterInfo");
            for (int i = 0;i<cabMates.length();i++){

                JSONObject cabmateX = cabMates.getJSONObject(i);
                String CabMate_Qlid = cabmateX.getString("Qlid");
                String CabMate_name = cabmateX.getString("f_name")+" "+cabmateX.getString("l_name");
                String CabMate_contactNumber = cabmateX.getString("e_mob");
                String CabMate_address = cabmateX.getString("p_a");
                ContentValues cabmateValues = new ContentValues();
                cabmateValues.put(CabMatesContract.COLUMN_CABMATE_QLID,CabMate_Qlid);
                cabmateValues.put(CabMatesContract.COLUMN_CABMATE_NAME,CabMate_name);
                cabmateValues.put(CabMatesContract.COLUMN_CABMATE_CONTACT_NUMBER,CabMate_contactNumber);
                cabmateValues.put(CabMatesContract.COLUMN_CABMATE_ADDRESS,CabMate_address);
                mSqLiteDatabase.insert(CabMatesContract.DB_TABLE,null,cabmateValues);
                Log.d(TAG, "parseJSON: Data Inserted to Cabmate Table row :-_"+i+"\n");

            }
            //</editor-fold>

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void login(JSONObject jsonBodyRequest){

        Log.d(TAG, "login: qlid: " + qlid);
        FirebaseTokenUtility ftu = new FirebaseTokenUtility(Login.this);
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBodyRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i("VOLLEY", "inside onResponse method: Login");
                        Log.i("VOLLEY", response.toString());

                        try {
                            if (response.getString("success").equalsIgnoreCase("true")) {
                                Intent intent = new Intent(Login.this, Dashboard.class);
                                parseJSON(response);

                                //<editor-fold desc="Saving the User Credentials in Shared Preferences">
                                sharedPreferences = context.getSharedPreferences(MY_PREFERENCES,Context.MODE_PRIVATE);
                                editor = sharedPreferences.edit();
//                                 qlid = user.getText().toString();
                                editor.putString("user_qlid",user.getText().toString());
                                editor.putString("user_name",
                                        response.getString("empFName")+" "+
                                                response.getString("empMName")+" "+
                                                response.getString("empLName")
                                );
                                editor.putString("user_password",pass.getText().toString());
                                editor.apply();
                                //</editor-fold>

                                startActivity(intent);
                                Toast.makeText(Login.this, "Welcome "+EmployeeFirstName, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Login.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
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

        RESTService.getInstance(Login.this).addToRequestQueue(jsonObjRequest);

        ftu.saveTokenToDB(
//                sharedPreferences.getString("user_qlid", ""),
                qlid,
                firebaseToken
        );
    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ncabSQLiteHelper.close();
        super.onDestroy();

    }
}