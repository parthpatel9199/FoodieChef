package com.example.foodiechef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.foodiechef.ui.RecyclerAdapter_Review;
import com.example.foodiechef.ui.Review;
import com.example.foodiechef.ui.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    private RecyclerView rv;
    private ImageView back;
    private DatabaseReference database;
    private List<Review> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        back = findViewById(R.id.back);
        rv = findViewById(R.id.review_list);
        database = FirebaseDatabase.getInstance().getReference();
        reviews = new ArrayList<>();

        if(getIntent().getStringExtra("ID") == null) {
            database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            reviews = user.getReviews();

                            RecyclerAdapter_Review recyclerAdapter = new RecyclerAdapter_Review(getApplicationContext(), reviews);
                            rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            rv.setAdapter(recyclerAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }else {
            database.child("users").child(getIntent().getStringExtra("ID"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            reviews = user.getReviews();

                            RecyclerAdapter_Review recyclerAdapter = new RecyclerAdapter_Review(getApplicationContext(), reviews);
                            rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            rv.setAdapter(recyclerAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

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
}
