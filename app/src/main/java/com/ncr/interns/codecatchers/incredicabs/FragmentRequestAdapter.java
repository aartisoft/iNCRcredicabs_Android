package com.ncr.interns.codecatchers.incredicabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by gs250365 on 2/17/2018.
 */

public class FragmentRequestAdapter extends FragmentPagerAdapter {
    public FragmentRequestAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0){
            return new RegularReqFragment();
        }
        else if(position == 1){
            return new ScheduledReqFragment();
        }
        else
            return new UnscheduledReqFragment() ;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position)
        {
            case 0: return "Regular Cabs";

            case 1: return "Scheduled Cabs";

            case 2: return "Unscheduled Cabs";
        }

        return super.getPageTitle(position);
    }
}
