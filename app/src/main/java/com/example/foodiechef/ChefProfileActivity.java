package com.example.foodiechef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodiechef.ui.User;
import com.example.foodiechef.ui.profile.ProfilePageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChefProfileActivity extends AppCompatActivity {

    private CircleImageView chef_image;
    private ImageView back;
    private TextView username;
    private DatabaseReference database;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_profile);

        database = FirebaseDatabase.getInstance().getReference();
        intent = getIntent();
        back = findViewById(R.id.backToChef);
        chef_image = findViewById(R.id.chef_image);
        username = findViewById(R.id.username);
        ViewPager viewPager = findViewById(R.id.viewpager3);
        String profile = "chef";
        ProfilePageAdapter pagerAdapter = new ProfilePageAdapter(getApplicationContext(),getSupportFragmentManager(),profile);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tab_layout_profile);
        tabLayout.setupWithViewPager(viewPager,true);
        if(getIntent().getStringExtra("from") != null){
            viewPager.setCurrentItem(1);
        }

        setProfile();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setProfile() {
        database.child("users").child(intent.getStringExtra("ID"))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        editor.putString("chefID",user.getId());
                        editor.putString("chefName",user.getName());
                        editor.putString("chefEmail",user.getEmail());
                        editor.putString("chefMobile",user.getMobile());
                        editor.putString("chefImage",user.getImage());
                        editor.putString("chefLocation",user.getLocation());
                        editor.putString("chefBio",user.getBiography());
                        editor.commit();
                        if (user.getImage().equals("1")){
                            chef_image.setImageResource(R.drawable.git);
                        }
                        else {
                            Picasso.get().load(user.getImage()).placeholder(R.drawable.git).into(chef_image);
                        }
//                        username.setText(user.getName());
                        username.setText(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("chefName","abc"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
