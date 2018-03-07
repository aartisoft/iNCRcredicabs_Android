package com.ncr.interns.codecatchers.incredicabs;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
;

public class MainRequestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_request);

        ViewPager viewPager = findViewById(R.id.viewPager);
        FragmentRequestAdapter fragmentRequestAdapter = new FragmentRequestAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentRequestAdapter);
        Toolbar toolbar = findViewById(R.id.my_Toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

//        TabLayout tabLayout = findViewById(R.id.slider_view);
//        tabLayout.setupWithViewPager(viewPager);
    }
}
