package com.ncr.interns.codecatchers.incredicabs.Requests;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonArray;
import com.ncr.interns.codecatchers.incredicabs.Adapter.MyRequestAdapter;
import com.ncr.interns.codecatchers.incredicabs.Login.Login;
import com.ncr.interns.codecatchers.incredicabs.NCABUtils.RESTService;
import com.ncr.interns.codecatchers.incredicabs.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyRequests extends Fragment {

    RecyclerView recyclerView;
    MyRequestAdapter myRequestAdapter;
    View rootView;
    CardView NoRequestsCard;
    Context context;
    JSONObject jsonBodyRequest;
    JSONObject api_response;
    JSONArray requestsArray;
    String Emp_Qlid;
    ProgressDialog progressDialog;
    public static final String TAG = "MYREQUESTS";
    private static final String MY_PREFERENCES = "MyPrefs_login";
    SharedPreferences sharedPreferences;
    String url = "http://ec2-18-219-151-75.us-east-2.compute.amazonaws.com:8080/NCAB/AndroidService/NotificationsByMe";

    public MyRequests() {
        // Required empty public constructor
        context = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Emp_Qlid = getEmployeeQlid();
        setData(Emp_Qlid);
        rootView = inflater.inflate(R.layout.fragment_my_requests, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView_myRequests);
        NoRequestsCard = rootView.findViewById(R.id.MyRequests_no_request_Card);
        return rootView;
    }


    public void setData(String Emp_Qlid) {

        progressDialog = new ProgressDialog(getActivity(), 0);
        progressDialog.setTitle("Getting your data..");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        jsonBodyRequest = new JSONObject();
        try {
            jsonBodyRequest.put("Emp_Qlid", Emp_Qlid);
            Log.d(TAG, "setData: creating JsonBody Object [SUCCESSFUL]");
        } catch (JSONException e) {
            Log.d(TAG, "setData: error in creating JsonBody Object");
            e.printStackTrace();
        }

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                jsonBodyRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "inside onResponse method: MyRequests");
                Log.d(TAG, "RESPONSE MY REQUEST " + response.toString());
                try {
                    if (response.getString("status").equals("success")) {
                        Log.d(TAG, "inside onResponse method: MyRequests : SUCCESS");
                        api_response = response;
                        myRequestAdapter = new MyRequestAdapter(api_response);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(myRequestAdapter);
                        NoRequestsCard.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    } else if (response.getString("status").equals("fail")) {
                        // TODO: 6/25/2018 Handel the case when there are no requests.
                        progressDialog.dismiss();
                       // Toast.makeText(context, "No request Data Available", Toast.LENGTH_SHORT).show();
                        NoRequestsCard.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);

                    }
                } catch (Exception e) {
                    Log.d("", "onResponse: " + e.getMessage());
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RESTService.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
    }

    public String getEmployeeQlid() {
        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String Employee_Qlid = sharedPreferences.getString("user_qlid", "");
        return Employee_Qlid;
    }


}
