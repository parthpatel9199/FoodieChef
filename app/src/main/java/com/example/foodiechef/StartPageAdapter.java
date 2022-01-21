package com.example.foodiechef;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class StartPageAdapter extends FragmentPagerAdapter {

    private final Context mContext;

    public StartPageAdapter(Context context,@NonNull FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                BrowseFragment browse = new BrowseFragment();
                return browse;
            case 1:
                Fragment_order order = new Fragment_order();
                return order;
            case 2:
                FragmentEnjoy enjoy = new FragmentEnjoy();
                return enjoy;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
