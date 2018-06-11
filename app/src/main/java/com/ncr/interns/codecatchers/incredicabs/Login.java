package com.ncr.interns.codecatchers.incredicabs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.CabMatesContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.ContactsContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.EmployeeContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.NcabSQLiteHelper;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.ShiftContract;
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
    ProgressDialog progressDialog;
    RelativeLayout relativeLayout;
    boolean checkCon;

    private static final String MY_PREFERENCES = "MyPrefs_login";
    int role;
    JSONObject jsonBodyRequest;
    NcabSQLiteHelper ncabSQLiteHelper;
    SQLiteDatabase mSqLiteDatabase;
    Context context = this;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String TAG = Login.class.getSimpleName();

    String baseUrl = "http://ec2-18-219-151-75.us-east-2.compute.amazonaws.com:8080";
    String loginUrl = "/NCAB/EmployeeService/doLogin-android";
    String mainUrl = "http://ec2-18-219-151-75.us-east-2.compute.amazonaws.com:8080/NCAB/EmployeeService/login-android";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        relativeLayout = findViewById(R.id.login_layout);
        sharedPreferences = context.getSharedPreferences(MY_PREFERENCES,Context.MODE_PRIVATE);
        String shared_pref_userName = sharedPreferences.getString("user_name","");
        String shared_pref_password = sharedPreferences.getString("user_password","");

        if(!shared_pref_userName.isEmpty() && !shared_pref_password.isEmpty()){
            startActivity(new Intent(Login.this,Dashboard.class));
          //  startActivity(new Intent(Login.this, Splash.class));
         }

        String copyStr = getResources().getString(R.string.login_copy);
        TextView copyTV = findViewById(R.id.copy_text);

        //copyTV.setText(Html.fromHtml(copyStr, 0));

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
                checkCon=checkConnection(Login.this);
                if(checkCon){
                    //if condition to validate if the input is
                    // entered or not and internet connection is avalible
                    if (validate(user.getText().toString(),pass.getText().toString())) {
                        progressDialog = new ProgressDialog(Login.this,0);
                        progressDialog.setTitle("Logging in..");
                        progressDialog.setMessage("Please Wait");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        qlid = user.getText().toString();
                        //json for request
                        jsonBodyRequest = new JSONObject();
                        try {

                            jsonBodyRequest.put("qlid",user.getText().toString());
                            jsonBodyRequest.put("password", pass.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        firebaseToken = FirebaseInstanceId.getInstance().getToken();
                        Log.d(TAG, "firebase Token:- "+firebaseToken);
                        doLogin(jsonBodyRequest);
                    } else {

                    }
                }
                else
                {
                    final AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                    alertDialog.setTitle("No Connection Available");
                    alertDialog.setMessage("Please Connect to the Internet");
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.cancel();
                        }
                    });
                    alertDialog.show();
                }

            }
        });

    }

    private void parseJSON(JSONObject response) {

        Log.d(TAG, "parseJSON: Response:- "+response);

        try {

            //<editor-fold desc="Get Driver details">
            JSONObject driverDetails = response.getJSONObject("driverDetails");
            if(driverDetails.length() == 0){
                sharedPreferences = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("DRIVERNAME", " ");
                editor.putString("DRIVERCONTACTNUMBER","");
                editor.apply();
            }else {
                String driverName = driverDetails.getString("driverName");
                String driverContactNumber = driverDetails.getString("driverContact");
                sharedPreferences = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("DRIVERNAME", driverName);
                editor.putString("DRIVERCONTACTNUMBER", driverContactNumber);
                editor.apply();
            }

            //</editor-fold>
            EmployeeQlID = response.getString("empQlid").toUpperCase();
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
            for (int i = 0;i< cabMates.length();i++){

                try {
                    JSONObject cabMateJSON = cabMates.getJSONObject(i);
                    String CabMate_Qlid = cabMateJSON.getString("Qlid").toUpperCase();
                    String CabMate_name = cabMateJSON.getString("f_name")+" "+cabMateJSON.getString("l_name");
                    String CabMate_contactNumber = cabMateJSON.getString("e_mob");
                    String CabMate_address = cabMateJSON.getString("p_a");
                    String CabMate_cabNumbr = cabMateJSON.getString("Cab_number");
                    String CabMate_roasterId = "";//cabMateJSON.getString("roster_id");
                    String CabMate_routeNumber = cabMateJSON.getString("Route_number");
                    String CabMate_pickupTime = cabMateJSON.getString("pickup_time");
                    String CabMate_shiftId = cabMateJSON.getString("shift_id");
                    ContentValues cabMateValues = new ContentValues();
                    cabMateValues.put(CabMatesContract.COLUMN_CABMATE_QLID,CabMate_Qlid);
                    cabMateValues.put(CabMatesContract.COLUMN_CABMATE_NAME,CabMate_name);
                    cabMateValues.put(CabMatesContract.COLUMN_CABMATE_CONTACT_NUMBER,CabMate_contactNumber);
                    cabMateValues.put(CabMatesContract.COLUMN_CABMATE_ADDRESS,CabMate_address);
                    cabMateValues.put(CabMatesContract.COLUMN_CABMATE_PICKUPTIME,CabMate_pickupTime);
                    cabMateValues.put(CabMatesContract.COLUMN_CABMATE_CAB_NUMBER,CabMate_cabNumbr);
                    cabMateValues.put(CabMatesContract.COLUMN_CABMATE_ROUTE_NUMBER,CabMate_routeNumber);
                    cabMateValues.put(CabMatesContract.COLUMN_CABMATE_SHIFT_ID,CabMate_shiftId);
                    cabMateValues.put(CabMatesContract.COLUMN_CABMATE_ROASTER_Id,CabMate_roasterId);

                    mSqLiteDatabase.insert(CabMatesContract.DB_TABLE,null,cabMateValues);
                } catch (JSONException e) {
                    e.printStackTrace();

                    Log.d(TAG, "parseJSON: Data Inserted to Cabmate Table row :- "+i);
                }

            }


            JSONArray contactSos = response.getJSONArray("contacts");
            for(int i = 0;i<contactSos.length();i++){

                JSONObject contactSOSObject = contactSos.getJSONObject(i);

                String contactSOS = contactSOSObject.getString("contactSos");
                String contactId = contactSOSObject.getString("contactId");
                String contactName = contactSOSObject.getString("contactName");
                String contactSosPriority =contactSOSObject.getString("contactSosPriority");
                String contactNumber = contactSOSObject.getString("contactNbr");
                String contactRole = contactSOSObject.getString("contactRole");
                ContentValues contactValues = new ContentValues();

                contactValues.put(ContactsContract.COLUMN_CONTACT_ID,contactId);
                contactValues.put(ContactsContract.COLUMN_CONTACT_SOS,contactSOS);
                contactValues.put(ContactsContract.COLUMN_CONTACT_NAME,contactName);
                contactValues.put(ContactsContract.COLUMN_CONTACT_SOS_PRIORITY,contactSosPriority);
                contactValues.put(ContactsContract.COLUMN_CONTACT_NUMBER,contactNumber);
                contactValues.put(ContactsContract.COLUMN_CONTACT_ROLE,contactRole);
                mSqLiteDatabase.insert(ContactsContract.DB_TABLE,null,contactValues);
    }

            JSONArray shiftInfo = response.getJSONArray("shiftInfo");

            for (int i  =0;i<shiftInfo.length();i++){
                JSONObject shiftTableInfo = shiftInfo.getJSONObject(i);
                int shiftId =  shiftTableInfo.getInt("shiftId");
                String shiftName = shiftTableInfo.getString("shiftName");
                String startTime = shiftTableInfo.getString("startTime");
                String endTime = shiftTableInfo.getString("endTime");
                String ShiftName = shiftTableInfo.getString("shiftName");
                ContentValues shiftInfoValues = new ContentValues();

                shiftInfoValues.put(ShiftContract.COLUMN_SHIFT_ID,shiftId);
                shiftInfoValues.put(ShiftContract.COLUMN_SHIFT_NAME,shiftName);
                shiftInfoValues.put(ShiftContract.COLUMN_START_TIME,startTime);
                shiftInfoValues.put(ShiftContract.COLUMN_END_TIME,endTime);
                shiftInfoValues.put(ShiftContract.COLUMN_SHIFT_NAME,ShiftName);
                 mSqLiteDatabase.insert(ShiftContract.DB_TABLE,null,shiftInfoValues);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public void doLogin(JSONObject jsonBodyRequest){

        Log.d(TAG, "doLogin: qlid: " + qlid);
        FirebaseTokenUtility ftu = new FirebaseTokenUtility(Login.this);
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, mainUrl, jsonBodyRequest,
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
//                              editor.putString("user_qlid",user.getText().toString());
                                editor.putString("user_name",
                                        response.getString("empFName")+" "+
                                                response.getString("empMName")+" "+
                                                response.getString("empLName"));
                                editor.putString("user_qlid",qlid);
                                editor.putString("user_password",pass.getText().toString());
                                editor.apply();
                                //</editor-fold>
                                progressDialog.cancel();
                                startActivity(intent);
                                Snackbar snackbar = Snackbar.make(relativeLayout,"Welcome",Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            } else {
                                progressDialog.cancel();
                                final AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                                alertDialog.setTitle("Alert");
                                alertDialog.setMessage("Invalid Credentials.");
                                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                                alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alertDialog.cancel();
                                    }
                                });
                                alertDialog.show();
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

    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) { // connected to the internet
            //Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();

            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }
    public boolean validate(String username,String password){
        if(username.isEmpty() || password.isEmpty()){
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.setMessage("Enter Credentials");
            alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.cancel();
                }
            });
            alertDialog.show();
            return false;
        }
        else
            return true;
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