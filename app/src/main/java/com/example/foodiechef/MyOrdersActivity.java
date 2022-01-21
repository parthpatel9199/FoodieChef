package com.example.foodiechef;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;

public class MyOrdersActivity extends AppCompatActivity {

    private ImageView back;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        back = findViewById(R.id.menu);
        viewPager = findViewById(R.id.viewpager4);
        MyOrderPageAdapter myOrderPageAdapter = new MyOrderPageAdapter(getBaseContext(),getSupportFragmentManager());
        viewPager.setAdapter(myOrderPageAdapter);
        tabLayout = findViewById(R.id.tab_layout_order);
        tabLayout.setupWithViewPager(viewPager);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onResume() {
        MyOrderPageAdapter myOrderPageAdapter = new MyOrderPageAdapter(getBaseContext(),getSupportFragmentManager());
        viewPager.setAdapter(myOrderPageAdapter);
        tabLayout = findViewById(R.id.tab_layout_order);
        tabLayout.setupWithViewPager(viewPager);
        super.onResume();
    }
}
