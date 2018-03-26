package com.ncr.interns.codecatchers.incredicabs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ncr.interns.codecatchers.incredicabs.NCABUtils.DatePickerUtilClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FeedbackActivity extends AppCompatActivity {
    Spinner complaint_type_spinner, spinnerShiftType, spinnerDropType;

    EditText datePicker;
    JSONObject jsonBody;
    CoordinatorLayout mylayout;

    String selectedDate;
    String selectType;
    String selectPickupDropType;
    String complaintType;
    JsonObjectRequest jsonObjRequest;
    EditText comment;
    List<String> DropTypeList;
    List<String> ShiftTypeSpinner;
    List<String> ComplaintsTypeSpinner;
    JSONArray cabshift;
    String startDate;
    String tempdate;
    EditText shiftTiming;
    SharedPreferences sharedPreferences;
    EditText cabNumber;
    String Employee_Qlid;
    private static final String MY_PREFERENCES = "MyPrefs_login";
    private String url = "http://ec2-18-219-151-75.us-east-2.compute.amazonaws.com:8080/NCAB/RosterService/complaint";
    private String getShiftDetailsUrl = "http://ec2-18-219-151-75.us-east-2.compute.amazonaws.com:8080/NCAB/RosterService/getCabShift";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Employee_Qlid = getEmployeeQlid();
        setContentView(R.layout.activity_feedback);
        mylayout = findViewById(R.id.feedbacklayout);
        shiftTiming=findViewById(R.id.Shift_timing);
        cabNumber=findViewById(R.id.Text_cab);
        comment = findViewById(R.id.text_reasonForRequest);
        datePicker = findViewById(R.id.text_fromDate);
        datePicker.setOnClickListener(new View.OnClickListener() {
         @Override
            public void onClick(View view) {

                methodToSelectDate(datePicker);
            }

        });

        //<editor-fold desc="Code to set the toolbar">
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //</editor-fold>
        spinnerDropType = (Spinner) findViewById(R.id.options_drop);
        //  spinnerDropType.setOnItemSelectedListener(this);

        prepareSpinnerData();

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.custom_spinner, DropTypeList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDropType.setOnItemSelectedListener(new DropTypeSpinner());
        spinnerDropType.setAdapter(adapter2);
        spinnerDropType = findViewById(R.id.options_drop);
        spinnerShiftType = findViewById(R.id.options_type);

        final ArrayAdapter<String> shiftTypeSpinnerAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, ShiftTypeSpinner);
        shiftTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShiftType.setAdapter(shiftTypeSpinnerAdapter);
        spinnerShiftType.setOnItemSelectedListener(new ShiftTypeSpinner());

        complaint_type_spinner = findViewById(R.id.options_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner, ComplaintsTypeSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        complaint_type_spinner.setOnItemSelectedListener(new ComplaintsTypeSpinner());
        complaint_type_spinner.setAdapter(adapter);

        Button Submit = findViewById(R.id.btn_submit);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendfeedback();
            }
        });


    }

    private void getcabshift() {
        Log.d("getcabshift: ", "inside");
        Log.d("selectedDate: ", " " + startDate);
        Log.d("selectedDate: ", " " + this.startDate);

        jsonBody = new JSONObject();
        try {
            jsonBody.put("qlid", Employee_Qlid);
            jsonBody.put("date", startDate);

            Log.d("getcabshift: ", jsonBody.toString());

        } catch (Exception e) {
            System.out.println(String.valueOf(e));
        }

        final JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonBody);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, getShiftDetailsUrl,
                jsonArray,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        cabshift = response;
                        Log.d("Inside onResponseMethod","getCabShiftResponse "+cabshift.toString());
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Log.d("onErrorResponse: error", String.valueOf(error));

                    }
                }
        );

        RESTService.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);


    }

    private boolean isvalid() {
        boolean flag=true;
        if (TextUtils.isEmpty(datePicker.getText())) {
            Snackbar sb = Snackbar.make(mylayout, "Date must be filled!", Snackbar.LENGTH_LONG);
            View view = sb.getView();
            view.setBackgroundColor(Color.RED);
            sb.show();
            flag=false;


        } else if (TextUtils.isEmpty(selectType)) {
            Snackbar sb = Snackbar.make(mylayout, "Type must be selected!", Snackbar.LENGTH_LONG);
            View view = sb.getView();
            view.setBackgroundColor(Color.RED);
            sb.show();
            flag=false;

        } if(TextUtils.isEmpty(shiftTiming.getText())){

            Snackbar sb = Snackbar.make(mylayout, "Error in Fetching Shift timing !", Snackbar.LENGTH_LONG);
            View view = sb.getView();
            view.setBackgroundColor(Color.RED);
            sb.show();
            flag=false;
        }
        if(TextUtils.isEmpty(cabNumber.getText())){
            Snackbar sb = Snackbar.make(mylayout, "Error in Fetching Cab Number!", Snackbar.LENGTH_LONG);
            View view = sb.getView();
            view.setBackgroundColor(Color.RED);
            sb.show();
            flag=false;
        }

        else if (TextUtils.isEmpty(selectPickupDropType)) {
            Snackbar sb = Snackbar.make(mylayout, "PickUp/Drop must be selected!", Snackbar.LENGTH_LONG);
            View view = sb.getView();
            view.setBackgroundColor(Color.RED);
            sb.show();
            flag=false;

        } else if (TextUtils.isEmpty(complaintType)) {
            Snackbar sb = Snackbar.make(mylayout, "Complaint Type must be selected!", Snackbar.LENGTH_LONG);
            View view = sb.getView();
            view.setBackgroundColor(Color.RED);
            sb.show();
            flag=false;


        } else if (TextUtils.isEmpty(comment.getText().toString())) {
            Snackbar sb = Snackbar.make(mylayout, "Comment can't be empty!", Snackbar.LENGTH_LONG);
            View view = sb.getView();
            view.setBackgroundColor(Color.RED);
            sb.show();
            flag=false;

        }
        return flag;

    }

    private void methodToSelectDate(final EditText datePicker) {

        final Calendar dateSelection = Calendar.getInstance();
        // Get Current Date
        DatePickerDialog datePickerDialog;
        int mYear = dateSelection.get(Calendar.YEAR);
        int mMonth = dateSelection.get(Calendar.MONTH);
        int mDay = dateSelection.get(Calendar.DAY_OF_MONTH);
        int mHour = dateSelection.get(Calendar.HOUR);
        int mMinute = dateSelection.get(Calendar.MINUTE);


        datePickerDialog = new DatePickerDialog(this, android.app.AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateSelection.set(year, monthOfYear, dayOfMonth);
                String dateFormat = "YYYY/MM/dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
                String date = simpleDateFormat.format(new Date(year - 1900, monthOfYear, dayOfMonth));

                Date userselected;
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
                try {
                    userselected = dateformat.parse(date);
                    Date currentdate = new Date();

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //own code ends

                startDate = date;
                tempdate=startDate;
                getcabshift();
                datePicker.setText(startDate);
                Log.d("FeedBack Activity", "onDateSet: Startdate:- "+startDate);

            }
        }, mYear, mMonth, mDay);

        datePickerDialog.setTitle("");
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }


    class ShiftTypeSpinner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (i == 0) {
                shiftTiming.setText("");
                cabNumber.setText("");

            }
            if (i == 1) {
                selectType = "Schedule";
                try {
                    if(cabshift.length()>0&&
                            cabshift.getJSONObject(0).getString("shift").contains("Shift")
                            ||cabshift.length()>0&&
                            cabshift.getJSONObject(0).getString("shift").contains("Regular")){

                        shiftTiming.setText(cabshift.getJSONObject(0).getString("shift"));
                        cabNumber.setText(cabshift.getJSONObject(0).getString("cabno"));

                    }
                    else if(cabshift.length()>1&&
                            cabshift.getJSONObject(1).getString("shift").contains("Shift")
                            ||cabshift.length()>1&&
                            cabshift.getJSONObject(1).getString("shift").contains("Regular")){
                        shiftTiming.setText(cabshift.getJSONObject(1).getString("shift"));
                        cabNumber.setText(cabshift.getJSONObject(1).getString("cabno"));
                    }
                    else {
                        shiftTiming.setText("");
                        cabNumber.setText("");
                    }
                    Log.d( "onItemSelected: "," "+shiftTiming.getText());
                    Log.d( "onItemSelected: "," "+cabNumber.getText());
                    Log.d( "onItemSelected: ",cabshift.getJSONObject(0).getString("shift"));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (i == 2) {
                Log.d("Unscheduled: ", "inside Unscheduled");
                selectType = "Unscheduled";
                try {
                    if(cabshift.length()>0&&
                            cabshift.getJSONObject(0).getString("shift").contains("UnScheduled"))
                    {
                        shiftTiming.setText(cabshift.getJSONObject(0).getString("shift"));
                        cabNumber.setText(cabshift.getJSONObject(0).getString("cabno"));
                        Log.d("Unscheduled: ", "inside Unscheduled 1");
                    }
                    else if(cabshift.length()>1&&
                            cabshift.getJSONObject(1).getString("shift").contains("Unscheduled"))
                    {
                        shiftTiming.setText(cabshift.getJSONObject(1).getString("shift"));
                        cabNumber.setText(cabshift.getJSONObject(1).getString("cabno"));
                        Log.d("Unscheduled: ", "inside Unscheduled 2");
                    }
                    else {
                        shiftTiming.setText("");
                        cabNumber.setText("");
                        Log.d("Unscheduled: ", "inside Unscheduled 3");
                    }
                    Log.d( "onItemSelected: "," "+shiftTiming.getText());
                    Log.d( "onItemSelected: "," "+cabNumber.getText());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }


    class DropTypeSpinner implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (i == 0) {

            }
            if (i == 1) {
                selectPickupDropType = "Pickup";
            }
            if (i == 2) {
                selectPickupDropType = "Drop";


            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    class ComplaintsTypeSpinner implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (i == 0) {

            }
            if (i == 1) {
                complaintType = "Cab condition";
            }
            if (i == 2) {
                complaintType = "Driver issue";

            }
            if (i == 3) {
                complaintType = "Driving";

            }
            if (i == 4) {
                complaintType = "Cab timing ";

            }
            if (i == 5) {
                complaintType = "Trip route";
            }
            if (i == 6) {
                complaintType = "Frequent cab change ";

            }
            if (i == 7) {
                complaintType = "Frequent driver change";

            }
            if (i == 8) {
                complaintType = "Ac not working ";

            }
            if (i == 9) {
                complaintType = "taxi ";

            }
            if (i == 10) {
                complaintType = "Hygiene";
            }
            if (i == 11) {
                complaintType = "Others ";

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public void prepareSpinnerData() {
        DropTypeList = new ArrayList<String>();
        DropTypeList.add("Select");
        DropTypeList.add("Pickup");
        DropTypeList.add("Drop");

        ComplaintsTypeSpinner = new ArrayList<String>();
        ComplaintsTypeSpinner.add("Select");
        ComplaintsTypeSpinner.add("Cab condition");
        ComplaintsTypeSpinner.add("Driver issue");
        ComplaintsTypeSpinner.add("Driving");
        ComplaintsTypeSpinner.add("Cab timing");
        ComplaintsTypeSpinner.add("Trip route");
        ComplaintsTypeSpinner.add("Frequent cab change");
        ComplaintsTypeSpinner.add("Frequent driver change");
        ComplaintsTypeSpinner.add("Ac not working");
        ComplaintsTypeSpinner.add("Taxi");
        ComplaintsTypeSpinner.add("Hygiene");
        ComplaintsTypeSpinner.add("Others");


        ShiftTypeSpinner = new ArrayList<String>();
        ShiftTypeSpinner.add("Select");
        ShiftTypeSpinner.add("Scheduled");
        ShiftTypeSpinner.add("Unscheduled");

    }

    public void sendfeedback() {
        Log.d("Send feed back: ", "inside send feed back ");

        if (spinnerDropType.getSelectedItem().toString() == "Select" ||
                complaint_type_spinner.getSelectedItem().toString() == "Select" ||
                spinnerShiftType.getSelectedItem().toString() == "Select") {

        }

        if (isvalid()) {
            final AlertDialog alertDialog = new AlertDialog.Builder(FeedbackActivity.this).create();
            alertDialog.setTitle("Alert!!");
            alertDialog.setMessage("Your Request is Successfully Submitted");
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.cancel();
                    startActivity(new Intent(FeedbackActivity.this,Dashboard.class));

                }
            });
            alertDialog.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent Dashboard_intent=new Intent(getApplicationContext(),Dashboard.class);
                    startActivity(Dashboard_intent);
                    finish();
                }
            }, 2000);

            ;

            jsonBody = new JSONObject();
            try {

                //jsonBody.put("type", selectType);
                jsonBody.put("pd", selectPickupDropType);
                jsonBody.put("date",tempdate);
                Log.d( "sendfeedback: ", tempdate);
                jsonBody.put("type", shiftTiming.getText());
                jsonBody.put("cab", cabNumber.getText());
                jsonBody.put("qlid", Employee_Qlid); //
                jsonBody.put("comp", complaintType);
                jsonBody.put("comments", comment.getText().toString());
                Log.d("sendfeedback: ", jsonBody.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("VOLLEY", "inside onResponse method:UnscheduledRequest");
                    Log.i("VOLLEY", response.toString());

                    try {
                        if (response.getString("response").equalsIgnoreCase("1")) {

                            Snackbar snackbar = Snackbar.make(mylayout, "Your request was Submitted.", Snackbar.LENGTH_LONG);
                            snackbar.show();



                        }
                        else {
                            Snackbar snackbar = Snackbar.make(mylayout, "There was a problem submitting your Request", Snackbar.LENGTH_LONG);
                            snackbar.setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // TODO: 3/19/2018 Yet ro implement
                                }
                            });
                            snackbar.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d("VOLLEY", String.valueOf(error));
                    error.printStackTrace();
                }
            });
            RESTService.getInstance(FeedbackActivity.this).addToRequestQueue(jsonObjRequest);


        }

        else {
            Toast.makeText(getApplicationContext(), "Please check your entered information", Toast.LENGTH_LONG).show();

        }


    }

    public String getEmployeeQlid(){
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String Employee_Qlid = sharedPreferences.getString("user_qlid","");
        return Employee_Qlid;
    }

}



