package com.example.weilun.birthdayreminder;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Created by Wei Lun on 8/7/2017.
 */

public class SimpleFragmentPageAdapter extends FragmentPagerAdapter {

    SparseArray<Fragment> registeredFragments = new SparseArray<>();
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

            case 2:
                return new QuoteFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        if (position == 0)
//            return context.getString(R.string.tab_title_upcoming);
//        else if (position == 1)
//            return context.getString(R.string.tab_title_contact);
//        else
//            return context.getString(R.string.tab_title_quote);
//    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
