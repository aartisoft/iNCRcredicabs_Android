package com.ncr.interns.codecatchers.incredicabs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.CabMatesContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.NcabSQLiteHelper;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.ShiftContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomScannerCheckOutActivity extends AppCompatActivity  implements DecoratedBarcodeView.TorchListener{
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageButton switchFlashlightButton;
    private  Button manualEntryButton;
    String Route_No=null;
    int torch_flag=0;
    String Pickup_Time=null;
    String Start_Time=null;
    String Emp_Qlid=null;
    private static final String MY_PREFERENCES = "MyPrefs_login";
    SharedPreferences sharedPreferences;
    SQLiteDatabase mSqLiteDatabase;
    NcabSQLiteHelper ncabSQLiteHelper;

    // String ipaddress="192.168.43.45:8080";
    String url = "http://ec2-18-219-151-75.us-east-2.compute.amazonaws.com:8080/NCAB/AndroidService/checkout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner_check_out);
        barcodeScannerView = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);

        ncabSQLiteHelper = new NcabSQLiteHelper(CustomScannerCheckOutActivity.this);
        mSqLiteDatabase = ncabSQLiteHelper.getReadableDatabase();



        switchFlashlightButton = (ImageButton)findViewById(R.id.switch_flashlight);
        manualEntryButton= findViewById(R.id.manualButton);
        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }
        manualEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                Emp_Qlid=sharedPreferences.getString("user_qlid","");
                final String query = "select a.cabmatepickuptime, a.routenumber, a.roasterid, a.shiftid, b.starttime, b.endtime  from CabMatesDetails a, ShiftTable b where a.CabMateQlid = ? and a.shiftid = b.shiftid";
                Cursor c = mSqLiteDatabase.rawQuery(query, new String[]{Emp_Qlid.toUpperCase()});
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    Pickup_Time = c.getString(c.getColumnIndex(CabMatesContract.COLUMN_CABMATE_PICKUPTIME));
                    Start_Time = c.getString(c.getColumnIndex(ShiftContract.COLUMN_START_TIME));
                    Route_No = c.getString(c.getColumnIndex(CabMatesContract.COLUMN_CABMATE_ROUTE_NUMBER));
                    c.moveToNext();
                }
                if (Pickup_Time != null && Start_Time != null) {
                    JSONObject jsonBodyRequest = new JSONObject();
                    try {
                        String split[] = Pickup_Time.split(":");
                        String split1[] = Start_Time.split(":");
                        int Pick_hour = Integer.parseInt(split[0]);
                        int Start_Time_Hour = Integer.parseInt(split1[0]);
                        DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat date = new SimpleDateFormat("HH:mm");
                        Calendar rightNow = Calendar.getInstance();
                        int Hour = rightNow.get(Calendar.HOUR_OF_DAY);
                        int Minute = rightNow.get(Calendar.MINUTE);
                        jsonBodyRequest.put("Emp_Qlid", Emp_Qlid);
                        jsonBodyRequest.put("Trip_Date", date_format.format(rightNow.getTime()));
                        jsonBodyRequest.put("Check_out_Time", date.format(rightNow.getTime()));
                        if (Hour >= Pick_hour && Hour < (Start_Time_Hour + 1)) {
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
                                @Override
                                public void onResponse(JSONObject response) {

                                    Log.i("VOLLEY", "inside onResponse method:doLogin");
                                    Log.i("VOLLEY", response.toString());
                                    Toast.makeText(CustomScannerCheckOutActivity.this, "CheckOut Successfull", Toast.LENGTH_SHORT).show();


                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Do something when error occurred
                                    Log.d("VOLLEY", "Something went wrong");
                                    Toast.makeText(CustomScannerCheckOutActivity.this, "CheckOut UnSuccesfull", Toast.LENGTH_SHORT).show();
                                    error.printStackTrace();
                                }
                            });
                    RESTService.getInstance(getApplicationContext()).addToRequestQueue(jsonObjRequest);

                    Intent Dashboard_intent = new Intent(getApplicationContext(), Dashboard.class);
                    startActivity(Dashboard_intent);

                }
            }

        });
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
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
     * @return true if there is Flashlight, otherwise false.
     */
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight(View view) {
        if (torch_flag==0)
        {
            switchFlashlightButton.setBackgroundResource(R.drawable.flashlight_off);
            barcodeScannerView.setTorchOn();
            torch_flag=1;
        }
        else
        {
            switchFlashlightButton.setBackgroundResource(R.drawable.flashlight_on);
            barcodeScannerView.setTorchOff();
            torch_flag=0;
        }
    }
    @Override
    public void onTorchOn() {
        //switchFlashlightButton.setText(R.string.turn_off_flashlight);
    }

    @Override
    public void onTorchOff() {
        //switchFlashlightButton.setText(R.string.turn_on_flashlight);
    }
}

