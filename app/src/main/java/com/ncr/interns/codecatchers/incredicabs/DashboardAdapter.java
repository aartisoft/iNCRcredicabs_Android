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
    private CardView cardViewDashboard;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        ImageView thumbnail;

        MyViewHolder(View view) {
            super(view);
            title =  view.findViewById(R.id.title);
            thumbnail =  view.findViewById(R.id.thumbnail);
            cardViewDashboard= view.findViewById(R.id.card_view_dashboard);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position ==0)
                    {
                        // TODO: 3/12/2018 Request Team4
                        Intent intent = new Intent(mContext, MainRequestActivity.class);
                        mContext.startActivity(intent);
                    }
                    if(position ==1)
                    {
                        // TODO: 3/12/2018 Team 1 SOS 
                    }
                    if(position ==2)
                    {
                        // TODO: 3/12/2018 Team 3 FeedBAck 
                    }
                    if(position ==3)
                    {
                        // TODO: 3/12/2018 Team 1 CHeck-in
                    }
                    if(position ==4)
                    {
                        // TODO: 3/12/2018 Team 1 Check-out
                    }
                    if(position ==5)
                    {
                        // TODO: 3/12/2018 Roster team  
                    }

                }
            });

        }
    }


     DashboardAdapter(Context mContext, List<DashboardEntry> DashboardEntryList) {
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


     /*   cardViewDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.title.getText()=="REQUEST"){
                    Intent intent = new Intent(mContext, MainRequestActivity.class);
                    mContext.startActivity(intent);
                }else{
                Toast.makeText(mContext, holder.title.getText(), Toast.LENGTH_SHORT).show();}
            }
        });*/
//        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(holder.title.getText()=="REQUEST")
//                {
//                    Intent intent = new Intent(mContext, MainRequestActivity.class);
//                    mContext.startActivity(intent);
//                }
//                Toast.makeText(mContext, holder.title.getText(), Toast.LENGTH_SHORT).show();
//            }
//        });




    }


    @Override
    public int getItemCount() {
        return DashboardEntryList.size();
    }
}
