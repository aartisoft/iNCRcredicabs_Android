package com.ncr.interns.codecatchers.incredicabs.Adapter;

/**
 * Created by gs250365 on 3/15/2018.
 */

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.EmployeeCabMatesDetails;
import com.ncr.interns.codecatchers.incredicabs.R;

import java.util.ArrayList;

/**
 * Created by gs250365 on 3/15/2018.
 */

public class CabMatesAdapter extends Adapter<CabMatesAdapter.cabMatesViewHolder> {

    ArrayList<EmployeeCabMatesDetails> matesList  = new ArrayList<>();
    public CabMatesAdapter(ArrayList<EmployeeCabMatesDetails> list) {
        matesList = list;
    }

    public static class cabMatesViewHolder extends RecyclerView.ViewHolder {

        TextView name,address,pickupTime,contactNumber;


        public cabMatesViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.employeeName);
            address = itemView.findViewById(R.id.Emp_pickupAddress);
            pickupTime = itemView.findViewById(R.id.emp_pickupTime);

        }
    }

    @Override
    public CabMatesAdapter.cabMatesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cabmates,parent,false);
        return new cabMatesViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(CabMatesAdapter.cabMatesViewHolder holder, int position) {
        EmployeeCabMatesDetails currentItem = matesList.get(position);
        holder.name.setText(currentItem.getCabMate_name());
        holder.address.setText(currentItem.getCabMate_address());
        holder.pickupTime.setText(currentItem.getCabMate_pickupTime());



    }

    @Override
    public int getItemCount() {
        return matesList.size();
    }
}

