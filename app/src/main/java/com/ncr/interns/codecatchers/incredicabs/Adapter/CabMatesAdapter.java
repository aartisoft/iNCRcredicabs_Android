package com.ncr.interns.codecatchers.incredicabs.Adapter;

/**
 * Created by gs250365 on 3/15/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ncr.interns.codecatchers.incredicabs.Dashboard;

import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.CabMatesContract;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.EmployeeCabMatesDetails;
import com.ncr.interns.codecatchers.incredicabs.R;

import java.util.ArrayList;

/**
 * Created by gs250365 on 3/15/2018.
 */

public class CabMatesAdapter extends Adapter<CabMatesAdapter.cabMatesViewHolder> {


    Cursor cursor;
    Context ctx;
    private static final int REQUEST_CALL = 1;
    String mobNum;
    private final static String TAG = "CABMATESADAPTER";

    public CabMatesAdapter(Cursor c,Context context) {
        cursor = c;
        ctx = context;
    }
    public CabMatesAdapter() {

    }

    public static class cabMatesViewHolder extends RecyclerView.ViewHolder {

        TextView name, address, pickupTime;
        ImageButton contactNumber;

         public cabMatesViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.employeeName);
            address = itemView.findViewById(R.id.Emp_pickupAddress);
            pickupTime = itemView.findViewById(R.id.emp_pickupTime);
            contactNumber = itemView.findViewById(R.id.button_call_cabMate);
          }
    }

    @Override
    public CabMatesAdapter.cabMatesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cabmates, parent, false);
        return new cabMatesViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(CabMatesAdapter.cabMatesViewHolder holder, int position) {

        if (!cursor.moveToPosition(position)) {
            return;
        }
        String emp_name = cursor.getString(cursor.getColumnIndex(CabMatesContract.COLUMN_CABMATE_NAME));
        String emp_address = cursor.getString(cursor.getColumnIndex(CabMatesContract.COLUMN_CABMATE_ADDRESS));
        String emp_pickupTime = cursor.getString(cursor.getColumnIndex(CabMatesContract.COLUMN_CABMATE_PICKUPTIME));
       // String emp_cabNumber = cursor.getString(cursor.getColumnIndex(CabMatesContract.COLUMN_CABMATE_CAB_NUMBER));
        final String emp_contact_number=cursor.getString(cursor.getColumnIndex(CabMatesContract.COLUMN_CABMATE_CONTACT_NUMBER));
        holder.name.setText(emp_name);
        holder.address.setText(emp_address);
        holder.pickupTime.setText(emp_pickupTime);
        //Log.d(TAG, "onBindViewHolder: "+emp_cabNumber);
        holder.contactNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobNum = emp_contact_number;
                makePhoneCall();

            }
        });

    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    private void makePhoneCall(){
        String mobNumber = mobNum;
       // this.mobNum = mobNum;
        if(true){
            if(ContextCompat.checkSelfPermission((Activity)ctx,
                    android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions( (Activity)ctx,
                        new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            }else{
                String dial = "tel:"+mobNumber;
                ctx.startActivity(new Intent(Intent.ACTION_CALL,Uri.parse(dial)));
            }
        }else{
            Toast.makeText(ctx, "Phone Number not Specified", Toast.LENGTH_SHORT).show();
        }

    }




}

