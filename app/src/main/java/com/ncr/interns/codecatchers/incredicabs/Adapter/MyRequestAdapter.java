package com.ncr.interns.codecatchers.incredicabs.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ncr.interns.codecatchers.incredicabs.R;

public class MyRequestAdapter extends RecyclerView.Adapter<MyRequestAdapter.myRequestViewHolder> {

    public static class myRequestViewHolder extends RecyclerView.ViewHolder{
       TextView employee_name,from_date,to_date,cab_request_time,source_address,destination_address,reason_for_request,request_status;

        public myRequestViewHolder(View itemView) {
            super(itemView);
            employee_name = itemView.findViewById(R.id.employee_name);
            from_date = itemView.findViewById(R.id.from_date);
            to_date = itemView.findViewById(R.id.to_date);
            cab_request_time = itemView.findViewById(R.id.cab_request_time);
            source_address  = itemView.findViewById(R.id.source_address);
            destination_address = itemView.findViewById(R.id.destination_address);
            reason_for_request = itemView.findViewById(R.id.reason_for_request);
            request_status = itemView.findViewById(R.id.request_status);
        }
    }

    @Override
    public MyRequestAdapter.myRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myrequest ,parent, false);
        return new MyRequestAdapter.myRequestViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MyRequestAdapter.myRequestViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }
}
