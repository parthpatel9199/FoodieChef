package com.example.foodiechef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.foodiechef.ui.Food;
import com.example.foodiechef.ui.RecyclerAdapter_Favourite;
import com.example.foodiechef.ui.RecyclerAdapter_Food;
import com.example.foodiechef.ui.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    private ImageView back;
    private RecyclerView rv;
    private List<String> foods;
    private DatabaseReference database;
    public static RecyclerAdapter_Favourite recyclerAdapter_favourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        database = FirebaseDatabase.getInstance().getReference();
        back = findViewById(R.id.menu);
        rv = findViewById(R.id.fav_list);

        foods = new ArrayList<>();

        database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        for (String f : user.getFavourite()){
                            foods.add(f);
                        }
                        recyclerAdapter_favourite = new RecyclerAdapter_Favourite(getApplicationContext(), foods);
                        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        rv.setAdapter(recyclerAdapter_favourite);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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
