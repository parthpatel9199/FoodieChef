package com.example.foodiechef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodiechef.ui.Cart;
import com.example.foodiechef.ui.Chef_cart;
import com.example.foodiechef.ui.Food;
import com.example.foodiechef.ui.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.foodiechef.ui.cart.CartFragment.chef_carts;
import static com.example.foodiechef.ui.cart.CartFragment.recyclerAdapter;

public class OrderCompleteActivity extends AppCompatActivity {

    private ImageView back;
    private TextView order_id,order_price,order_list;
    private Button Done;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_complete);

        database = FirebaseDatabase.getInstance().getReference();
        Done = findViewById(R.id.Done);
        back = findViewById(R.id.back);
        order_id = findViewById(R.id.order_id);
        order_price = findViewById(R.id.order_price);
        order_list = findViewById(R.id.order_list);

        order_id.setText(getIntent().getStringExtra("orderID"));
        order_price.setText(getIntent().getStringExtra("orderPrice"));
        order_list.setText(getIntent().getStringExtra("orderDetail"));

        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCart();
                onBackPressed();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCart();
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    private void deleteCart(){
        for (Chef_cart chef_cart : chef_carts) {
            if (chef_cart.getId().equals(getIntent().getStringExtra("ID"))) {
                chef_carts.remove(chef_cart);
                break;
            }
        }
        database.child("carts").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getIntent().getStringExtra("ID")).removeValue();
        recyclerAdapter.notifyDataSetChanged();
    }
}
