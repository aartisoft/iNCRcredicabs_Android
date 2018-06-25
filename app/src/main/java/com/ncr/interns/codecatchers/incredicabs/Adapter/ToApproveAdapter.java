package com.ncr.interns.codecatchers.incredicabs.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ncr.interns.codecatchers.incredicabs.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ToApproveAdapter extends RecyclerView.Adapter<ToApproveAdapter.ToApproveViewHolder> {

    JSONArray requestsArray;
    JSONObject responseObject;
    int length;
    Context context;
    public ToApproveAdapter() {

    }
    public ToApproveAdapter(JSONObject api_response,Context ctx) {
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
       TextView employee_name,from_date,to_date,cab_request_time,
                source_address,destination_address,reason_for_request,request_status;
       Button btn_approve,btn_reject;

        public ToApproveViewHolder(View itemView) {
            super(itemView);
            employee_name = itemView.findViewById(R.id.employee_name);
            from_date = itemView.findViewById(R.id.from_date);
            to_date = itemView.findViewById(R.id.to_date);
            cab_request_time = itemView.findViewById(R.id.cab_request_time);
            source_address  = itemView.findViewById(R.id.source_address);
            destination_address = itemView.findViewById(R.id.destination_address);
            reason_for_request = itemView.findViewById(R.id.reason_for_request);
            request_status = itemView.findViewById(R.id.request_status);
            btn_approve = itemView.findViewById(R.id.button_approve);
            btn_reject = itemView.findViewById(R.id.button_reject);
        }
    }
    @Override
    public ToApproveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_to_approve ,parent, false);
        return new ToApproveAdapter.ToApproveViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ToApproveViewHolder holder, int position) {
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
            } else if(Approval.equals("REJECTED")){
                holder.request_status.setText(Approval);
                holder.request_status.setTextColor(Color.RED);
                holder.btn_approve.setVisibility(View.GONE);
                holder.btn_reject.setVisibility(View.GONE);
            }
            holder.btn_approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 6/25/2018 Approve Request via Broadcast Receiver
                    Toast.makeText(context, "Request Approved for "+Emp_name, Toast.LENGTH_SHORT).show();
                }
            });
            holder.btn_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 6/25/2018 Reject request via Broadcast Receiver
                    Toast.makeText(context, "Request Rejected for "+Emp_name, Toast.LENGTH_SHORT).show();
                }
            });


            //}
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return length;
    }


}
