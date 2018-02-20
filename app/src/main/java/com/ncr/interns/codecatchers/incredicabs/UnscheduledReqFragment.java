package com.ncr.interns.codecatchers.incredicabs;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
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


public class UnscheduledReqFragment extends android.support.v4.app.Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    int mYear, mMonth, mDay, mHour, mMinute;
    EditText fromDate, toDate, managerQLid, reasonForRequest, dropLocation, timepicker; //timepickerfrom;
    Button submit;
    TextView displayLocationSpinner;
    Spinner spinner_location;
    View rootView;
    CheckBox sat, sun;
    String url = "http://192.168.43.209:8080/DemoProject/re/sample";
    int i = 0;
    String locationArray[] = {"Home", "Office"};

    public UnscheduledReqFragment() {
        // Required empty public constructor
    }

    Context mContext;
    String startdate, enddate, starttime, endtime, locations, drop, dest;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflating the xml layout
        // View rootView = inflater.inflate(R.layout.content_request, container,false);
        View rootView = inflater.inflate(R.layout.content_request, container, false);

        //getting the id's
        displayLocationSpinner = rootView.findViewById(R.id.display_location_spinner);
        submit = rootView.findViewById(R.id.btn_submit);
        fromDate = rootView.findViewById(R.id.text_fromDate);
        toDate = rootView.findViewById(R.id.text_ToDate);
        timepicker = rootView.findViewById(R.id.time_picker);
        managerQLid = rootView.findViewById(R.id.text_managerQLID);
        reasonForRequest = rootView.findViewById(R.id.text_reasonForRequest);
        dropLocation = rootView.findViewById(R.id.text_dropLocation);
        //  timepickerfrom = rootView.findViewById(R.id.time_pickerfrom);
        sat = rootView.findViewById(R.id.cbsat);
        sun = rootView.findViewById(R.id.cbsun);

        /**
         *Code For handling the spinner
         */

        spinner_location = rootView.findViewById(R.id.spinner_location);
        spinner_location.setOnItemSelectedListener(this);

        /**
         * Code for creating the Adapter to add the data of location array into Spinner
         */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, locationArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_location.setAdapter(adapter);

        //added date and time pickers
        fromDate.setOnClickListener(this);
        toDate.setOnClickListener(this);
        timepicker.setOnClickListener(this);
        //timepickerfrom.setOnClickListener(this);

        //Handling the Submit button
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
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("Emp_QLID", "1234");
                    jsonBody.put("Shift_ID", "4");
                    jsonBody.put("Mgr_QLID", "456");
                    jsonBody.put("Weekend", String.valueOf(i));
                    jsonBody.put("Destination", dest);
                    jsonBody.put("Reason", reasonForRequest.getText().toString());
                    jsonBody.put("Start_Date_Time", "2018/12/12");
                    jsonBody.put("End_Date_Time", enddate + " " + endtime + ":00");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //json for request
                JSONObject jsonBodyRequest = new JSONObject();
                try {
                    jsonBodyRequest.put("from", "karangupta.199317@gmail.com");
                    jsonBodyRequest.put("recepient1", "karangupta.199317@gmail.com");
                    jsonBodyRequest.put("recepient2", "haffiza123@gmail.com");
                    jsonBodyRequest.put("subject", "You were expecting me");
                    jsonBodyRequest.put("message", "this is test mail from rest api");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST,
                        url,
                        jsonBody,
                        new Response.Listener<JSONObject>() {
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

            }
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

    //method to implement functionality in Spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Toast.makeText(getActivity(), ""+position, Toast.LENGTH_SHORT).show();
        if (position == 0) {
            dest = "H";
            
            displayLocationSpinner.setText("BL No:-86,Saraswati Kunj,Golf Course Road,Sector 53,Gurgaon");
            dropLocation.setText("NCR Corporation, Vipul Plaza,Suncity,Sector 54,Gurgaon");
        }
        else{
            dest = "O";

            displayLocationSpinner.setText("NCR Corporation, Vipul Plaza,Suncity,Sector 54,Gurgaon");
            dropLocation.setText("BL No:-86,Saraswati Kunj,Golf Course Road,Sector 53,Gurgaon");
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
