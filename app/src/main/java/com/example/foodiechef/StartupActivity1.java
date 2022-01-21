package com.example.foodiechef;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class StartupActivity1 extends AppCompatActivity {

    private TextView skip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup1);

        skip = findViewById(R.id.skip);
        ViewPager viewPager = findViewById(R.id.viewpager1);
        StartPageAdapter pagerAdapter = new StartPageAdapter(getApplicationContext(), getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager,true);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkipToLogin();
            }
        });
    }

    private void SkipToLogin() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(preferences.getString("login","no").equals("yes")){
            Intent intent = new Intent(StartupActivity1.this,DashboardActivity.class);
            startActivity(intent);
            finish();
        }
        else if (preferences.getString("login","no").equals("no")){
            Intent intent = new Intent(StartupActivity1.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
