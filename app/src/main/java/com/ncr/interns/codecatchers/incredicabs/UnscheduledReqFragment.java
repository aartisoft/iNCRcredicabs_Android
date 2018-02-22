package com.ncr.interns.codecatchers.incredicabs;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class UnscheduledReqFragment extends android.support.v4.app.Fragment implements View.OnClickListener{
    int mYear, mMonth, mDay, mHour, mMinute;
    EditText fromDate, toDate, reasonForRequest, dropLocation, timepicker; //timepickerfrom;
    EditText managerQLid_textField;
    Button submit;
    TextView displayLocationSpinner;
    Spinner spinner_location;
    Spinner managerQLid;
    View rootView;
    CheckBox sat, sun;
    String url = "http://192.168.43.209:8080/DemoProject/req/unscheduled";
    int i = 0;
    String locationArray[] = {"Select", "Home", "Office"};
    String ApproverArray[] = {"Select", "Lvl 1 Manager", "Lvl2 Manager"};
    NestedScrollView nsv;

    public UnscheduledReqFragment() {
        // Required empty public constructor
    }

    Context mContext;
    String startdate, enddate, starttime, endtime, locations, drop, dest;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_request, container, false);

        //getting the id's
        managerQLid_textField = rootView.findViewById(R.id.EditText_managerQLID);
        displayLocationSpinner = rootView.findViewById(R.id.display_location_spinner);
        submit = rootView.findViewById(R.id.btn_submit);
        fromDate = rootView.findViewById(R.id.text_fromDate);
        toDate = rootView.findViewById(R.id.text_ToDate);
        timepicker = rootView.findViewById(R.id.time_picker);
        reasonForRequest = rootView.findViewById(R.id.text_reasonForRequest);
        dropLocation = rootView.findViewById(R.id.text_dropLocation);
        nsv = rootView.findViewById(R.id.nestedsv);
        sat = rootView.findViewById(R.id.cbsat);
        sun = rootView.findViewById(R.id.cbsun);
        /**
         *Code For handling the spinner
         */

        spinner_location = rootView.findViewById(R.id.spinner_location);
        managerQLid = rootView.findViewById(R.id.text_managerQLID);
        spinner_location.setOnItemSelectedListener(new LocationSpinner());
        managerQLid.setOnItemSelectedListener(new ApprovalManagerQlid());

        /**
         * Code for creating the Adapter to add the data of location array into Spinner
         */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, locationArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_location.setAdapter(adapter);



        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ApproverArray);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        managerQLid.setAdapter(adapter2);

        //added date and time pickers
        fromDate.setOnClickListener(this);
        toDate.setOnClickListener(this);
        timepicker.setOnClickListener(this);

        // managerQLid.setText(getActivity().getSharedPreferences(null, MODE_PRIVATE).getString("Mgr_Qlid", "RB250491"));
        // Handling the Submit button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sat.isChecked())
                    i = 1;
                if (sun.isChecked())
                    i = 2;
                if (sun.isChecked() && sat.isChecked())
                    i = 3;

                Toast.makeText(getActivity(), "" + i, Toast.LENGTH_SHORT).show();
                JSONObject jsonBody;

                // Toast.makeText(getActivity(), "" + i, Toast.LENGTH_SHORT).show();
                if (validation()) {
                    jsonBody = new JSONObject();
                    try {
                        jsonBody.put("Emp_QLID", getActivity().getSharedPreferences(null, MODE_PRIVATE).getString("Emp_qlid", "RB250491"));
                        jsonBody.put("Shift_ID", "4");
                        // jsonBody.put("Mgr_QLID", managerQLid.getText().toString());
                        jsonBody.put("Weekend", String.valueOf(i));
                        jsonBody.put("Destination", dest);
                        jsonBody.put("Reason", reasonForRequest.getText().toString());
                        jsonBody.put("Start_Date_Time", fromDate);
                        jsonBody.put("End_Date_Time", enddate);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.i("VOLLEY", "inside onResponse method:UnscheduledRequest");
                            Log.i("VOLLEY", response.toString());

                            try {
                                if (response.getString("status").equalsIgnoreCase("success")) {
                                    Toast.makeText(getActivity(), "Your request is in process", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity(), "Failed to make request", Toast.LENGTH_LONG).show();
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
                                    error.printStackTrace();
                                }
                            });
                    RESTService.getInstance(getContext().getApplicationContext()).addToRequestQueue(jsonObjRequest);

                    RESTService.getInstance(getContext().getApplicationContext()).addToRequestQueue(jsonObjRequest);
                } else {
                    //  Toast.makeText(getActivity(), "Please check your entered information", Toast.LENGTH_LONG).show();
                }
            }//On Click listner Finish
        });
        // Toast.makeText(getActivity(), "Works!!!", Toast.LENGTH_SHORT).show();
        return rootView;
    }

    private void methodToSelectDate(final EditText datePicker) {

        final Calendar dateSelection = Calendar.getInstance();
        // Get Current Date
        TimePickerDialog timePickerDialog;
        DatePickerDialog datePickerDialog;
        mYear = dateSelection.get(Calendar.YEAR);
        mMonth = dateSelection.get(Calendar.MONTH);
        mDay = dateSelection.get(Calendar.DAY_OF_MONTH);
        mHour = dateSelection.get(Calendar.HOUR);
        mMinute = dateSelection.get(Calendar.MINUTE);

        datePickerDialog = new DatePickerDialog(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateSelection.set(year, monthOfYear, dayOfMonth);
                String dateFormat = "YYYY/MM/dd";
//                datePicker.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
//                datePicker.setText();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
                String date = simpleDateFormat.format(new Date(year - 1900, monthOfYear, dayOfMonth));
                datePicker.setText(date);
                startdate = date;

            }
        }, mYear, mMonth, mDay);
        datePickerDialog.setTitle("");
        datePickerDialog.show();

    }

    private void methodToSelectDateto(final EditText datePicker) {

        final Calendar dateSelection = Calendar.getInstance();
        // Get Current Date
        TimePickerDialog timePickerDialog;
        DatePickerDialog datePickerDialog;
        mYear = dateSelection.get(Calendar.YEAR);
        mMonth = dateSelection.get(Calendar.MONTH);
        mDay = dateSelection.get(Calendar.DAY_OF_MONTH);
        mHour = dateSelection.get(Calendar.HOUR);
        mMinute = dateSelection.get(Calendar.MINUTE);

        datePickerDialog = new DatePickerDialog(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateSelection.set(year, monthOfYear, dayOfMonth);
                String dateFormat = "YYYY/MM/dd";
//                datePicker.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
//                datePicker.setText();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
                String date = simpleDateFormat.format(new Date(year - 1900, monthOfYear, dayOfMonth));
                datePicker.setText(date);
                enddate = date;

            }
        }, mYear, mMonth, mDay);
        datePickerDialog.setTitle("");
        datePickerDialog.show();

    }

    private void methodToSelectTime(final EditText datePicker) {

        final Calendar dateSelection = Calendar.getInstance();
        // Get Current Date
        final TimePickerDialog timePickerDialog;
        mHour = dateSelection.get(Calendar.HOUR);
        mMinute = dateSelection.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (minute < 10) {
                    timepicker.setText("" + hourOfDay + ":0" + minute);
                    endtime = "" + hourOfDay + ":0" + minute;
                } else {
                    timepicker.setText("" + hourOfDay + ":" + minute);
                    endtime = "" + hourOfDay + ":" + minute;
                }

            }
        }, mHour, mMinute, false);
        timePickerDialog.show();
    }


    /*Code to call the Date and time picker*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_fromDate:
                methodToSelectDate(fromDate);
                break;
            case R.id.text_ToDate:
                methodToSelectDateto(toDate);
                break;
            case R.id.time_picker:
                methodToSelectTime(timepicker);
                break;
        }
    }

    //method to do validation
    public Boolean validation() {

        if (TextUtils.isEmpty(startdate)) {
            Snackbar snackbar = Snackbar.make(nsv, "From Date Can't be empty", Snackbar.LENGTH_LONG);
            snackbar.show();
            // fromDate.setError("Can't be empty");
        } else {
            if (TextUtils.isEmpty(enddate)) {
                //toDate.setError("Can't be empty");
                Snackbar snackbar = Snackbar.make(nsv, "To Date Can't be empty", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                if (TextUtils.isEmpty(endtime)) {
                    //  timepicker.setError("Can't be empty");
                    Snackbar snackbar = Snackbar.make(nsv, "Time Can't be empty", Snackbar.LENGTH_LONG);
                    snackbar.show();

//                } else {
//                    if (TextUtils.isEmpty(managerQLid.getText().toString())) {
//                        managerQLid.setError("Can't be empty");
//                        Snackbar snackbar = Snackbar.make(nsv, "Manager ID Can't be empty", Snackbar.LENGTH_LONG);
//                        snackbar.show();
                } else {
                    if (true/*isValid(managerQLid.getText().toString())*/) {
                        if (TextUtils.isEmpty(reasonForRequest.getText().toString())) {
                            reasonForRequest.setError("Can't be empty");
                            Snackbar snackbar = Snackbar.make(nsv, " Reason Can't be empty", Snackbar.LENGTH_LONG);
                            snackbar.show();

                        } else {
                            return true;
                        }
                    } else {
                        Snackbar snackbar = Snackbar.make(nsv, "ID is not valid", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            }
        }
        // }
        return false;
    }

    public static boolean isValid(String str) {
        if (!(str.length() == 8)) {
            return false;
        } else if (!(((str.charAt(0) <= 'Z' && str.charAt(0) >= 'A')) || ((str.charAt(0) <= 'z') && (str.charAt(0) >= 'a')) && ((str.charAt(1) <= 'Z' && str.charAt(1) >= 'A')) || ((str.charAt(1) <= 'z') && (str.charAt(1) >= 'a')))) {

            return false;
        } else if (true) {
            for (int i = 2; i < str.length(); i++) {
                int a = str.charAt(i);
                if ((a <= 57) && (a >= 48)) {

                } else {
                    return false;
                }
            }
        }
        return true;
    }

    class LocationSpinner implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(position ==1){
                dest ="O";
                //TODO have to get the Data from DATABASE
                displayLocationSpinner.setText("BL No:-86,Saraswati Kunj,Golf Course Road,Sector 53,Gurgaon");
                displayLocationSpinner.setVisibility(View.VISIBLE);
                dropLocation.setText("NCR Corporation, Vipul Plaza,Suncity,Sector 54,Gurgaon");
            }
            if(position==2)
            {
               dest="H";
               //TODO have to get the Data from DATABASE
               displayLocationSpinner.setText("NCR Corporation, Vipul Plaza,Suncity,Sector 54,Gurgaon");
                displayLocationSpinner.setVisibility(View.VISIBLE);
               dropLocation.setText("BL No:-86,Saraswati Kunj,Golf Course Road,Sector 53,Gurgaon");
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class ApprovalManagerQlid implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1){
                    //TODO Getting the Magnager's QLid

                    managerQLid_textField.setText("Mg123456");
                }
                if(position ==2){
                    //TODO Getting the Magnager's QLid
                    managerQLid_textField.setText("Mg654321");
                }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

}
