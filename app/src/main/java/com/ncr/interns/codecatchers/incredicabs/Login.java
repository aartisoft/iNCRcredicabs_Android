package com.ncr.interns.codecatchers.incredicabs;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.EmployeeCabMatesDetails;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.EmployeeContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.EmployeeData;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.NcabSQLiteHelper;

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
    int role;
    NcabSQLiteHelper ncabSQLiteHelper;
    SQLiteDatabase mSqLiteDatabase;
    private static final String TAG = "LOGIN_ACTIVITY";

    String url = "http://ec2-18-219-151-75.us-east-2.compute.amazonaws.com:8080/NCAB/EmployeeService/login-android";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = findViewById(R.id.editText_Qlid);
        pass = findViewById(R.id.editText_password);

        login = findViewById(R.id.button);

        //<editor-fold desc="Code to Create SqLite Database Table">
        ncabSQLiteHelper = new NcabSQLiteHelper(this);
        mSqLiteDatabase = ncabSQLiteHelper.getWritableDatabase();
        //</editor-fold>

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //json for request
                JSONObject jsonBodyRequest = new JSONObject();
                try {
                    jsonBodyRequest.put("qlid", user.getText().toString());
                    jsonBodyRequest.put("password", pass.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST,
                        url,
                        jsonBodyRequest,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                Log.i("VOLLEY", "inside onResponse method: Login");
                                Log.i("VOLLEY", response.toString());

                                try {
                                    if (response.getString("success").equalsIgnoreCase("true")) {
                                        Intent intent = new Intent(Login.this, Dashboard.class);
                                        parseJSON(response);
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
            //</editor-fold>


            //<editor-fold desc="Yet to Implement">
            /*JSONArray cabMates = response.getJSONArray("rosterInfo");
            for (int i = 0;i<cabMates.length();i++){

                JSONObject cabmateX = cabMates.getJSONObject(i);
                String CabMate_Qlid = cabmateX.getString("Qlid");
                String CabMate_name = cabmateX.getString("f_name")+" "+cabmateX.getString("l_name");
                String CabMate_contactNumber = cabmateX.getString("e_mob");
                String CabMate_address = cabmateX.getString("p_a");

                EmployeeCabMatesDetails matesDetails = new EmployeeCabMatesDetails(CabMate_name,
                        CabMate_address,"",CabMate_contactNumber,CabMate_Qlid);

            }
*/
            //</editor-fold>

            /*EmployeeData employeeData = new EmployeeData(EmployeeQlID,EmployeeFirstName,
                    "",EmployeeLastName,Level1ManagerQlid,Level2ManagerQlid,
                    Level2ManagerName,Level1ManagerName,HomeAddress,
                    "NCR Corporation, 5th Floor, Vipul Plaza, Suncity, Sector 54,Gurgoan",
                    contactNumber,
                    "",emergencyContactNumber,role);*/


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public Cursor getEmployeeDetails(){

        Cursor cursor = mSqLiteDatabase.rawQuery("SELECT * FROM "+EmployeeContract.DB_TABLE,null);
        return cursor;
    }
}