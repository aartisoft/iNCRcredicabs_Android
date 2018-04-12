package com.ncr.interns.codecatchers.incredicabs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.CabMatesContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.NcabSQLiteHelper;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.ShiftContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CheckOut extends AppCompatActivity {

    SQLiteDatabase mSqLiteDatabase;
    NcabSQLiteHelper ncabSQLiteHelper;
    private ZXingScannerView scannerView;
    String url = "http://ec2-18-219-151-75.us-east-2.compute.amazonaws.com:8080/NCAB/AndroidService/checkout";
    // String url_roasterinfo="http://ec2-18-219-151-75.us-east-2.compute.amazonaws.com:8080/NCAB/AndroidService/RoasterDetailsByEmpID";
    String Route_No;
    String Pickup_Time;
    String Start_Time;
    String Emp_Qlid;
    SharedPreferences sharedPreferences;
    JSONObject jsonBodyRequest = new JSONObject();
    private static final String MY_PREFERENCES = "MyPrefs_login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Emp_Qlid=getEmployeeQlid();


        Intent intent = getIntent();
        Pickup_Time = intent.getStringExtra("pickup");
        Start_Time = intent.getStringExtra("start_time");
        Route_No = intent.getStringExtra("route_no");
        ncabSQLiteHelper = new NcabSQLiteHelper(this);
        mSqLiteDatabase = ncabSQLiteHelper.getReadableDatabase();
        Emp_Qlid = getEmployeeQlid();

        openScanner();



    }

    private void openScanner() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(CheckOut.this);
        scanIntegrator.setPrompt("Scan a barcode");
        scanIntegrator.setBeepEnabled(true);
        scanIntegrator.setOrientationLocked(true);
        scanIntegrator.setBarcodeImageEnabled(true);
        scanIntegrator.setCaptureActivity(CustomScannerCheckOutActivity.class);
        scanIntegrator.initiateScan();


    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        /*As an example in order to get the content of what is scanned you can do the following*/
        if (scanningResult.getContents() != null) {
            if(Pickup_Time!=null && Start_Time!=null)
            {
                String scanContent = scanningResult.getContents().toString();
                Log.e("scanContent", scanContent);
                //   service_tag.setText(scanContent);
                JSONObject jsonBodyRequest = new JSONObject();
                try {
                    String split[]=Pickup_Time.split(":");
                    String split1[]=Start_Time.split(":");
                    int Pick_hour= Integer.parseInt(split[0]);
                    int Start_Time_Hour=Integer.parseInt(split1[0]);
                    DateFormat date_format=new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat date = new SimpleDateFormat("HH:mm");
                    Calendar rightNow = Calendar.getInstance();
                    int Hour= rightNow.get(Calendar.HOUR_OF_DAY);
                    int Minute=rightNow.get(Calendar.MINUTE);
                    jsonBodyRequest.put("Emp_Qlid", Emp_Qlid);
                    jsonBodyRequest.put("Trip_Date", date_format.format(rightNow.getTime()));
                    jsonBodyRequest.put("Check_out_Time", date.format(rightNow.getTime()));
                    jsonBodyRequest.put("QRcode",scanContent);
                    if(Hour>=Pick_hour && Hour<(Start_Time_Hour+1)) {
                        jsonBodyRequest.put("Trip_Type", "Pick");
                    }
                    else
                    {
                        jsonBodyRequest.put("Trip_Type", "Drop");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBodyRequest,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                JSONObject js = null;
                                try {
                                    String Check_out;
                                    js = new JSONObject(response.getString("result"));
                                    Check_out = js.getString("Check_out");
                                    if(Check_out.equals("Done"))
                                    {

                                        Toast.makeText(getApplicationContext(), "Successful Checkout", Toast.LENGTH_LONG).show();
                                    }
                                    else if(Check_out.equals("INCORRECT QRcode"))
                                    {
                                        Toast.makeText(getApplicationContext(), "INCORRECT QRcode", Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "Already Checked out", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                Log.i("VOLLEY", "inside onResponse method:doLogin");
                                Log.i("VOLLEY", response.toString());
                                //   Toast.makeText(CheckOut.this, "CheckOut Sucessful", Toast.LENGTH_SHORT).show();

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Do something when error occurred
                                Log.d("VOLLEY", "Something went wrong");
                                error.printStackTrace();                            }
                        });
                RESTService.getInstance(getApplicationContext()).addToRequestQueue(jsonObjRequest);
                finish();
                /*Intent Dashboard_intent= new Intent(getApplicationContext(),Dashboard.class);
                startActivity(Dashboard_intent);*/

            }
            else {
                Toast.makeText(getApplicationContext(),"CheckOut failed,Contact Admin",Toast.LENGTH_LONG).show();
            }

        }
        else
        {

            finish();
        }
    }
    public String getEmployeeQlid(){
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String Employee_Qlid = sharedPreferences.getString("user_qlid","");
        return Employee_Qlid;
    }

}
