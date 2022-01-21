package com.example.foodiechef;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.foodiechef.ui.buy.BuyFragment;

public class MyOrderPageAdapter extends FragmentPagerAdapter {

    private final Context context;
    private String tabTitles[] = new String[]{"My Orders","Order History"};

    public MyOrderPageAdapter(Context context,@NonNull FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Bundle bundle = new Bundle();
                bundle.putString("buy","buy");
                BuyFragment buyFragment = new BuyFragment();
                buyFragment.setArguments(bundle);
                return buyFragment;
            case 1:
                Bundle bundle1 = new Bundle();
                bundle1.putString("buy","sell");
                BuyFragment buyFragment1 = new BuyFragment();
                buyFragment1.setArguments(bundle1);
                return buyFragment1;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
