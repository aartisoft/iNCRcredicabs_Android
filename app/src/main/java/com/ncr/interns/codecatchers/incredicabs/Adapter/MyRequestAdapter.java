package com.ncr.interns.codecatchers.incredicabs.Adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ncr.interns.codecatchers.incredicabs.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyRequestAdapter extends RecyclerView.Adapter<MyRequestAdapter.myRequestViewHolder> {


    JSONArray requestsArray;
    JSONObject responseObject;
    int length;

    public MyRequestAdapter(JSONObject api_response) {
        responseObject = api_response;
        try {
            requestsArray = responseObject.getJSONArray("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        length = requestsArray.length();

    }

    public MyRequestAdapter() {
    }

    public static class myRequestViewHolder extends RecyclerView.ViewHolder {
        TextView employee_name, from_date, to_date, cab_request_time,
                source_address, destination_address, reason_for_request, request_status;

        public myRequestViewHolder(View itemView) {
            super(itemView);
            employee_name = itemView.findViewById(R.id.employee_name);
            from_date = itemView.findViewById(R.id.from_date);
            to_date = itemView.findViewById(R.id.to_date);
            cab_request_time = itemView.findViewById(R.id.cab_request_time);
            source_address = itemView.findViewById(R.id.source_address);
            destination_address = itemView.findViewById(R.id.destination_address);
            reason_for_request = itemView.findViewById(R.id.reason_for_request);
            request_status = itemView.findViewById(R.id.request_status);
        }
    }

    @Override
    public myRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myrequest, parent, false);
        return new MyRequestAdapter.myRequestViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(myRequestViewHolder holder, int position) {
        try {
            requestsArray = responseObject.getJSONArray("result");
            //for (int i = 0; i < requestsArray.length(); i++) {
            JSONObject requestObject = requestsArray.getJSONObject(position);
            String Emp_name = requestObject.getString("Emp_Name").trim();
            String End_Date = requestObject.getString("End_Date_Time").trim();
            String Start_Date_Time = requestObject.getString("Start_Date_Time").trim();
            String Start_Date_Array[] = Start_Date_Time.split(" ");
            String StartDate = Start_Date_Array[0];
            String CabTime = Start_Date_Array[1];
            String Rqst_Date_Time = requestObject.getString("Rqst_Date_Time");
            String Source = requestObject.getString("Source").trim();
            String Destination = requestObject.getString("Destination").trim();
            String reason = requestObject.getString("Reason").trim();
            String Approval = requestObject.getString("Approval").trim();
            String RequestId = requestObject.getString("Req_Id").trim();
            holder.employee_name.setText(Emp_name);
            holder.from_date.setText(StartDate);
            holder.to_date.setText(End_Date);
            holder.cab_request_time.setText(CabTime);
            holder.source_address.setText(Source);
            holder.destination_address.setText(Destination);
            holder.reason_for_request.setText(reason);
            if (Approval.equals("UNAPPROVED")) {
                holder.request_status.setText(Approval);
                holder.request_status.setTextColor(Color.GRAY);
            } else if (Approval.equals("APPROVED")) {
                holder.request_status.setText(Approval);
                holder.request_status.setTextColor(Color.GREEN);
            } else if (Approval.equals("REJECTED")) {
                holder.request_status.setText(Approval);
                holder.request_status.setTextColor(Color.RED);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return length;
        //return 20;
        //return requestsArray.length();
    }
}
