/**
 * Created by gs250365 on 3/23/2018.
 */

package com.ncr.interns.codecatchers.incredicabs;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonObjectRequest;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.CabMatesContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.NcabSQLiteHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    private CountDownTimer countDownTimer;
    public TextView text;
    public Activity c;
    public Dialog d;
    private String numbers[];
    private Button no;
    private int currentNum;
    private int numLength;
    String EmpQlid;
    String RosterId;
    String CabNo;
    SharedPreferences sharedPreferences;
    SQLiteDatabase mSqLiteDatabase;
    NcabSQLiteHelper ncabSQLiteHelper;
    private static final String MY_PREFERENCES = "MyPrefs_login";
    Context mContext,appContext;
    String sosUrl = "";
    String Url = "http://ec2-18-219-151-75.us-east-2.compute.amazonaws.com:8080/" +
            "NCAB/EmployeeService/sos-trigger-android";
    MediaPlayer mp = MediaPlayer.create(getContext(),R.raw.alert);

    public CustomDialogClass(Activity a,Context context) {
        super(a);
        this.c = a;
        mContext = context;
    }

    public static final int REQUEST_CALL =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialogue);

        currentNum = 0;
        text = findViewById(R.id.txt_dia);
        no = findViewById(R.id.btn_no);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_no:
                this.cancel();
                countDownTimer.cancel();
                countDownTimer = null;
                text.setText(R.string.sos_cancelled);
                dismiss();
                break;
            default:
                break;
        }
    }


    public void start() {
        text.setText("15");

        countDownTimer = new CountDownTimer(15  * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                AudioManager mgr = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                int valuess = 10;//range(0-15)
                mgr.setStreamVolume(AudioManager.STREAM_MUSIC, valuess, 0);
                text.setText("" + millisUntilFinished / 1000);          //Changing the text for Countdown.
                mp.start();                                             //For playing alert sound
            }

            @Override
            public void onFinish() {
                text.setText("SOS Triggered");
                NcabSQLiteHelper sqLiteHelper = new NcabSQLiteHelper(getContext());
                SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();
                Cursor cur = sqLiteDatabase.rawQuery(
                        "SELECT ContactNumber FROM Contacts WHERE ContactSos != '0' " +
                                "ORDER BY (ContactSosPriority)", null);
                cur.moveToFirst();

                numLength = cur.getCount();

                numbers = new String[numLength];
                for(int i=0; i<numLength; ++i){
                    numbers[i] = cur.getString(0);
                    cur.moveToNext();
                }

               /* EmpQlid = getEmployeeQlid();
                ncabSQLiteHelper = new NcabSQLiteHelper(c);
                sqLiteDatabase = ncabSQLiteHelper.getWritableDatabase();
                final String query = "select a.cabmatepickuptime, a.routenumber, a.roasterid, a.shiftid, b.starttime, b.endtime  from CabMatesDetails a, ShiftTable b where a.CabMateQlid = ? and a.shiftid = b.shiftid";
                Cursor c = mSqLiteDatabase.rawQuery(query, new String[]{EmpQlid.toUpperCase()});
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    RosterId = c.getString(c.getColumnIndex(CabMatesContract.COLUMN_CABMATE_ROASTER_Id));
                    CabNo = c.getString(c.getColumnIndex(CabMatesContract.COLUMN_CABMATE_CAB_NUMBER));
                    c.moveToNext();
                }

                JSONObject jsonBodyRequest = new JSONObject();
                try {
                    jsonBodyRequest.put("empQlid",EmpQlid);
                    jsonBodyRequest.put("rosterId",RosterId);
                    jsonBodyRequest.put("cabLicensePlateNo",CabNo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                RESTService.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
*/
                if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) getContext(), new String[] {android.Manifest.permission.CALL_PHONE},CustomDialogClass.REQUEST_CALL);
                }else{
                    getContext().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+numbers[0])));
                }
                dismiss();
            }
//            cur.moveToFirst();


        };
        countDownTimer.start();

    }

    public void cancel() {
//        if (countDownTimer != null) {
//            countDownTimer.cancel();
//            countDownTimer = null;
//            text.setText(R.string.sos_cancelled);
//            dismiss();
//        }
    }

    public String nextNum(){
        if(this.currentNum+1 > numLength){
            return "";
        }

        String num = numbers[currentNum];
        currentNum++;
        return num;
    }

    public String getEmployeeQlid(){
        sharedPreferences = c.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String Employee_Qlid = sharedPreferences.getString("user_qlid","");
        return Employee_Qlid;
    }

}