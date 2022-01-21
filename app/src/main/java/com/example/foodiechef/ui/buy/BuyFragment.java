package com.example.foodiechef.ui.buy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodiechef.R;
import com.example.foodiechef.ui.Cart;
import com.example.foodiechef.ui.Food;
import com.example.foodiechef.ui.Order;
import com.example.foodiechef.ui.cart.RecyclerAdapter_Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BuyFragment extends Fragment {

    private BuyViewModel buyViewModel;
    private RecyclerView rv;
    private List<Order> orders;
    private DatabaseReference database;
    private List<Cart> carts;
    private List<Food> foods;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        buyViewModel = ViewModelProviders.of(this).get(BuyViewModel.class);
        View root = inflater.inflate(R.layout.fragment_buy, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        orders = new ArrayList<>();

        rv = root.findViewById(R.id.order_list);

        Bundle argument = getArguments();
        if (argument.getString("buy").equals("sell")) {
            database.child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot child : dataSnapshot.getChildren()){
                                for(DataSnapshot item : child.getChildren()){
                                    if(item.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        for(DataSnapshot grand : item.getChildren()){
                                            Order order = grand.getValue(Order.class);
                                            orders.add(order);
                                        }
                                    }
                                }
                            }
                            RecyclerAdapter_Order recyclerAdapter = new RecyclerAdapter_Order(getContext(), orders, "sell");
                            rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                            rv.setAdapter(recyclerAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            database.child("orders").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot child : dataSnapshot.getChildren()){
                                for(DataSnapshot item : child.getChildren()){
                                    Order order = item.getValue(Order.class);
                                    orders.add(order);
                                }
                            }
                            RecyclerAdapter_Order recyclerAdapter = new RecyclerAdapter_Order(getContext(), orders, "buy");
                            rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                            rv.setAdapter(recyclerAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }

        return root;
    }
}
