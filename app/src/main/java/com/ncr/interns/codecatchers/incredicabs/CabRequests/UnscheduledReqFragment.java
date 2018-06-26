package com.ncr.interns.codecatchers.incredicabs.CabRequests;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ncr.interns.codecatchers.incredicabs.NCABUtils.Environment;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.EmployeeContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.NcabSQLiteHelper;
import com.ncr.interns.codecatchers.incredicabs.R;
import com.ncr.interns.codecatchers.incredicabs.NCABUtils.RESTService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class UnscheduledReqFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private final static String TAG = UnscheduledReqFragment.class.getSimpleName();

    SQLiteDatabase mSqLiteDatabase;
    View rootView;
    int mYear, mMonth, mDay, mHour, mMinute, selectPick, selectDrop;
    long diffDays, diff;    //diffDays stores the difference between from and to date and is to be used while sending the message to the manager
    SimpleDateFormat dateFormat;
    Date from_date, to_date;
    String dateToCheck, dateFromCheck, day_st, from_address;;
    EditText fromDate, toDate, reasonForRequest, timePicker, editText_otherPickUp, editText_otherDrop;
    TextView managerQLid_textField, displayDropLocation_textView, textView_selectTime, displayPickupLocation_textView;
    ScrollView nsv;
    String startDate, endDate, startTime, destination_entry_source, sourceAddress, dropAddress;
    String destination_entry_to;
    Button submit;
    Spinner spinner_location, spinner_dropLocation, managerQLid;
    JSONObject jsonBody;
    private ProgressDialog progressBar;
    public int Counter;
    Context ctx;
    NcabSQLiteHelper ncabSQLiteHelper;
    String mainUrl = Environment.URL_UNSCHEDULED_REQUEST;
    String pickupLocationArray[] = {"Select", "Home", "Office", "Other"}; //String Array
    String dropLocationArray[] = {"Select", "Home", "Office", "Other"}; //String Array
    String approverManagerArray[] = {"Lvl 1 Manager", "Lvl 2 Manager"}; //String Array

    //<editor-fold desc="Data For Request">
    String Employee_Qlid;
    String Employee_Name;
    String Employee_Manager_1_Qlid;
    String Employee_Manager_2_Qlid;
    String Employee_Manager_1_Name;
    String Employee_Manager_2_Name;
    String Employee_HomeAddress;
    String Employee_OfficeAddress;
    String Employee_Contact_number;
    //</editor-fold>


    public UnscheduledReqFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.new_demo, container, false);

        getComponentsId();

        ctx = getActivity();
        ncabSQLiteHelper = new NcabSQLiteHelper(ctx);
        mSqLiteDatabase = ncabSQLiteHelper.getReadableDatabase();
        getData();
        ArrayAdapter<String> pickupSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, pickupLocationArray);

        pickupSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_location.setAdapter(pickupSpinnerAdapter);

        ArrayAdapter dropSpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_spinner, dropLocationArray) {
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
                    spinnerTextView.setTextColor(Color.GRAY);
                } else {
                    spinnerTextView.setTextColor(Color.BLACK);
                }
                return spinnerView;
            }
        };
        pickupSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_dropLocation.setAdapter(dropSpinnerAdapter);
        spinner_dropLocation.setOnItemSelectedListener(new LocationSpinnerOtherDrop());

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, approverManagerArray);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        managerQLid.setAdapter(adapter2);
        //added date and time pickers
        fromDate.setOnClickListener(this);
        toDate.setOnClickListener(this);
        timePicker.setOnClickListener(this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Submit Button Logic Start
                if (selectPick == 3) {
                    //   displayDropLocation_textView.setText("");
                    sourceAddress = editText_otherPickUp.getText().toString();
                } else
                    sourceAddress = displayPickupLocation_textView.getText().toString();

                if (selectDrop == 3)
                    dropAddress = editText_otherDrop.getText().toString();
                else
                    dropAddress = displayDropLocation_textView.getText().toString();


                if (spinner_location.getSelectedItem().toString() == "Select"
                        || spinner_dropLocation.getSelectedItem().toString() == "Select") {
                    Snackbar snackbar = Snackbar.make(nsv, "Select Valid Location", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }

                if (validation()) {
                    sendRequest();//Calling the RestAPI here
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
                String dateFormat = "yyyy-MM-dd";
//                datePicker.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
//                datePicker.setText();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);


                String date = simpleDateFormat.format(new Date(year - 1900, monthOfYear, dayOfMonth));

                //own code starts    status:- working
                String status = null;
                Date userselected = new Date();
                UnscheduledReqFragment.this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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

    private void methodToSelectDateTwo(final EditText datePicker) {

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
                String dateFormat = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
                String date = simpleDateFormat.format(new Date(year - 1900, monthOfYear, dayOfMonth));

                //own code starts
                String status = null;
                Date userselected = new Date();
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
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
                    //<editor-fold desc="rahul- if number of hours are less than 10 it will now work">
                    if (minute < 10 ) {
                        if(hourOfDay<10)
                        {  timePicker.setText("0" + hourOfDay + ":0" + minute);
                            startTime = "0" + hourOfDay + ":0" + minute;}else{timePicker.setText("" + hourOfDay + ":0" + minute);
                            startTime = "" + hourOfDay + ":0" + minute;

                        }
                    } else {  if(hourOfDay<10){
                        timePicker.setText("0" + hourOfDay + ":" + minute);
                        startTime = "0" + hourOfDay + ":" + minute;
                    }else{
                        timePicker.setText("" + hourOfDay + ":" + minute);
                        startTime = "" + hourOfDay + ":" + minute;
                    }
                        //</editor-fold>
                    }}}
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
                methodToSelectDateTwo(toDate);
                break;
            case R.id.time_picker:
                methodToSelectTime(timePicker);
                break;
        }
    }

    public Boolean validation() {
        if (TextUtils.isEmpty(startDate)) {
            Snackbar snackbar = Snackbar.make(nsv, "From Date Can't be empty", Snackbar.LENGTH_LONG);
            snackbar.show();

        } else {
            if (TextUtils.isEmpty(endDate)) {
                Snackbar snackbar = Snackbar.make(nsv, "To Date Can't be empty", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                if (TextUtils.isEmpty(sourceAddress)) {
                    Snackbar snackbar = Snackbar.make(nsv, "Enter or select Valid Source Address", Snackbar.LENGTH_LONG);
                    snackbar.show();

                } else {

                    if (TextUtils.isEmpty(startTime)) {
                        Snackbar snackbar = Snackbar.make(nsv, "Time Can't be empty", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else {
                        if (TextUtils.isEmpty(dropAddress)) {
                            Snackbar snackbar = Snackbar.make(nsv, "Enter or select Valid Destination Address", Snackbar.LENGTH_LONG);
                            snackbar.show();

                        } else if (TextUtils.isEmpty(reasonForRequest.getText().toString())) {
                            reasonForRequest.setError("Can't be empty");
                            Snackbar snackbar = Snackbar.make(nsv, " Reason Can't be empty", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    //<editor-fold desc="Spinner Class to get the Pickup Location">
    class PickupLocationSpinner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //System.out.println(position);
            selectPick = position;
            if (position == 0) {
                displayDropLocation_textView.setText("");
                displayPickupLocation_textView.setVisibility(View.GONE);
                editText_otherPickUp.setVisibility(View.GONE);
            }
            if (position == 1) {
                // destination_entry_source = "HOME TO ";
                from_address = "HOME";
                displayPickupLocation_textView.setText("");
                displayPickupLocation_textView.setText(Employee_HomeAddress);
                displayPickupLocation_textView.setVisibility(View.VISIBLE);
                //textView_selectTime.setText(R.string.field_drop_time);
                editText_otherPickUp.setVisibility(View.GONE);

            }
            if (position == 2) {
                //   destination_entry_source = "OFFICE TO ";
                from_address = "OFFICE";
                displayPickupLocation_textView.setText("");
                displayPickupLocation_textView.setText(Employee_OfficeAddress);
                displayPickupLocation_textView.setVisibility(View.VISIBLE);
                editText_otherPickUp.setVisibility(View.GONE);
                //textView_selectTime.setText(R.string.field_pickup_time);
            }


            if (position == 3) {
                // destination_entry_source = "OTHERS TO ";
                from_address = "OTHERS";
                // textView_selectTime.setText("Pickup Time");
                displayPickupLocation_textView.setText("");
                displayPickupLocation_textView.setVisibility(View.GONE);
                editText_otherPickUp.setVisibility(View.VISIBLE);
                spinner_dropLocation.setVisibility(View.VISIBLE);
                editText_otherPickUp.getText();
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
    //</editor-fold>

    //<editor-fold desc="Spiner class to get the Drop Location">
    class LocationSpinnerOtherDrop implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            System.out.println(position);
            selectDrop = position;
            if (position == 0) {
                displayDropLocation_textView.setText("");
                destination_entry_source = "";
                textView_selectTime.setText(R.string.field_pickup_time);
                displayDropLocation_textView.setVisibility(View.GONE);
                editText_otherDrop.setVisibility(View.GONE);

            }
            if (position == 1) {
                displayDropLocation_textView.setVisibility(View.VISIBLE);
                editText_otherDrop.setVisibility(View.GONE);
                destination_entry_to = "HOME";
                textView_selectTime.setText(R.string.field_pickup_time);
                displayDropLocation_textView.setText(Employee_HomeAddress);

            }
            if (position == 2) {
                destination_entry_to = "OFFICE";
                displayDropLocation_textView.setVisibility(View.VISIBLE);
                textView_selectTime.setText(R.string.field_drop_time);
                editText_otherDrop.setVisibility(View.GONE);
                displayDropLocation_textView.setText("");
                displayDropLocation_textView.setText(Employee_OfficeAddress);

            }
            if (position == 3) {
                destination_entry_to = "OTHERS";
                displayDropLocation_textView.setVisibility(View.GONE);
                displayDropLocation_textView.setText("");
                textView_selectTime.setText(R.string.field_pickup_time);
                editText_otherDrop.setVisibility(View.VISIBLE);
                editText_otherDrop.getText();
            }

            destination_entry_source = from_address + " TO " + destination_entry_to;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
    //</editor-fold>


    class ApprovalManagerQlid implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if (position == 0) {
                Counter = 1;
                managerQLid_textField.setText(new StringBuilder().append(Employee_Manager_1_Name).append("  Qlid: ").append(Employee_Manager_1_Qlid).toString());
                managerQLid_textField.setVisibility(View.VISIBLE);
            }
            if (position == 1) {
                managerQLid_textField.setText(new StringBuilder().append(Employee_Manager_2_Name).append("  Qlid: ").append(Employee_Manager_2_Qlid).toString());
                managerQLid_textField.setVisibility(View.VISIBLE);
                Counter = 2;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public void getComponentsId() {
        managerQLid_textField = (TextView) rootView.findViewById(R.id.TextView_managerQLID);
        displayPickupLocation_textView = (TextView)rootView.findViewById(R.id.display_location_spinner);
        displayDropLocation_textView = (TextView)rootView.findViewById(R.id.text_dropLocation);
        submit = (Button) rootView.findViewById(R.id.btn_submit); //Submit Button
        fromDate = (EditText) rootView.findViewById(R.id.text_fromDate);
        toDate = (EditText) rootView.findViewById(R.id.text_ToDate);
        timePicker = (EditText)rootView.findViewById(R.id.time_picker);
        reasonForRequest = (EditText)rootView.findViewById(R.id.text_reasonForRequest);
        nsv = (ScrollView) rootView.findViewById(R.id.nestedsv);
        textView_selectTime = (TextView) rootView.findViewById(R.id.textView_selectTime);
        spinner_dropLocation = (Spinner) rootView.findViewById(R.id.spinner_other);
        editText_otherPickUp = (EditText) rootView.findViewById(R.id.toOtherPickupLocation);
        editText_otherDrop = (EditText) rootView.findViewById(R.id.otherDropLocation);

        spinner_location = (Spinner) rootView.findViewById(R.id.spinner_location);
        managerQLid = (Spinner) rootView.findViewById(R.id.text_managerQLID);
        spinner_location.setOnItemSelectedListener(new PickupLocationSpinner());
        managerQLid.setOnItemSelectedListener(new ApprovalManagerQlid());

    }

    public void sendRequest() {
        diff = to_date.getTime() - from_date.getTime();
        diffDays = diff / (24 * 60 * 60 * 1000) + 1;
        if ((validation()) && (diffDays > 0)) {

            //<editor-fold desc="code to set the Progress bar">
            progressBar = new ProgressDialog(getActivity(), 0);
            progressBar.setTitle("Wait");
            progressBar.setMessage("Sending request..");
            progressBar.show();
            progressBar.setCanceledOnTouchOutside(false);
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
            builder.setCancelable(false);
            builder.setTitle("Confirmation").setMessage("You are requesting cab for " + diffDays + " " + day_st + " , Do you want to continue")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            if (validation()) {
                                jsonBody = new JSONObject();
                                try {
                                    jsonBody.put("Emp_QLID", Employee_Qlid);
                                    jsonBody.put("Employee_Name", Employee_Name);
                                    jsonBody.put("Shift_ID", "4");
                                    jsonBody.put("Mgr_QLID", Employee_Manager_1_Qlid);
                                    jsonBody.put("Employee_Manager_1_Name", Employee_Manager_1_Name);
                                    jsonBody.put("Counter", Counter);
                                    jsonBody.put("Source", sourceAddress);
                                    jsonBody.put("Destination", dropAddress);
                                    jsonBody.put("Mgr_QLID_Level2", Employee_Manager_2_Qlid);
                                    jsonBody.put("Employee_Manager_2_Name", Employee_Manager_2_Name);
                                    jsonBody.put("Other_Addr", destination_entry_source);
                                    jsonBody.put("Reason", reasonForRequest.getText().toString());
                                    jsonBody.put("Start_Date_Time", startDate +" "+ startTime+":00");
                                    jsonBody.put("End_Date_Time", endDate);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, mainUrl, jsonBody, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.i("VOLLEY", "inside onResponse method:UnscheduledRequest");
                                        Log.i("VOLLEY", response.toString());

                                        try {
                                            if (response.getString("status").equalsIgnoreCase("success")) {
                                                // Toast.makeText(getActivity(), "Your request is Submitted", Toast.LENGTH_LONG).show();
                                                final AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
                                                alertDialog.setTitle("Alert!!");
                                                alertDialog.setMessage(getString(R.string.alert_request_submitted));
                                                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                                                alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        alertDialog.cancel();
}
                                                });
                                                alertDialog.show();

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
                                        //  Toast.makeText(getActivity(), "Oops..Something Went wrong", Toast.LENGTH_SHORT).show();
                                        Snackbar snackbar = Snackbar.make(nsv, "Oops Something Went Wrong Failed To Submit your Request", Snackbar.LENGTH_LONG);
                                        snackbar.setAction("Retry", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                sendRequest();
                                            }
                                        });
                                        snackbar.show();
                                        error.printStackTrace();
                                    }
                                });
                                /*jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
*/
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


    public void getData() {

        Cursor c = mSqLiteDatabase.rawQuery("SELECT * FROM " + EmployeeContract.DB_TABLE, null);
        while (c.moveToNext()) {
            Employee_Qlid = c.getString(c.getColumnIndex(EmployeeContract.COLUMN_EMP_QLID));
            Employee_Name = c.getString(c.getColumnIndex(EmployeeContract.COLUMN_FIRST_NAME))
                    + c.getString(c.getColumnIndex(EmployeeContract.COLUMN_LAST_NAME));
            Employee_Contact_number = c.getString(c.getColumnIndex(EmployeeContract.COLUMN_CONTACT_NUMBER));
            Employee_HomeAddress = c.getString(c.getColumnIndex(EmployeeContract.COLUMN_HOME_ADDRESS));
            Employee_OfficeAddress = c.getString(c.getColumnIndex(EmployeeContract.COLUMN_OFFICE_ADDRESS));
            Employee_Manager_1_Qlid = c.getString(c.getColumnIndex(EmployeeContract.COLUMN_LEVEL_1_MANAGER));
            Employee_Manager_2_Qlid = c.getString(c.getColumnIndex(EmployeeContract.COLUMN_LEVEL_2_MANAGER));
            Employee_Manager_1_Name = c.getString(c.getColumnIndex(EmployeeContract.COLUMN_LEVEL_1_MANAGER_NAME));
            Employee_Manager_2_Name = c.getString(c.getColumnIndex(EmployeeContract.COLUMN_LEVEL_2_MANAGER_NAME));
        }
        c.close();

    }

}

