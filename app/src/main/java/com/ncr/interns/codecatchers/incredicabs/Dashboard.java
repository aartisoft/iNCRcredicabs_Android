package com.ncr.interns.codecatchers.incredicabs;

import android.content.Intent;
import android.os.Bundle;
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

import com.ncr.interns.codecatchers.incredicabs.Adapter.*;
import com.ncr.interns.codecatchers.incredicabs.NCABdatabase.EmployeeCabMatesDetails;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

        mRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        CabMatesAdapter adapter = new CabMatesAdapter(mList);
        mRecyclerView.setLayoutManager(linearLayoutManager);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

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
    }


}
