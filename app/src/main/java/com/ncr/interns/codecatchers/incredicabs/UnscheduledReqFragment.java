package com.ncr.interns.codecatchers.incredicabs;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class UnscheduledReqFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private final static String LOG_TAG = UnscheduledReqFragment.class.getSimpleName();

    int mYear, mMonth, mDay, mHour, mMinute, selectPick, selectDrop;
    long diffDays, diff;    //diffDays stores the difference between from and to date and is to be used while sending the message to the manager
    SimpleDateFormat dateFormat;
    Date from_date, to_date;
    String dateToCheck, dateFromCheck;
    EditText fromDate, toDate, reasonForRequest, timePicker, otherPickUp, otherDrop;
    TextView managerQLid_textField, dropLocation;
    Button submit;
    TextView displayLocationSpinner;
    Spinner spinner_location, spinner_other_drop;
    String day_st;
    JSONObject jsonBody;
    private ProgressDialog progressBar;
    Spinner managerQLid;
    public int Counter;
    String url = "http://192.168.43.108:8522/DemoProject/re/sample";
    String locationArray[] = {"Select", "Home", "Office", "Other"};
    String locationArraydrop[] = {"Select", "Home", "Office", "Other"};
    String approverArray[] = {"Lvl 1 Manager", "Lvl2 Manager"};
    NestedScrollView nsv;

    public UnscheduledReqFragment() {
        // Required empty public constructor
    }

    String startDate, endDate, startTime, dest, sourceAdd, dropAdd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.content_request, container, false);

        //getting the id's
        managerQLid_textField = rootView.findViewById(R.id.TextView_managerQLID);
        displayLocationSpinner = rootView.findViewById(R.id.display_location_spinner);
        submit = rootView.findViewById(R.id.btn_submit);
        fromDate = rootView.findViewById(R.id.text_fromDate);
        toDate = rootView.findViewById(R.id.text_ToDate);
        timePicker = rootView.findViewById(R.id.time_picker);
        reasonForRequest = rootView.findViewById(R.id.text_reasonForRequest);
        dropLocation = rootView.findViewById(R.id.text_dropLocation);
        nsv = rootView.findViewById(R.id.nestedsv);
        spinner_other_drop = rootView.findViewById(R.id.spinner_other);
        otherPickUp = rootView.findViewById(R.id.tvotherpick);
        otherDrop = rootView.findViewById(R.id.etotherdrop);

        /*
         Code For handling the spinner
         */
        spinner_location = rootView.findViewById(R.id.spinner_location);
        managerQLid = rootView.findViewById(R.id.text_managerQLID);
        spinner_location.setOnItemSelectedListener(new LocationSpinner());
        managerQLid.setOnItemSelectedListener(new ApprovalManagerQlid());
      /*
          Code for creating the Adapter to add the data of location array into Spinner
         */
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, locationArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_location.setAdapter(adapter);

        ArrayAdapter adapterDropToOther = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, locationArraydrop) {
            @Override
            public boolean isEnabled(int position) {
                if (position == selectPick && position != 3) {
                    //Disable the third item of spinner.
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View spinnerView = super.getDropDownView(position, convertView, parent);
                TextView spinnerTextView = (TextView) spinnerView;

                if (position == selectPick && position != 3) {

                    //Set the disable spinner item color fade .
                    spinnerTextView.setTextColor(Color.parseColor("#bcbcbb"));
                } else {

                    spinnerTextView.setTextColor(Color.BLACK);


                }
                return spinnerView;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_other_drop.setAdapter(adapterDropToOther);
        spinner_other_drop.setOnItemSelectedListener(new LocationSpinnerotherdrop());

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, approverArray);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        managerQLid.setAdapter(adapter2);

        //added date and time pickers
        fromDate.setOnClickListener(this);
        toDate.setOnClickListener(this);
        timePicker.setOnClickListener(this);

        // managerQLid.setText(getActivity().getSharedPreferences(null, MODE_PRIVATE).getString("Mgr_Qlid", "RB250491"));
        // Handling the Submit button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPick == 3) {
                    sourceAdd = otherPickUp.getText().toString();
                }
                if (selectDrop != 3)
                    dropAdd = dropLocation.getText().toString();
                else
                    dropAdd = otherDrop.getText().toString();
                if (validation()) {
                    diff = to_date.getTime() - from_date.getTime();
                    diffDays = diff / (24 * 60 * 60 * 1000) + 1;
                    if ((validation()) && (diffDays > 0)) {

                        //<editor-fold desc="code to set the Progress bar">
                        progressBar = new ProgressDialog(getActivity(), 0);
                        progressBar.setTitle("Wait");
                        progressBar.setMessage("Sending request..");
                        progressBar.show();
                        progressBar.setCanceledOnTouchOutside(true);
                        //</editor-fold>
                        diff = to_date.getTime() - from_date.getTime();
                        diffDays = diff / (24 * 60 * 60 * 1000) + 1;
                        //if day is 1 then day_st day else days
                        if (diffDays == 1) {
                            day_st = "day";
                        } else {
                            day_st = "days";
                        }
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Confirmation").setMessage("You are requesting cab for " + diffDays + " " + day_st + " , Do you want to continue")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (validation()) {
                                            jsonBody = new JSONObject();
                                            try {
                                                jsonBody.put("Emp_QLID", getActivity().getSharedPreferences(null, MODE_PRIVATE).getString("Emp_qlid", "RB250491"));
                                                jsonBody.put("Shift_ID", "4");
                                                jsonBody.put("Mgr_QLID", "sc250512");
                                                // jsonBody.put("Weekend", String.valueOf(i));
                                                jsonBody.put("Counter", Counter);
                                                jsonBody.put("Level2_mgr", "gs250365");
                                                Log.i(LOG_TAG, "gs250365");
                                                jsonBody.put("Other_Addr", dest);
                                                jsonBody.put("Reason", reasonForRequest.getText().toString());
                                                jsonBody.put("Start_Date_Time", startDate+startTime);
                                                jsonBody.put("End_Date_Time", endDate );
                                                jsonBody.put("Source", sourceAdd);
                                                jsonBody.put("Destination", dropAdd);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
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
                                                            progressBar.dismiss();
                                                        }
                                                    }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {

                                                    progressBar.dismiss();
                                                    // Do something when error occurred
                                                    Log.d("VOLLEY", "Something went wrong");
                                                    Toast.makeText(getActivity(), "Oops..Something Went wrong", Toast.LENGTH_SHORT).show();
                                                    error.printStackTrace();
                                                }
                                            });


                                            RESTService.getInstance(getContext().getApplicationContext()).addToRequestQueue(jsonObjRequest);

                                        } else {
                                            Toast.makeText(getActivity(), "Please check your entered information", Toast.LENGTH_LONG).show();
                                        }


                                    }
                                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //code to set the Action for the Negitive button
                                dialog.cancel();
                                progressBar.dismiss();


                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();

                    } else {
                        Snackbar snackbar = Snackbar.make(nsv, "Selected Date is invalid", Snackbar.LENGTH_LONG);
                        snackbar.show();

                    }
                }

            }

        });
        // Toast.makeText(getActivity(), "Works!!!", Toast.LENGTH_SHORT).show();
        return rootView;
    }

    private void methodToSelectDate(final EditText datePicker) {

        final Calendar dateSelection = Calendar.getInstance();
        // Get Current Date
        // TimePickerDialog timePickerDialog;
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

                //own code starts    status:- working
                String status = null;
                Date userselected = new Date();
                UnscheduledReqFragment.this.dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                try {
                    userselected = UnscheduledReqFragment.this.dateFormat.parse(date);
                    Date currentDate = new Date();
                    if (UnscheduledReqFragment.this.dateFormat.parse(UnscheduledReqFragment.this.dateFormat.format(userselected)).before(UnscheduledReqFragment.this.dateFormat.parse(UnscheduledReqFragment.this.dateFormat.format(currentDate)))) {
                        status = "Not Valid";
                    } else {
                        status = UnscheduledReqFragment.this.dateFormat.format(userselected);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                //own code ends

                datePicker.setText(status);   //previously:- datePicker.setText(date);
                try {
                    from_date = UnscheduledReqFragment.this.dateFormat.parse(status);
                    dateFromCheck = status;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                startDate = date;

            }
        }, mYear, mMonth, mDay);
        datePickerDialog.setTitle("");
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();

    }

    private void methodToSelectDateto(final EditText datePicker) {

        final Calendar dateSelection = Calendar.getInstance();
        // Get Current Date
        // TimePickerDialog timePickerDialog;
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

                //own code starts
                String status = null;
                Date userselected = new Date();
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
                try {
                    userselected = dateformat.parse(date);
                    Date currentdate = new Date();
                    if (dateformat.parse(dateformat.format(userselected)).before(dateformat.parse(dateformat.format(currentdate)))) {
                        status = "Not Valid";
                    } else {
                        status = dateformat.format(userselected);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //own code ends
                datePicker.setText(status);
                try {
                    to_date = dateformat.parse(status);
                    dateToCheck = status;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                endDate = date;

            }
        }, mYear, mMonth, mDay);
        datePickerDialog.setTitle("");
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
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
                    timePicker.setText("" + hourOfDay + ":0" + minute);
                    startTime = "" + hourOfDay + ":0" + minute;
                } else {
                    timePicker.setText("" + hourOfDay + ":" + minute);
                    startTime = "" + hourOfDay + ":" + minute;
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
                methodToSelectTime(timePicker);
                break;
        }
    }

    //method to do validation
    public Boolean validation() {

        if (TextUtils.isEmpty(startDate)) {
            Snackbar snackbar = Snackbar.make(nsv, "From Date Can't be empty", Snackbar.LENGTH_LONG);
            snackbar.show();
            //fromDate.setError("Can't be empty");
        } else {
            if (TextUtils.isEmpty(endDate)) {
                //toDate.setError("Can't be empty");
                Snackbar snackbar = Snackbar.make(nsv, "To Date Can't be empty", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                if (TextUtils.isEmpty(startTime)) {
                    //   timePicker.setError("Can't be empty");
                    Snackbar snackbar = Snackbar.make(nsv, "Time Can't be empty", Snackbar.LENGTH_LONG);
                    snackbar.show();

                } else {
                    if (TextUtils.isEmpty(managerQLid_textField.getText().toString())) {
                        //  managerQLid_textField.setError("Can't be empty");
                        Snackbar snackbar = Snackbar.make(nsv, "Manager ID Can't be empty", Snackbar.LENGTH_LONG);
                        snackbar.show();
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
        }
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

    class LocationSpinnerotherdrop implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            System.out.println(position);
            selectDrop = position;
            if (position == 0) {
                dropLocation.setVisibility(View.GONE);

            }
            if (position == 1) {
                dropLocation.setVisibility(View.VISIBLE);
                otherDrop.setVisibility(View.GONE);
                dest = dest + "HOME";
                //TODO have to get the Data from DATABASE
                dropLocation.setText("BL No:-86,Saraswati Kunj,Golf Course Road,Sector 53,Gurgaon");

            }
            if (position == 2) {
                dest = dest + "OFFICE";
                dropLocation.setVisibility(View.VISIBLE);
                otherDrop.setVisibility(View.GONE);
                dropLocation.setText("NCR Corporation, Vipul Plaza,Suncity,Sector 54,Gurgaon");
                //TODO have to get the Data from DATABASE
            }


            if (position == 3) {
                dest = dest + "OTHERS ";
                dropLocation.setVisibility(View.GONE);
                otherDrop.setVisibility(View.VISIBLE);
                dropLocation.setText("");


            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class LocationSpinner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            System.out.println(position);
            selectPick = position;
            // dropLocation.setText("");

            if (position == 0) {
                displayLocationSpinner.setVisibility(View.GONE);
                dropLocation.setText("");

            }
            if (position == 1) {
                dest = "HOME TO ";
                //TODO have to get the Data from DATABASE
                displayLocationSpinner.setText("BL No:-86,Saraswati Kunj,Golf Course Road,Sector 53,Gurgaon");
                displayLocationSpinner.setVisibility(View.VISIBLE);
                otherPickUp.setVisibility(View.GONE);
                sourceAdd = "BL No:-86,Saraswati Kunj,Golf Course Road,Sector 53,Gurgaon";
                //dropLocation.setText("NCR Corporation, Vipul Plaza,Suncity,Sector 54,Gurgaon");

            }
            if (position == 2) {
                dest = "OFFICE TO ";
                //TODO have to get the Data from DATABASE
                displayLocationSpinner.setText("NCR Corporation, Vipul Plaza,Suncity,Sector 54,Gurgaon");
                displayLocationSpinner.setVisibility(View.VISIBLE);
                otherPickUp.setVisibility(View.GONE);
                sourceAdd = "NCR Corporation, Vipul Plaza,Suncity,Sector 54,Gurgaon";
                //dropLocation.setText("BL No:-86,Saraswati Kunj,Golf Course Road,Sector 53,Gurgaon");
            }


            if (position == 3) {
                dest = "OTHERS TO ";
                displayLocationSpinner.setVisibility(View.GONE);
                otherPickUp.setVisibility(View.VISIBLE);
                spinner_other_drop.setVisibility(View.VISIBLE);
                //dropLocation.setVisibility(View.GONE);

            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class ApprovalManagerQlid implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//            if(position==0){
//                managerQLid_textField.setVisibility(View.GONE);}

            if (position == 0) {
                Counter = 1;
                managerQLid_textField.setText("sc250512");
                managerQLid_textField.setVisibility(View.VISIBLE);
            }
            if (position == 1) {
                managerQLid_textField.setText("gs250365");
                managerQLid_textField.setVisibility(View.VISIBLE);
                Counter = 2;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

}

