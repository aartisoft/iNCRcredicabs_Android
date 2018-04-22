package com.ncr.interns.codecatchers.incredicabs;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
;

public class MainRequestActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_REQUEST_ACTIVITY_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_request);
        Log.d(TAG, "onCreate: inside on create");
       /* ViewPager viewPager = findViewById(R.id.viewPager);
        FragmentRequestAdapter fragmentRequestAdapter = new FragmentRequestAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentRequestAdapter);
       */
        Toolbar toolbar = findViewById(R.id.my_Toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        UnscheduledReqFragment fragment = new UnscheduledReqFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
       // fragmentTransaction.add(fragment,"Unscheduled req fragment");
        fragmentTransaction.add(R.id.viewPager,fragment);
        fragmentTransaction.commit();

//        TabLayout tabLayout = findViewById(R.id.slider_view);
//        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: inside onpause");
        finish();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: inside onStop");
    }
}
