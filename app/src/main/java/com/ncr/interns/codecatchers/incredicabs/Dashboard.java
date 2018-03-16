package com.ncr.interns.codecatchers.incredicabs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ncr.interns.codecatchers.incredicabs.Adapter.*;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.EmployeeCabMatesDetails;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    LinearLayout linearLayout;
    RecyclerView mRecyclerView;
    ArrayList<EmployeeCabMatesDetails> mList;
    Button checkIn,checkOut,Complaints,request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prepareCabmatesDetails();

        checkIn = findViewById(R.id.button_checkIn);
        checkOut = findViewById(R.id.button_checkOut);
        Complaints = findViewById(R.id.button_complaint);
        request = findViewById(R.id.button_request);
        linearLayout = findViewById(R.id.dashboard_linerarParent);
        mRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        CabMatesAdapter adapter = new CabMatesAdapter(mList);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new
                DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(adapter);

        checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 3/15/2018 CHeckIn Intent
            }
        });

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 3/15/2018  CheckOut Intent
            }
        });

        Complaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 3/15/2018 Feedback Activity Intent
            }
        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this,MainRequestActivity.class);
                startActivity(intent);
            }
        });




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.call_transport){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("tel:7895305782"));
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_call_transport_one) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("tel:7895305782"));
            startActivity(intent);

        } else if (id == R.id.nav_call_transport_two) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("tel:7895305782"));
            startActivity(intent);

        } else if (id == R.id.nav_app_feedback) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, Uri.parse("mailto:gauravsati1997@gmail.com "));
            intent.putExtra(Intent.EXTRA_SUBJECT,"Feedback about iNCRedicabs");
            intent.putExtra(Intent.EXTRA_TEXT, "Enter Your Helpful Feedback");
            startActivity(Intent.createChooser(intent, "Send Email with"));

        } else if (id == R.id.nav_about_developers) {

        } else if(id == R.id.LogOut){
            Snackbar snackbar = Snackbar.make(linearLayout,"Logging Out",Snackbar.LENGTH_LONG);
            snackbar.show();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void prepareCabmatesDetails()
    {
        mList = new ArrayList<>();
        mList.add(new EmployeeCabMatesDetails("Pulkit Gupta","Saraswati Kunj, Golf Cource road, Sector 53","8:00AM",
                "987654321"));
        mList.add(new EmployeeCabMatesDetails("Gaurav Sati","NCR Corporation Vipul Plaza","8:10AM",
                "987654321"));
        mList.add(new EmployeeCabMatesDetails("Sonia Chawla","NCR Corporation Vipul Plaza","8:40AM",
                "987654321"));
        mList.add(new EmployeeCabMatesDetails("Neeraj joshi","NCR Corporation Vipul Plaza","9:00AM",
                "987654321"));
        mList.add(new EmployeeCabMatesDetails("Abhinav Gunwant","NCR Corporation Vipul Plaza","9:05AM",
                "987654321"));
    }


}
