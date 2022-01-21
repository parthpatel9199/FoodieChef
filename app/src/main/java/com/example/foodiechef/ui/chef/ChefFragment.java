package com.example.foodiechef.ui.chef;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.foodiechef.NavigationActivity;
import com.example.foodiechef.R;
import com.example.foodiechef.ui.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChefFragment extends Fragment {
    private ChefViewModel chefViewModel;
    private RecyclerView rv;
    private ImageView menu;
    private List<User> users;

    private DatabaseReference database;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chefViewModel = ViewModelProviders.of(this).get(ChefViewModel.class);
        View root = inflater.inflate(R.layout.fragment_chef, container, false);

        users = new ArrayList<>();

        database = FirebaseDatabase.getInstance().getReference();
        menu = root.findViewById(R.id.menu);
        rv = root.findViewById(R.id.chef_list);

        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    User user = child.getValue(User.class);
                    if(user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){}
                    else {
                        users.add(user);
                    }
                }
                RecyclerAdapter_Chef recyclerAdapter = new RecyclerAdapter_Chef(getActivity(), users);
                int NumberOfColumn = 2;
                GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), NumberOfColumn);
                rv.setLayoutManager(mLayoutManager);
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
