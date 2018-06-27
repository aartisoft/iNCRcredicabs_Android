package com.ncr.interns.codecatchers.incredicabs.Adapter;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ncr.interns.codecatchers.incredicabs.NCABUtils.Environment;
import com.ncr.interns.codecatchers.incredicabs.NCABUtils.RESTService;
import com.ncr.interns.codecatchers.incredicabs.R;
import com.ncr.interns.codecatchers.incredicabs.notification.Approve;
import com.ncr.interns.codecatchers.incredicabs.notification.Reject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ToApproveAdapter extends RecyclerView.Adapter<ToApproveAdapter.ToApproveViewHolder> {

    JSONArray requestsArray;
    JSONObject responseObject;
    int length;
    Context context;
    SharedPreferences sharedPreferences;
    private static final String MY_PREFERENCES = "MyPrefs";
    public static final String ACTION1 = "Approve";
    public static final String ACTION2 = "Reject";

    public ToApproveAdapter() {

    }

    public ToApproveAdapter(JSONObject api_response, Context ctx) {
        responseObject = api_response;
        context = ctx;
        try {
            requestsArray = responseObject.getJSONArray("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        length = requestsArray.length();
    }

    public static class ToApproveViewHolder extends RecyclerView.ViewHolder {
        TextView employee_name, from_date, to_date, cab_request_time,
                source_address, destination_address, reason_for_request, request_status;
        Button btn_approve, btn_reject;

        public ToApproveViewHolder(View itemView) {
            super(itemView);
            employee_name = itemView.findViewById(R.id.employee_name);
            from_date = itemView.findViewById(R.id.from_date);
            to_date = itemView.findViewById(R.id.to_date);
            cab_request_time = itemView.findViewById(R.id.cab_request_time);
            source_address = itemView.findViewById(R.id.source_address);
            destination_address = itemView.findViewById(R.id.destination_address);
            reason_for_request = itemView.findViewById(R.id.reason_for_request);
            request_status = itemView.findViewById(R.id.request_status);
            btn_approve = itemView.findViewById(R.id.button_approve);
            btn_reject = itemView.findViewById(R.id.button_reject);
        }
    }

    @Override
    public ToApproveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_to_approve, parent, false);
        return new ToApproveAdapter.ToApproveViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ToApproveViewHolder holder, int position) {
        Intent action1Intent = new Intent(context, Approve.class).setAction(ACTION1);
        Intent action2Intent = new Intent(context, Reject.class).setAction(ACTION2);
        PendingIntent action1PendingIntent = PendingIntent.getBroadcast(context, 0,
                action1Intent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent action2PendingIntent = PendingIntent.getBroadcast(context, 0,
                action2Intent, PendingIntent.FLAG_ONE_SHOT);


        try {
            requestsArray = responseObject.getJSONArray("result");
            //for (int i = 0; i < requestsArray.length(); i++) {
            JSONObject requestObject = requestsArray.getJSONObject(position);
            final String Emp_name = requestObject.getString("Emp_Name").trim();
            String End_Date_Time = requestObject.getString("End_Date_Time");
            String End_Date_Array[] = End_Date_Time.split(" ");
            String End_Date = End_Date_Array[0];
            String Start_Date_Time = requestObject.getString("Start_Date_Time").trim();
            String Start_Date_Array[] = Start_Date_Time.split(" ");
            String StartDate = Start_Date_Array[0];
            String CabTime = Start_Date_Array[1];
            String Rqst_Date_Time = requestObject.getString("Rqst_Date_Time");
            String Source = requestObject.getString("Source").trim();
            String Destination = requestObject.getString("Destination").trim();
            String reason = requestObject.getString("Reason").trim();
            String Approval = requestObject.getString("Approval").trim();
            final String RequestId = requestObject.getString("Req_Id").trim();
            holder.employee_name.setText(Emp_name);
            holder.from_date.setText(StartDate);
            holder.to_date.setText(End_Date);
            holder.cab_request_time.setText(CabTime);
            holder.source_address.setText(Source);
            holder.destination_address.setText(Destination);
            holder.reason_for_request.setText(reason);
            holder.btn_approve.setVisibility(View.GONE);
            holder.btn_reject.setVisibility(View.GONE);
            if (Approval.equals("UNAPPROVED")) {
                holder.request_status.setText(Approval);
                holder.request_status.setTextColor(Color.GRAY);
                holder.btn_approve.setVisibility(View.VISIBLE);
                holder.btn_reject.setVisibility(View.VISIBLE);
            } else if (Approval.equals("APPROVED")) {
                holder.request_status.setText(Approval);
                holder.request_status.setTextColor(Color.GREEN);
                holder.btn_approve.setVisibility(View.GONE);
                holder.btn_reject.setVisibility(View.GONE);
            } else if (Approval.equals("REJECTED")) {
                holder.request_status.setText(Approval);
                holder.request_status.setTextColor(Color.RED);
                holder.btn_approve.setVisibility(View.GONE);
                holder.btn_reject.setVisibility(View.GONE);
            }
            holder.btn_approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Confirm");
                    alertDialog.setMessage("Are you sure you want to APPROVE the request?");
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SURE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO: 6/26/2018 Positive Action Button
                            approveRequest(RequestId);
                           // Toast.makeText(context, "Request Approved", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO: 6/26/2018 Negative Action Button
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            });
            holder.btn_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Confirm");
                    alertDialog.setMessage("Are you sure you want to REJECT the request?");
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SURE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO: 6/26/2018 Positive Action Button
                            rejectRequest(RequestId);
                           // Toast.makeText(context, "Request Rejected", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO: 6/26/2018 Negative Action Button
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return length;
    }

    private void approveRequest(String requestId) {
        String url = Environment.URL_REQUEST_APPROVE;
        JSONObject jsonBodyRequest = new JSONObject();
        try {
            jsonBodyRequest.put("request_id", requestId);
            jsonBodyRequest.put("Approval", "APPROVED");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonBodyRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("ToApproveAdapter", "inside onResponse method : approveRequest");
                        Log.i("ToApproveAdapter", response.toString());
                        try {
                            if (response.getString("status").equalsIgnoreCase("success")) {
                                Toast.makeText(context, "Unscheduled Cab request APPROVED", Toast.LENGTH_LONG).show();
                            } else {
                                if (response.getString("status").equalsIgnoreCase("Already")) {
                                    Toast.makeText(context, "Unscheduled Cab request APPROVED", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, "Failed to submit", Toast.LENGTH_LONG).show();
                                }
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
                        Log.d("ToApproveAdapter", "ERROR METHOD : Something went wrong");
                        error.printStackTrace();
                    }
                });
        RESTService.getInstance(context).addToRequestQueue(jsonObjRequest);
    }

    private void rejectRequest(String requestId) {

        String url = Environment.URL_REQUEST_REJECT;
        JSONObject jsonBodyRequest = new JSONObject();
        try {
            jsonBodyRequest.put("request_id", requestId);
            jsonBodyRequest.put("Approval", "REJECTED");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                jsonBodyRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("ToApproveAdapter", "inside onResponse method:doLogin");
                        Log.i("ToApproveAdapter", response.toString());
                        try {
                            if (response.getString("status").equalsIgnoreCase("success")) {
                                Toast.makeText(context, "Unscheduled Cab request REJECTED", Toast.LENGTH_LONG).show();
                            } else {
                                if (response.getString("status").equalsIgnoreCase("Already")) {
                                    Toast.makeText(context, "Unscheduled Cab request already REJECTED", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, "Failed to submit", Toast.LENGTH_LONG).show();
                                }
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
        RESTService.getInstance(context).addToRequestQueue(jsonObjRequest);
    }
}
