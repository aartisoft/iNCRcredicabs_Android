package com.ncr.interns.codecatchers.incredicabs;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
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
    EditText shiftTiming;
    EditText cabNumber;
    private String url = "http://153.58.56.173:8080/Cab_Managemnet/CBMang/complaint";
    private String getShiftDetailsUrl = "http://192.168.43.49:8090/Cab_Managemnet/CBMang/getCabShift";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                getcabshift();
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

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.custom_spinner, DropTypeList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDropType.setOnItemSelectedListener(new DropTypeSpinner());
        spinnerDropType.setAdapter(adapter2);
        spinnerDropType = findViewById(R.id.options_drop);
        spinnerShiftType = findViewById(R.id.options_type);

        final ArrayAdapter<String> shiftTypeSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, ShiftTypeSpinner);
        shiftTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShiftType.setAdapter(shiftTypeSpinnerAdapter);
        spinnerShiftType.setOnItemSelectedListener(new ShiftTypeSpinner());

        complaint_type_spinner = findViewById(R.id.options_spinner);
        //complaint_type_spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, ComplaintsTypeSpinner);
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
        Log.d("selectedDate: ", " " + selectedDate);

        jsonBody = new JSONObject();
        try {
            jsonBody.put("qlid", "AK250783");
            jsonBody.put("date", "2018/03/20");
            Log.d("getcabshift: ", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JSONArray jsonArray =new JSONArray();
        jsonArray.put(jsonBody);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.POST,
                getShiftDetailsUrl,
                jsonArray,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        cabshift=response;
                        Log.d("getcabshift: ",cabshift.toString());
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

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RESTService.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);


    }

    private boolean isvalid() {
        if (TextUtils.isEmpty(selectedDate)) {
            // Snackbar sb = Snackbar.make(mylayout, "Date must be filled!", 0);
            // View view = sb.getView();
            // view.setBackgroundColor(Color.RED);
            //sb.show();


        } else {
            if (TextUtils.isEmpty(selectType)) {
                Snackbar sb = Snackbar.make(mylayout, "Type must be selected!", 0);
                View view = sb.getView();
                view.setBackgroundColor(Color.RED);
                sb.show();

            } else {
                if (TextUtils.isEmpty(selectPickupDropType)) {
                    Snackbar sb = Snackbar.make(mylayout, "PickUp/Drop must be selected!", 0);
                    View view = sb.getView();
                    view.setBackgroundColor(Color.RED);
                    sb.show();

                } else {
                    if (TextUtils.isEmpty(complaintType)) {
                        Snackbar sb = Snackbar.make(mylayout, "Complaint Type must be selected!", 0);
                        View view = sb.getView();
                        view.setBackgroundColor(Color.RED);
                        sb.show();


                    } else {
                        if (TextUtils.isEmpty(comment.getText().toString())) {
                            Snackbar sb = Snackbar.make(mylayout, "Comment can't be empty!", 0);
                            View view = sb.getView();
                            view.setBackgroundColor(Color.RED);
                            sb.show();

                        } else {
                            return true;
                        }
                    }


                }


            }
        }

        return false;

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
                datePicker.setText(startDate);
                Log.d("FeedBack Activity", "onDateSet: Startdate:- "+startDate);

            }
        }, mYear, mMonth, mDay);

        datePickerDialog.setTitle("");
        datePickerDialog.show();
    }

    public boolean validation() {

        return false;
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
                    shiftTiming.setText(cabshift.getJSONObject(0).getString("shift"));
                    cabNumber.setText(cabshift.getJSONObject(0).getString("cabno"));
                    Log.d( "onItemSelected: "," "+cabshift.getJSONObject(0).getString("shift"));
                    Log.d( "onItemSelected: "," "+cabshift.getJSONObject(0).getString("cabno"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (i == 2) {
                selectType = "Unschedule";
                try {
                    if(cabshift.length()<2){
                        shiftTiming.setText("");
                        cabNumber.setText("");
                    }
                    else{
                        shiftTiming.setText(cabshift.getJSONObject(1).getString("shift"));
                        cabNumber.setText(cabshift.getJSONObject(1).getString("cabno"));
                    }

                    Log.d( "onItemSelected: "," "+cabshift.getJSONObject(1).getString("shift"));
                    Log.d( "onItemSelected: "," "+cabshift.getJSONObject(1).getString("cabno"));
                } catch (Exception e) {
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
        ShiftTypeSpinner.add("scheduled");
        ShiftTypeSpinner.add("Unscheduled");

    }

    public void sendfeedback() {

        if (spinnerDropType.getSelectedItem().toString() == "Select" ||
                complaint_type_spinner.getSelectedItem().toString() == "Select" ||
                spinnerShiftType.getSelectedItem().toString() == "Select") {
            Snackbar sb = Snackbar.make(mylayout, "Data insufficient!", 0);
            View view1 = sb.getView();
            view1.setBackgroundColor(Color.RED);
            sb.show();
        }

        if (isvalid()) {

            Snackbar snackbar = Snackbar.make(mylayout,"Submitted",Snackbar.LENGTH_LONG);
            snackbar.show();

            jsonBody = new JSONObject();
            try {
                jsonBody.put("date", selectedDate);
                jsonBody.put("type", selectType);
                jsonBody.put("pd", selectPickupDropType);
                //jsonBody.put("shift", "shift");
                //jsonBody.put("cab", "cab no");
                jsonBody.put("qlid", "gs250365"); // TODO: 3/19/2018 Get Qlid from Database
                jsonBody.put("comp", complaintType);
                jsonBody.put("comments", comment.getText().toString());

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

                        } else {
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

                    // Do something when error occurred
                    Log.d("VOLLEY", "Something went wrong");
                    //  Toast.makeText(getActivity(), "Oops..Something Went wrong", Toast.LENGTH_SHORT).show();
                                /*Snackbar snackbar = Snackbar.make(mylayout, "Oops Something Went Wrong", Snackbar.LENGTH_LONG);
                                snackbar.show();*/
                    error.printStackTrace();
                }
            });
            jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RESTService.getInstance(FeedbackActivity.this).addToRequestQueue(jsonObjRequest);

        } else {
            //  Toast.makeText(getApplicationContext(), "Please check your entered information", Toast.LENGTH_LONG).show();
        }


    }

}




