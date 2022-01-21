package com.example.foodiechef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.foodiechef.ui.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class NavigationActivity extends AppCompatActivity implements View.OnClickListener {
    public static FrameLayout frame;
    public static DuoDrawerLayout drawerLayout;
    public static CircleImageView user_image;
    private LinearLayout sign_out, dashboard, myorder, favourite;
    public static TextView user_name;
    private Intent intent;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation2);

        mAuth = FirebaseAuth.getInstance();
        drawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer_layout);
        frame = (FrameLayout) findViewById(R.id.frame);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        user_image = (CircleImageView) findViewById(R.id.user_image);
        user_name = (TextView) findViewById(R.id.user_name);
        sign_out = (LinearLayout) findViewById(R.id.sign_out);
        dashboard = (LinearLayout) findViewById(R.id.dashboard);
        myorder = (LinearLayout) findViewById(R.id.myorder);
        favourite = (LinearLayout) findViewById(R.id.favourite);

        sign_out.setOnClickListener(this);
        dashboard.setOnClickListener(this);
        myorder.setOnClickListener(this);
        favourite.setOnClickListener(this);

        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        toolbar.setVisibility(View.GONE);
        drawerLayout.setDrawerListener(duoDrawerToggle);

        duoDrawerToggle.syncState();
    }

    public static void getDrawer() {
        if (drawerLayout.isDrawerOpen()) {
            drawerLayout.closeDrawer();
        } else {
            drawerLayout.openDrawer();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.sign_out:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("login", "no");
                editor.commit();
                mAuth.signOut();
                Toast.makeText(getApplicationContext(), "Sign Out", Toast.LENGTH_LONG).show();
                intent = new Intent(NavigationActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
                break;
            case R.id.dashboard:
                getDrawer();
                break;
            case R.id.myorder:
                getDrawer();
                intent = new Intent(NavigationActivity.this, MyOrdersActivity.class);
                startActivity(intent);
                break;
            case R.id.favourite:
                getDrawer();
                intent = new Intent(NavigationActivity.this, FavouriteActivity.class);
                startActivity(intent);
                break;
        }
    }


}
