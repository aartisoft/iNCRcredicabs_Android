package com.ncr.interns.codecatchers.incredicabs.Dashboard;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ncr.interns.codecatchers.incredicabs.Adapter.*;
import com.ncr.interns.codecatchers.incredicabs.CabRequests.MainRequestActivity;
import com.ncr.interns.codecatchers.incredicabs.CheckinCheckOut.CheckIn;
import com.ncr.interns.codecatchers.incredicabs.CheckinCheckOut.CheckOut;
import com.ncr.interns.codecatchers.incredicabs.Feedback.FeedbackActivity;
import com.ncr.interns.codecatchers.incredicabs.Login.Login;
import com.ncr.interns.codecatchers.incredicabs.NCABUtils.Environment;
import com.ncr.interns.codecatchers.incredicabs.NCABUtils.RESTService;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.CabMatesContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.ContactsContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.EmployeeContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.NcabSQLiteHelper;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.ShiftContract;
import com.ncr.interns.codecatchers.incredicabs.R;
import com.ncr.interns.codecatchers.incredicabs.Requests.RequestNotifications;
import com.ncr.interns.codecatchers.incredicabs.SOS.CustomDialogClass;
import com.ncr.interns.codecatchers.incredicabs.Splash.AboutPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    LinearLayout linearLayout;
    RecyclerView mRecyclerView;
    Button checkIn, checkOut, Complaints, request, button_sos;
    SQLiteDatabase mSqLiteDatabase;
    NcabSQLiteHelper ncabSQLiteHelper;
    Cursor cursor;
    String number, Pickuptime = "14:10";
    String Employee_Qlid, Employee_Name, Employee_Contact_number, Cab_number;
    String Employee_HomeAddress, DriverName, DriverContactNumber;
    CabMatesAdapter adapter;
    JSONObject jsonObject;
    String Route_No = null, Pickup_Time = null, Start_Time = null, End_Time = null;
    boolean checkCon;
    String query = "select a.cabmatepickuptime, a.routenumber, a.roasterid, a.shiftid, b.starttime, b.endtime  from CabMatesDetails a, ShiftTable b where a.CabMateQlid = ? and a.shiftid = b.shiftid";
    String mainUrl = "http://ec2-18-219-151-75.us-east-2.compute.amazonaws.com:8080/NCAB/AndroidService/RoasterDetailsByEmpID"; //
    String loginUrl = Environment.URL_LOGIN;
    SharedPreferences sharedPreferences;
    private static final String MY_PREFERENCES = "MyPrefs_login";
    Context context = this;
    private static final String TAG = "Dashboard Debugging";
    private static final int REQUEST_CALL = 1;
    TextView Emp_QLID_textView, Emp_Name_textView, Emp_HomeAddress_textView, Emp_ContactNum_textView;
    TextView Current_shift, textView_NOcabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ncabSQLiteHelper = new NcabSQLiteHelper(this);
        mSqLiteDatabase = ncabSQLiteHelper.getWritableDatabase();
        getIdofComponents();
        getEmployeeData();
        getDriverDetails();
        getCabNumber();
        Current_shift = findViewById(R.id.textView_currentShift);
        Emp_QLID_textView = findViewById(R.id.Emp_QLid);
        Emp_Name_textView = findViewById(R.id.Emp_Name);
        textView_NOcabs = findViewById(R.id.textView_NOcabs);
        Emp_HomeAddress_textView = findViewById(R.id.Emp_homeAddress);
        Emp_ContactNum_textView = findViewById(R.id.Emp_contactNumber);
        Emp_QLID_textView.setText(String.format("Emp.ID: %s", Employee_Qlid));
        Emp_Name_textView.setText(String.format("Emp Name: %s", Employee_Name));
        Emp_HomeAddress_textView.setText(String.format("Current Address : %s", Employee_HomeAddress));
        Emp_ContactNum_textView.setText(String.format("Contact Number:- %s", Employee_Contact_number));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        Cursor cursor = getCabMatesDetails();
        if (!cursor.moveToPosition(1)) {

            //    Toast.makeText(this, "No Data is recycler view", Toast.LENGTH_SHORT).show();
            textView_NOcabs.setVisibility(View.VISIBLE);

        } else {
            textView_NOcabs.setVisibility(View.GONE);
            adapter = new CabMatesAdapter(getCabMatesDetails(), this);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            mRecyclerView.setAdapter(adapter);
        }
        checkCon = checkConnection(Dashboard.this);

        //<editor-fold desc="Get teh data from SQLite database">
        // final String query = "select a.cabmatepickuptime, a.routenumber, a.roasterid, a.shiftid, b.starttime, b.endtime  from CabMatesDetails a, ShiftTable b where a.CabMateQlid = ? and a.shiftid = b.shiftid";

        Cursor c = mSqLiteDatabase.rawQuery(query, new String[]{Employee_Qlid.toUpperCase()});
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Pickup_Time = c.getString(c.getColumnIndex(CabMatesContract.COLUMN_CABMATE_PICKUPTIME));
            Start_Time = c.getString(c.getColumnIndex(ShiftContract.COLUMN_START_TIME));
            Route_No = c.getString(c.getColumnIndex(CabMatesContract.COLUMN_CABMATE_ROUTE_NUMBER));
            End_Time = c.getString(c.getColumnIndex(ShiftContract.COLUMN_END_TIME));
            c.moveToNext();
        }

if (Start_Time == null) {
            Current_shift.setText("You're not in any Cabs");
        } else {
            String currentShift = "Current Shift is " + Start_Time + " to " + End_Time;
            Current_shift.setText(currentShift);
        }


        checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //<editor-fold desc="Code to CheckIn">
                if (Start_Time == null) {
                    Snackbar snackbar = Snackbar.make(linearLayout, "You Cannnot CheckIn You don't have a ride Scheduled", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }

                if (checkCon) {
                    Intent checkIn_intent = new Intent(Dashboard.this, CheckIn.class);
                    checkIn_intent.putExtra("pickup", Pickup_Time);
                    checkIn_intent.putExtra("start_time", Start_Time);
                    checkIn_intent.putExtra("route_no", Route_No);
                    startActivity(checkIn_intent);

                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(Dashboard.this).create();
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
                //</editor-fold>
            }
        });

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //<editor-fold desc="Code to CheckOut">
                if (Start_Time == null) {
                    Snackbar snackbar = Snackbar.make(linearLayout, "You Cannnot CheckOut You don't have a ride Scheduled", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }

                if (checkCon) {
                    Intent checkOut_intent = new Intent(Dashboard.this, CheckOut.class);
                    checkOut_intent.putExtra("pickup", Pickup_Time);
                    checkOut_intent.putExtra("start_time", Start_Time);
                    checkOut_intent.putExtra("route_no", Route_No);
                    startActivity(checkOut_intent);

                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(Dashboard.this).create();
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
                //</editor-fold>
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

                if (ContextCompat.checkSelfPermission(Dashboard.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Dashboard.this, new String[]{android.Manifest.permission.CALL_PHONE}, CustomDialogClass.REQUEST_CALL);
                } else {
                    CustomDialogClass cdd = new CustomDialogClass(Dashboard.this, context);
                    cdd.show();
                    cdd.start();
                }
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        jsonObject = new JSONObject();
        if (id == R.id.call_driver) {
            showDriverDetailsDialogBox();
        }

        if (id == R.id.call_transport) {
            number = "9953122087";
            makePhoneCall(number);
        }
        if (id == R.id.refresh) {
            //<editor-fold desc="Refresh Button">
            /**
             * Getting the User's QLid and password from shared Preferences for hitting the LogIn APi and getting
             * the Appropriate DATA
             */
            sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
            String userQlid = sharedPreferences.getString("user_qlid", "");
            String userPassword = sharedPreferences.getString("user_password", "");
            try {
                jsonObject.put("qlid", userQlid);
                jsonObject.put("password", userPassword);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, loginUrl, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //                          Log.i("VOLLEY", "inside onResponse method: Login");
//                            Log.i("VOLLEY", response.toString());
                            try {
                                if (response.getString("success").equalsIgnoreCase("true")) {
                                    // Code for Changing the existing view of Dashboard
                                    mSqLiteDatabase.execSQL("DELETE FROM " + CabMatesContract.DB_TABLE);
                                    mSqLiteDatabase.execSQL("DELETE FROM " + ShiftContract.DB_TABLE);
                                    Log.d(TAG, "Gaurav >>>> onResponse: StartTime:- " + Start_Time);
                                    Log.d(TAG, "onResponse: Data deleted from Sqlite");
                                    parseJSON(response);
                                    //     Updating the details of users Cabmates
                                    //     adapter = new CabMatesAdapter(getCabMatesDetails(), Dashboard.this);
                                    //     adapter.notifyDataSetChanged();
                                    // TODO: 6/4/2018 Update the Shift timings too
                                    Start_Time = null;
                                    End_Time = null;
                                    Cursor c = mSqLiteDatabase.rawQuery(query, new String[]{Employee_Qlid.toUpperCase()});
                                    c.moveToFirst();
                                    while (!c.isAfterLast()) {
                                        Pickup_Time = c.getString(c.getColumnIndex(CabMatesContract.COLUMN_CABMATE_PICKUPTIME));
                                        Start_Time = c.getString(c.getColumnIndex(ShiftContract.COLUMN_START_TIME));
                                        Route_No = c.getString(c.getColumnIndex(CabMatesContract.COLUMN_CABMATE_ROUTE_NUMBER));
                                        End_Time = c.getString(c.getColumnIndex(ShiftContract.COLUMN_END_TIME));
                                        c.moveToNext();
                                    }

                                    if (Start_Time == null) {
                                        Current_shift.setText("You're not in any Cabs");
                                    } else {
                                        String currentShift = "Current Shift is " + Start_Time + " to " + End_Time;
                                        Current_shift.setText(currentShift);
                                    }
                                    Cursor cursor = getCabMatesDetails();
                                    if (!cursor.moveToPosition(1)) {

                                        //    Toast.makeText(this, "No Data is recycler view", Toast.LENGTH_SHORT).show();
                                        textView_NOcabs.setVisibility(View.VISIBLE);
                                        mRecyclerView.setVisibility(View.GONE);

                                    } else {
                                        textView_NOcabs.setVisibility(View.GONE);
                                        mRecyclerView.setVisibility(View.VISIBLE);
                                        adapter = new CabMatesAdapter(getCabMatesDetails(), Dashboard.this);
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Dashboard.this);
                                        mRecyclerView.setLayoutManager(linearLayoutManager);
                                        mRecyclerView.addItemDecoration(new DividerItemDecoration(Dashboard.this, DividerItemDecoration.VERTICAL));
                                        mRecyclerView.setAdapter(adapter);

                                    }
                                    Toast.makeText(context, "Cab Mates Details Updated", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onResponse: Refresh Done");
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
                            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    });

            RESTService.getInstance(Dashboard.this).addToRequestQueue(jsonObjRequest);
            //</editor-fold>
        }

        return super.onOptionsItemSelected(item);
    }

    private void parseJSON(JSONObject response) {

        Log.d(TAG, "parseJSON: Response:- " + response);

        try {
            //<editor-fold desc="Parse JSON method">

            JSONArray shiftInfo = response.getJSONArray("shiftInfo");

            for (int i = 0; i < shiftInfo.length(); i++) {
                JSONObject shiftTableInfo = shiftInfo.getJSONObject(i);
                int shiftId = shiftTableInfo.getInt("shiftId");
                String shiftName = shiftTableInfo.getString("shiftName");
                String startTime = shiftTableInfo.getString("startTime");
                String endTime = shiftTableInfo.getString("endTime");
                String ShiftName = shiftTableInfo.getString("shiftName");
                ContentValues shiftInfoValues = new ContentValues();

                shiftInfoValues.put(ShiftContract.COLUMN_SHIFT_ID, shiftId);
                shiftInfoValues.put(ShiftContract.COLUMN_SHIFT_NAME, shiftName);
                shiftInfoValues.put(ShiftContract.COLUMN_START_TIME, startTime);
                shiftInfoValues.put(ShiftContract.COLUMN_END_TIME, endTime);
                shiftInfoValues.put(ShiftContract.COLUMN_SHIFT_NAME, ShiftName);
                mSqLiteDatabase.insert(ShiftContract.DB_TABLE, null, shiftInfoValues);
            }


            JSONArray cabMates = response.getJSONArray("rosterInfo");
            for (int i = 0; i < cabMates.length(); i++) {

                try {
                    JSONObject cabMateJSON = cabMates.getJSONObject(i);
                    String CabMate_Qlid = cabMateJSON.getString("Qlid").toUpperCase();
                    String CabMate_name = cabMateJSON.getString("f_name") + " " + cabMateJSON.getString("l_name");
                    String CabMate_contactNumber = cabMateJSON.getString("e_mob");
                    String CabMate_address = cabMateJSON.getString("p_a");
                    String CabMate_shiftId = cabMateJSON.getString("shift_id");
                    String CabMate_routeNumber = cabMateJSON.getString("Route_number");
                    String CabMate_pickupTime = cabMateJSON.getString("pickup_time");
//
                    ContentValues cabMateValues = new ContentValues();
                    cabMateValues.put(CabMatesContract.COLUMN_CABMATE_QLID, CabMate_Qlid);
                    cabMateValues.put(CabMatesContract.COLUMN_CABMATE_NAME, CabMate_name);
                    cabMateValues.put(CabMatesContract.COLUMN_CABMATE_SHIFT_ID, CabMate_shiftId);
                    cabMateValues.put(CabMatesContract.COLUMN_CABMATE_ROUTE_NUMBER, CabMate_routeNumber);
                    cabMateValues.put(CabMatesContract.COLUMN_CABMATE_PICKUPTIME, CabMate_pickupTime);
                    cabMateValues.put(CabMatesContract.COLUMN_CABMATE_CONTACT_NUMBER, CabMate_contactNumber);
                    cabMateValues.put(CabMatesContract.COLUMN_CABMATE_ADDRESS, CabMate_address);
                    mSqLiteDatabase.insert(CabMatesContract.DB_TABLE, null, cabMateValues);
                    adapter = new CabMatesAdapter(getCabMatesDetails(), Dashboard.this);
                    mRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();

                    Log.d(TAG, "parseJSON: Data Inserted to Cabmate Table row :- " + i);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_call_transport_one) {
            number = "9953122087";
            makePhoneCall(number);
            // FIXME: 3/22/2018 Get the real number from DB

        } else if (id == R.id.nav_call_transport_two) {
            number = "9953695187";
            makePhoneCall(number);

        } else if (id == R.id.nav_app_feedback) {
            //<editor-fold desc="Implementation Hidden">
          /*  Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, "gs250365@ncr.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Test");
            intent.putExtra(Intent.EXTRA_TEXT, " Test Test");
            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent, "Choose an email client"));*/
            //</editor-fold>

        } else if (id == R.id.nav_about_developers) {
            startActivity(new Intent(Dashboard.this, AboutPage.class));

        } else if (id == R.id.nav_requests) {
            startActivity(new Intent(Dashboard.this, RequestNotifications.class));
            ;
        } else if (id == R.id.LogOut) {

            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Do You Want to Log Out?");
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(context, Login.class);
                    mSqLiteDatabase.execSQL("DELETE FROM " + EmployeeContract.DB_TABLE);
                    mSqLiteDatabase.execSQL("DELETE FROM " + CabMatesContract.DB_TABLE);
                    mSqLiteDatabase.execSQL("DELETE FROM " + ShiftContract.DB_TABLE);
                    mSqLiteDatabase.execSQL("DELETE FROM " + ContactsContract.DB_TABLE);
                    sharedPreferences = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    startActivity(intent);
                    finish();
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.cancel();
                }
            });
            alertDialog.show();

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
    public Cursor getCabMatesDetails() {
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

    //<editor-fold desc="Method to get CabMatesShift Time">
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

    //<editor-fold desc="method CabmatesNotification">
    public void cabMatesNotification(String Pickuptim) {
        String cabMatesShiftTime = Pickuptim;
        String[] time = cabMatesShiftTime.split(":");
        int hour = Integer.parseInt(time[0].trim());
        int min = Integer.parseInt(time[1].trim());
        if (min >= 30)
            min -= 30;
        else {
            hour -= 1;
            min += 30;
        }
        Log.e("my log alarm", "h- " + hour + "m-" + min);
        //Toast.makeText(context, "h- "+hour+"m-"+min, Toast.LENGTH_SHORT).show();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        Intent intent1 = new Intent(Dashboard.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Dashboard.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) Dashboard.this.getSystemService(ALARM_SERVICE);
        assert am != null;
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


    }
    //</editor-fold>

    //<editor-fold desc="OnStart Method of Dashboard Activity">
    @Override
    protected void onStart() {
        if (getSharedPreferences(null, MODE_PRIVATE).getBoolean("alarm", true)) {
            gettingPickuptime();
            getSharedPreferences(null, MODE_PRIVATE).edit().putBoolean("alarm", false).apply();
        }


        super.onStart();

    }
    //</editor-fold>

    //<editor-fold desc="gettingPickupTime">
    private void gettingPickuptime() {
        final JSONObject[] js = new JSONObject[1];
        final String[] pickuptime = new String[1];
        JSONObject jsonBodynot = new JSONObject();
        try {
            String Emp_qlid = getEmployeeQlid();
            jsonBodynot.put("Emp_Qlid", Emp_qlid);
            Log.d(TAG, "gettingPickuptime: Emp_QLID:- " + Emp_qlid);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, mainUrl, jsonBodynot, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("VOLLEY", "inside onResponse method:UnscheduledRequest");
                Log.i("VOLLEY", response.toString());
                try {
                    js[0] = new JSONObject(response.getString("result"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    if (js[0].getString("Pickup_Time") != "") {
                        pickuptime[0] = js[0].getString("Pickup_Time");
                        Pickuptime = pickuptime[0];
                        cabMatesNotification(Pickuptime);

                        // Toast.makeText(Dashboard.this, "Your request is Submitted", Toast.LENGTH_LONG).show();
                    } else {
                        cabMatesNotification("8:30");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Do something when error occurred
                Log.d("VOLLEY", "Something went wrong");
                //  Toast.makeText(getActivity(), "Oops..Something Went wrong", Toast.LENGTH_SHORT).show();

                error.printStackTrace();
            }
        });

        RESTService.getInstance(Dashboard.this).addToRequestQueue(jsonObjRequest);


    }
    //</editor-fold>

    //<editor-fold desc="Method to get Employeee Qlid">
    public String getEmployeeQlid() {
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String Employee_Qlid = sharedPreferences.getString("user_qlid", "");
        return Employee_Qlid;
    }
    //</editor-fold>

    //<editor-fold desc="Method to get Driver Details">
    public void getDriverDetails() {
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        DriverName = sharedPreferences.getString("DRIVERNAME", "");
        DriverContactNumber = sharedPreferences.getString("DRIVERCONTACTNUMBER", "");
    }
    //</editor-fold>

    //<editor-fold desc="Method to get the Current Employee data from the database to show in dashboard">
    public void getEmployeeData() {
        Cursor c = mSqLiteDatabase.rawQuery("SELECT * FROM " + EmployeeContract.DB_TABLE, null);
        while (c.moveToNext()) {
            Employee_Qlid = c.getString(c.getColumnIndex(EmployeeContract.COLUMN_EMP_QLID));
            Employee_Name = c.getString(c.getColumnIndex(EmployeeContract.COLUMN_FIRST_NAME)) + " "
                    + c.getString(c.getColumnIndex(EmployeeContract.COLUMN_LAST_NAME));
            Employee_Contact_number = c.getString(c.getColumnIndex(EmployeeContract.COLUMN_CONTACT_NUMBER));
            Employee_HomeAddress = c.getString(c.getColumnIndex(EmployeeContract.COLUMN_HOME_ADDRESS));
        }
        c.close();

    }

    //</editor-fold>

    //<editor-fold desc="Method to get Cab Number">
    public void getCabNumber() {
        Cursor c = mSqLiteDatabase.rawQuery("SELECT * FROM " + CabMatesContract.DB_TABLE, null);
        while (c.moveToNext()) {
            Cab_number = c.getString(c.getColumnIndex(CabMatesContract.COLUMN_CABMATE_CAB_NUMBER));
            Log.d(TAG, "getCabNumber: " + Cab_number);
        }
    }
    //</editor-fold>

    //<editor-fold desc="ShowDriverDetailsDialogBox">
    public void showDriverDetailsDialogBox() {

        final Dialog dialog = new Dialog(Dashboard.this);
        dialog.setContentView(R.layout.driver_details_dialogue);
        dialog.setTitle("DRIVER DETAILS");
        dialog.show();
        TextView textView_driverName = dialog.findViewById(R.id.dialog_driver_name);
        TextView textView_driverContactNumber = dialog.findViewById(R.id.dialog_driver_number);
        LinearLayout driverDetailsLayout = dialog.findViewById(R.id.layout_driver_details);
        TextView driverDetailsNotAvailble = dialog.findViewById(R.id.driver_details_not_available);
        TextView textView_cabNumber = dialog.findViewById(R.id.dialog_cab_number);
        driverDetailsLayout.setVisibility(View.GONE);
        driverDetailsNotAvailble.setVisibility(View.GONE);

        if (DriverName.isEmpty() || DriverContactNumber.isEmpty()) {
            driverDetailsNotAvailble.setVisibility(View.VISIBLE);
        } else {
            driverDetailsLayout.setVisibility(View.VISIBLE);
            textView_driverName.setText(DriverName);
            textView_driverContactNumber.setText(DriverContactNumber);
            textView_cabNumber.setText(Cab_number);
        }
        Button button_callDriver = dialog.findViewById(R.id.dialog_call_driver);
        button_callDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall(DriverContactNumber);
                dialog.dismiss();
            }
        });


    }
    //</editor-fold>

    //<editor-fold desc="Check Internet Connection">
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
    //</editor-fold>


}