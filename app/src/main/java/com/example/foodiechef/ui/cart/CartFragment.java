package com.example.foodiechef.ui.cart;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodiechef.NavigationActivity;
import com.example.foodiechef.R;
import com.example.foodiechef.ui.Cart;
import com.example.foodiechef.ui.Chef_cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment {

    private CartViewModel cartViewModel;
    public static RecyclerView rv;
    private ImageView menu;
    public static List<Chef_cart> chef_carts;
    public static RecyclerAdapter_Chef_cart recyclerAdapter;
    private DatabaseReference database;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cartViewModel =
                ViewModelProviders.of(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        menu = root.findViewById(R.id.menu);
        rv = root.findViewById(R.id.cart_list);
        chef_carts = new ArrayList<>();

        String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.child("carts").child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    Chef_cart chef_cart = child.getValue(Chef_cart.class);
                    chef_carts.add(chef_cart);
                }
                recyclerAdapter = new RecyclerAdapter_Chef_cart(getContext(),chef_carts);
                rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                rv.setAdapter(recyclerAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationActivity.getDrawer();
            }
        });

        return root;
    }

}