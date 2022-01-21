package com.example.foodiechef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.foodiechef.ui.Food;
import com.example.foodiechef.ui.RecyclerAdapter_Food;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.foodiechef.FoodActivity.status;

public class CategoryActivity extends AppCompatActivity {

    private List<Food> foods;
    private String temp;
    private DatabaseReference database;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        database = FirebaseDatabase.getInstance().getReference();
        temp = getIntent().getStringExtra("title");
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView backtohome = findViewById(R.id.backToHome);
        ImageView cate_layout = findViewById(R.id.app_bar_image);
        rv = findViewById(R.id.food_list);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        foods = new ArrayList<>();

        database.child("foods").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    if(child.getValue(Food.class).getCategory().equals(temp) && !child.getValue(Food.class).getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    && child.getValue(Food.class).getStatus() != 1) {
                        foods.add(child.getValue(Food.class));
                    }
                }
                RecyclerAdapter_Food recyclerAdapter = new RecyclerAdapter_Food(getApplicationContext(), foods);
                rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rv.setAdapter(recyclerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        collapsingToolbar.setTitle(getIntent().getStringExtra("title"));

        if (temp.equals(getResources().getString(R.string.breakfast))){
            cate_layout.setImageResource(R.drawable.breakfast);
        }else if (temp.equals(getResources().getString(R.string.lunch))){
            cate_layout.setImageResource(R.drawable.lunch);
        }else if (temp.equals(getResources().getString(R.string.dinner))){
            cate_layout.setImageResource(R.drawable.dinner);
        }else if (temp.equals(getResources().getString(R.string.fastfood))){
            cate_layout.setImageResource(R.drawable.fastfood);
        }

        backtohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(status != null && status.equals("url")){
            startActivity(new Intent(CategoryActivity.this,DashboardActivity.class));
        }else {
            this.finish();
        }
    }

}
