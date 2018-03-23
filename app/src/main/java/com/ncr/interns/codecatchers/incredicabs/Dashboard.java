package com.ncr.interns.codecatchers.incredicabs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ncr.interns.codecatchers.incredicabs.Adapter.*;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.CabMatesContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.EmployeeCabMatesDetails;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.NcabSQLiteHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    LinearLayout linearLayout;
    RecyclerView mRecyclerView;
    ArrayList<EmployeeCabMatesDetails> mList;
    Button checkIn, checkOut, Complaints, request;
    SQLiteDatabase mSqLiteDatabase;
    NcabSQLiteHelper ncabSQLiteHelper;
    Cursor cursor;
    Button button_sos;
    String number;
    SharedPreferences sharedPreferences;
    private static final String MY_PREFERENCES = "MyPrefs_login";
    Context context = this;
    private static final String TAG = "Dashboard Debugging";
    private static final int REQUEST_CALL = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ncabSQLiteHelper = new NcabSQLiteHelper(this);
        mSqLiteDatabase = ncabSQLiteHelper.getWritableDatabase();
        getIdofComponents();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        CabMatesAdapter adapter = new CabMatesAdapter(getCabmatesDetails(), this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(adapter);

       /* cabMatesNotification();//Abhishek Alarm manager
        getCabMateShiftTimeNew();
*/
        checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent checkIn_intent = new Intent(Dashboard.this, CheckIn.class);
                startActivity(checkIn_intent);

            }
        });

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent checkOut_intent = new Intent(Dashboard.this, CheckOut.class);
                startActivity(checkOut_intent);
            }
        });

        Complaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, FeedbackActivity.class);
                startActivity(intent);
            }
        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, MainRequestActivity.class);
                startActivity(intent);
            }
        });

        button_sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 3/20/2018 By Harshit pandey SOS implementation
                if(ContextCompat.checkSelfPermission(Dashboard.this, android.Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Dashboard.this, new String[] {android.Manifest.permission.CALL_PHONE},CustomDialogClass.REQUEST_CALL);
                }else{
//                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:9760179474")));
                    CustomDialogClass cdd=new CustomDialogClass(Dashboard.this);
                    cdd.show();
                    cdd.start();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.call_transport) {
            number = "9998764636";
            makePhoneCall(number);
        }
        if(id == R.id.refresh){
            // TODO: 3/23/2018 Yet to implement
            Snackbar snackbar = Snackbar.make(linearLayout,"Refresh",Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_call_transport_one) {
            number = "9998764636";
            makePhoneCall(number);
            // FIXME: 3/22/2018 Get the real number from DB

        } else if (id == R.id.nav_call_transport_two) {
            number = "7864648383";
            makePhoneCall(number);

        } else if (id == R.id.nav_app_feedback) {
            //<editor-fold desc="Implementation Hidden">
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, "gs250365@ncr.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Test");
            intent.putExtra(Intent.EXTRA_TEXT, " Test Test");
            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent, "Choose an email client"));
            //</editor-fold>

        } else if (id == R.id.nav_about_developers) {
           startActivity(new Intent(Dashboard.this,AboutPage.class));

        } else if (id == R.id.LogOut) {

            Intent intent = new Intent(this, Login.class);
            mSqLiteDatabase.execSQL("DELETE FROM " + CabMatesContract.DB_TABLE);
            sharedPreferences = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(intent);
            finish();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getIdofComponents() {
        button_sos = findViewById(R.id.button_sos);
        checkIn = findViewById(R.id.button_checkIn);
        checkOut = findViewById(R.id.button_checkOut);
        Complaints = findViewById(R.id.button_complaint);
        request = findViewById(R.id.button_request);
        linearLayout = findViewById(R.id.dashboard_linerarParent);
        mRecyclerView = findViewById(R.id.recyclerView);

    }

    //<editor-fold desc="Function to get the Current Cab Mates Details from the Database">
    public Cursor getCabmatesDetails() {
        cursor = mSqLiteDatabase.rawQuery("SELECT * FROM " + CabMatesContract.DB_TABLE, null);
        return cursor;
    }
    //</editor-fold>

    //<editor-fold desc="Function to make the phone call">
    private void makePhoneCall(String number) {
        //String number = mEditTextNumber.getText().toString();
        if (true) {

            if (ContextCompat.checkSelfPermission(Dashboard.this,
                    android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Dashboard.this,
                        new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        } else {
            Toast.makeText(context, "There might be Some Problem..", Toast.LENGTH_SHORT).show();
            //Toast.makeText(Dashboard.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Function overRided to ask permission">
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CabMatesAdapter adapter = new CabMatesAdapter();
            makePhoneCall(number);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //</editor-fold>

   /* //<editor-fold desc="Method to get CabMatesShift Time">
    public String[] getCabMatesShiftTime() {
        Cursor cursorShiftTime = mSqLiteDatabase.rawQuery("SELECT * FROM " + CabMatesContract.DB_TABLE, null);
        int len = cursor.getCount();
        String shiftTimes_array[] = new String[len];
        int counter = 0;
        while (cursorShiftTime.moveToNext()) {
            String shiftTime = cursorShiftTime.getString(cursorShiftTime.getColumnIndex
                    (CabMatesContract.COLUMN_CABMATE_PICKUPTIME));
            shiftTimes_array[counter] = shiftTime;
            counter++;
        }
        return shiftTimes_array;
    }
    //</editor-fold>

    public void getCabMateShiftTimeNew(){
       *//* Cursor cursor = mSqLiteDatabase.rawQuery("SELECT "+CabMatesContract.COLUMN_CABMATE_PICKUPTIME+" FROM "
        +CabMatesContract.DB_TABLE+" WHERE ",null)*//*


       Cursor cursor = mSqLiteDatabase.query(CabMatesContract.DB_TABLE,
               new String[]{CabMatesContract.COLUMN_CABMATE_PICKUPTIME},
               CabMatesContract.COLUMN_CABMATE_QLID+ "="+getEmployeeQlid(),
               null,null,null,null,null);

       while (cursor.moveToNext()){
          String pickup_time =  cursor.getString(cursor.getColumnIndex(CabMatesContract.COLUMN_CABMATE_PICKUPTIME));
           Log.d(TAG, "getCabMateShiftTimeNew: PickupTime:- "+pickup_time);
       }
    }

    //<editor-fold desc="Method to send System notification to The Users">
    public void cabMatesNotification() {

        String[] cabMatesShiftTime = getCabMatesShiftTime();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < cabMatesShiftTime.length; i++) {
            String shiftTime = cabMatesShiftTime[i];
            Log.d(TAG, "cabMatesNotification: shiftTime: " + shiftTime);
            String arr[] = shiftTime.split(":");
            String hour = arr[0];
            Log.d(TAG, "cabMatesNotification: hour: - "+hour);
            String min = arr[1];
            Log.d(TAG, "cabMatesNotification: min: - "+min);
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            calendar.set(Calendar.MINUTE, Integer.parseInt(min));
            Intent intent1 = new Intent(Dashboard.this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(Dashboard.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) Dashboard.this.getSystemService(ALARM_SERVICE);
            assert am != null;
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }
 }
    //</editor-fold>
*/
    public String getEmployeeQlid(){
        sharedPreferences = getSharedPreferences(MY_PREFERENCES,Context.MODE_PRIVATE);
        String Employee_Qlid = sharedPreferences.getString("user_qlid","");
        return Employee_Qlid;
    }

}
