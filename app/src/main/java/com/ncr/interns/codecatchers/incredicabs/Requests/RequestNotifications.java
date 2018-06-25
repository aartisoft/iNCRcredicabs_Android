package com.ncr.interns.codecatchers.incredicabs.Requests;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ncr.interns.codecatchers.incredicabs.R;

public class RequestNotifications extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_notifications);
        Toolbar toolbar =  findViewById(R.id.toolbar_requestnotification);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Requests");
        setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_my_requests:
                        fragment = new MyRequests();
                        break;
                    case R.id.navigation_requests_to_approve:
                        fragment = new RequestsToApprove();
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.request_frame, fragment);
                transaction.commit();
                return true;
            }
        });
        Fragment fragment = new MyRequests();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.request_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
 }

}
