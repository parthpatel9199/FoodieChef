package com.example.foodiechef.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.foodiechef.ui.about.AboutFragment;
import com.example.foodiechef.Fragment_order;
import com.example.foodiechef.ui.history.HistoryFragment;
import com.example.foodiechef.ui.history.HistoryViewModel;

public class ProfilePageAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    private String profile;
    private String tabTitles[];


    public ProfilePageAdapter(Context context,@NonNull FragmentManager fm,String profile) {

        super(fm);
        mContext = context;
        this.profile = profile;
        if (profile.equals("chef")){
            tabTitles = new String[]{"About","Recipes"};
        }
        else {
            tabTitles = new String[]{"About","Foods"};
        }

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Bundle bundle = new Bundle();
                bundle.putString("profile",profile);
                AboutFragment aboutFragment = new AboutFragment();
                aboutFragment.setArguments(bundle);
                return aboutFragment;
            case 1:
                Bundle bundle1 = new Bundle();
                bundle1.putString("profile",profile);
                HistoryFragment historyFragment = new HistoryFragment();
                historyFragment.setArguments(bundle1);
                return historyFragment;
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

