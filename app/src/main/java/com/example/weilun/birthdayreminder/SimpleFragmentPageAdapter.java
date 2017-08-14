package com.example.weilun.birthdayreminder;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Wei Lun on 8/7/2017.
 */

public class SimpleFragmentPageAdapter extends FragmentPagerAdapter {

    private Context context;

    public SimpleFragmentPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new UpComingBirthdayFragment();
            case 1:
                return new ContactListFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0)
            return context.getString(R.string.tab_title_upcoming);
        else
            return context.getString(R.string.tab_title_contact);
    }
}
