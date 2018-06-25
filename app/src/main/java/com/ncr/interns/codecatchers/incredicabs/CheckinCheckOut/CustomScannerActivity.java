package com.ncr.interns.codecatchers.incredicabs.CheckinCheckOut;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.ncr.interns.codecatchers.incredicabs.Dashboard.Dashboard;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.CabMatesContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.NcabSQLiteHelper;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.ShiftContract;
import com.ncr.interns.codecatchers.incredicabs.R;
import com.ncr.interns.codecatchers.incredicabs.NCABUtils.RESTService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class CustomScannerActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private CaptureManager capture;
    int torch_flag = 0;
    String Check_In = "";
    private static final String MY_PREFERENCES = "MyPrefs_login";
    SharedPreferences sharedPreferences;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageButton switchFlashlightButton;
    private Button manualEntryButton;
    String Route_No = null;
    String Pickup_Time = null;
    String Start_Time = null;
    String Emp_Qlid = null;
    SQLiteDatabase mSqLiteDatabase;
    NcabSQLiteHelper ncabSQLiteHelper;

    String url = "http://ec2-18-219-151-75.us-east-2.compute.amazonaws.com:8080/NCAB/AndroidService/checkin";
    JsonObjectRequest jsonObjRequest = null;
    JsonObjectRequest jsonObjRequest2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);


        ncabSQLiteHelper = new NcabSQLiteHelper(CustomScannerActivity.this);
        mSqLiteDatabase = ncabSQLiteHelper.getReadableDatabase();
        /*Emp_Qlid = getEmployeeQlid();*/

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);

        switchFlashlightButton = findViewById(R.id.switch_flashlight);
        manualEntryButton = findViewById(R.id.manualButton);


        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }
        manualEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                Emp_Qlid = sharedPreferences.getString("user_qlid", "");
                final String query = "select a.cabmatepickuptime, a.routenumber, a.roasterid, a.shiftid, b.starttime, b.endtime  from CabMatesDetails a, ShiftTable b where a.CabMateQlid = ? and a.shiftid = b.shiftid";
                Cursor c = mSqLiteDatabase.rawQuery(query, new String[]{getEmployeeQlid().toUpperCase()});
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    Pickup_Time = c.getString(c.getColumnIndex(CabMatesContract.COLUMN_CABMATE_PICKUPTIME));
                    Start_Time = c.getString(c.getColumnIndex(ShiftContract.COLUMN_START_TIME));
                    Route_No = c.getString(c.getColumnIndex(CabMatesContract.COLUMN_CABMATE_ROUTE_NUMBER));
                    c.moveToNext();
                }
                if (Pickup_Time != null && Start_Time != null && Route_No != null) {
                    JSONObject jsonBodyRequest = new JSONObject();
                    try {
                        DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat time_format = new SimpleDateFormat("HH:mm");
                        String split[] = Pickup_Time.split(":");
                        String split1[] = Start_Time.split(":");
                        int Pick_hour = Integer.parseInt(split[0]);
                        int Start_Time_Hour = Integer.parseInt(split1[0]);

                        Calendar rightNow = Calendar.getInstance();
                        System.out.println(rightNow);
                        int Hour = rightNow.get(Calendar.HOUR_OF_DAY);
                        System.out.println(Hour);
                        int Minute = rightNow.get(Calendar.MINUTE);
                        jsonBodyRequest.put("Emp_Qlid", Emp_Qlid);
                        jsonBodyRequest.put("Route_No", Route_No);
                        jsonBodyRequest.put("Trip_Date", date_format.format(rightNow.getTime()));
                        jsonBodyRequest.put("Check_in_Time", time_format.format(rightNow.getTime()));
                        jsonBodyRequest.put("Cab_Type", "manual");
                        jsonBodyRequest.put("QRcode", "null");
                        if (Hour >= Pick_hour && Hour <= Start_Time_Hour) {
                            jsonBodyRequest.put("Trip_Type", "Pick");
                        } else {

                            jsonBodyRequest.put("Trip_Type", "Drop");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST,
                            url,
                            jsonBodyRequest,
                            new Response.Listener<JSONObject>() {
                                JSONObject js = null;

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        js = new JSONObject(response.getString("result"));
                                        Check_In = js.getString("Check_In");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (Check_In.equals("Done")) {
                                        Toast.makeText(getApplicationContext(), "Successful Checkin", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Already Checkedin", Toast.LENGTH_LONG).show();
                                    }
                                    Log.i("VOLLEY", "inside onResponse method:doLogin");
                                    Log.i("VOLLEY", response.toString());


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
                    RESTService.getInstance(getApplicationContext()).addToRequestQueue(jsonObjRequest);

//                    Intent Dashboard_intent = new Intent(getApplicationContext(), Dashboard.class);
//                    startActivity(Dashboard_intent);
                }
            }

        });
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
       // finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    /**
     * Check if the device's camera has a Flashlight.
     *
     * @return true if there is Flashlight, otherwise false.
     */
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight(View view) {
        if (torch_flag == 0) {
            switchFlashlightButton.setBackgroundResource(R.drawable.flashlight_off);
            barcodeScannerView.setTorchOn();
            torch_flag = 1;
        } else {
            switchFlashlightButton.setBackgroundResource(R.drawable.flashlight_on);
            torch_flag = 0;
            barcodeScannerView.setTorchOff();
        }
    }

    @Override
    public void onTorchOn() {
        // switchFlashlightButton.setText(R.string.turn_off_flashlight);
    }

    @Override
    public void onTorchOff() {
        //   switchFlashlightButton.setText(R.string.turn_on_flashlight);
    }

    public String getEmployeeQlid() {
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String Employee_Qlid = sharedPreferences.getString("user_qlid", "");
        return Employee_Qlid;
    }
}
