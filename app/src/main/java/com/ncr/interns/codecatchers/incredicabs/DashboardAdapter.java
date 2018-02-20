package com.ncr.interns.codecatchers.incredicabs;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.MyViewHolder> {

    private Context mContext;
    private List<DashboardEntry> DashboardEntryList;
    CardView cardViewDashboard;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            cardViewDashboard=(CardView)view.findViewById(R.id.card_view_dashboard);

        }
    }


    public DashboardAdapter(Context mContext, List<DashboardEntry> DashboardEntryList) {
        this.mContext = mContext;
        this.DashboardEntryList = DashboardEntryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dashboard_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        DashboardEntry dashboardEntry = DashboardEntryList.get(position);
        holder.title.setText(dashboardEntry.getName());
        holder.thumbnail.setImageResource(dashboardEntry.getThumbnail());

        // loading DashboardEntry cover using Glide library
        //Glide.with(mContext).load(dashboardEntry.getThumbnail()).into(holder.thumbnail);
        cardViewDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.title.getText()=="REQUEST")
                {
                    Intent intent = new Intent(mContext, MainRequestActivity.class);
                    mContext.startActivity(intent);
                }
                Toast.makeText(mContext, holder.title.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.title.getText()=="REQUEST")
                {
                    Intent intent = new Intent(mContext, MainRequestActivity.class);
                    mContext.startActivity(intent);
                }
                Toast.makeText(mContext, holder.title.getText(), Toast.LENGTH_SHORT).show();
            }
        });




    }


    @Override
    public int getItemCount() {
        return DashboardEntryList.size();
    }
}
