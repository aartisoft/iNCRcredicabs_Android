package com.ncr.interns.codecatchers.incredicabs;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

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
    String Emp_Qlid="AN250279";
    String ipaddress="192.168.43.45:8080";
    String url = "http://"+ipaddress+"/iNCRcredicabs_WS/VendorService/checkout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner_check_out);
        barcodeScannerView = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);

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
                Pickup_Time="8:00";
                Start_Time="10:00:00";
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

