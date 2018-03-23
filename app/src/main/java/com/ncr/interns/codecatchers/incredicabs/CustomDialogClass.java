/**
 * Created by gs250365 on 3/23/2018.
 */

package com.ncr.interns.codecatchers.incredicabs;

import android.*;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.ContactsContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.NcabSQLiteHelper;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.ContactsContract;

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
    Context mContext,appContext;
    MediaPlayer mp = MediaPlayer.create(getContext(),R.raw.alert);

    public CustomDialogClass(Activity a) {
        super(a);
        this.c = a;
    }

/*
    public CustomDialogClass(Context context) {
        mContext = context;
    }
*/

    public static final int REQUEST_CALL =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialogue);

        currentNum = 0;

//        NcabSQLiteHelper sqLiteHelper = new NcabSQLiteHelper(getContext());
//        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();
//        Cursor cur = sqLiteDatabase.rawQuery(
//                "SELECT ContactNumber FROM Contacts WHERE ContactSos != '0' " +
//                        "ORDER BY (ContactSosPriority)", null);
//        cur.moveToFirst();
//
//        numLength = cur.getCount();
//
//        numbers = new String[numLength];
//        for(int i=0; i<numLength; ++i){
//            numbers[i] = cur.getString(0);
//            cur.moveToNext();
//        }
        text = findViewById(R.id.txt_dia);
//       yes = (Button) findViewById(R.id.btn_yes);
        no = findViewById(R.id.btn_no);
//      yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_yes:
//                text.setText("Kon Hai?");
//                this.start();
//                c.finish();
//                break;
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
//        dismiss();
    }


    public void start() {
        text.setText("15");



        countDownTimer = new CountDownTimer(15  * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                AudioManager mgr = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                int valuess = 15;//range(0-15)
                mgr.setStreamVolume(AudioManager.STREAM_MUSIC, valuess, 0);
                text.setText("" + millisUntilFinished / 1000);          //Changing the text for Countdown.
                mp.start();                                             //For playing alert sound
            }

            @Override
            public void onFinish() {
                text.setText("SOS Triggered");
//                MainActivity mains = new MainActivity();
//                mains.makePhoneCall();
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

}