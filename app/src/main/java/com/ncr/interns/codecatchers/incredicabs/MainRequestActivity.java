package com.ncr.interns.codecatchers.incredicabs;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainRequestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_request);

        ViewPager viewPager = findViewById(R.id.viewPager);
        FragmentRequestAdapter fragmentRequestAdapter = new FragmentRequestAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentRequestAdapter);

        TabLayout tabLayout = findViewById(R.id.slider_view);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.request_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }
}
