package com.example.foodiechef.ui.cart;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodiechef.CheckoutActivity;
import com.example.foodiechef.ChefProfileActivity;
import com.example.foodiechef.R;
import com.example.foodiechef.ui.Chef_cart;
import com.example.foodiechef.ui.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RecyclerAdapter_Chef_cart extends RecyclerView.Adapter<RecyclerAdapter_Chef_cart.MyViewHolder> {

    private Context context;
    private List<Chef_cart> chef_carts;
    private DatabaseReference database;

    public RecyclerAdapter_Chef_cart(Context context, List<Chef_cart> chef_carts) {
        this.context = context;
        this.chef_carts = chef_carts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        database = FirebaseDatabase.getInstance().getReference();
        v = LayoutInflater.from(context).inflate(R.layout.item_cart_chefwise, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Chef_cart chef_cart = chef_carts.get(position);
        database.child("users").child(chef_cart.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.chef_name.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        RecyclerAdapter_Cart recyclerAdapter = new RecyclerAdapter_Cart(context, chef_cart.getCart());
        holder.rv.setLayoutManager(new LinearLayoutManager(context));
        holder.rv.setAdapter(recyclerAdapter);

        MyOnClickListener listener = new MyOnClickListener(chef_cart);
        holder.checkout.setOnClickListener(listener);
        holder.open_chef.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return chef_carts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView chef_name;
        private TextView open_chef;
        private RecyclerView rv;
        private LinearLayout checkout;

        public MyViewHolder(View itemView) {
            super(itemView);
            chef_name = (TextView) itemView.findViewById(R.id.chef_name);
            open_chef = (TextView) itemView.findViewById(R.id.openProfile);
            checkout = (LinearLayout)itemView.findViewById(R.id.checkout);
            rv = (RecyclerView) itemView.findViewById(R.id.cart_details);
        }
    }

    public class MyOnClickListener implements View.OnClickListener{

        Chef_cart chef_cart;

        public MyOnClickListener(Chef_cart chef_cart) {
            this.chef_cart = chef_cart;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();

            if(id == R.id.checkout){
                Intent intent = new Intent(context, CheckoutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ID", chef_cart.getId() + "");
                context.startActivity(intent);
            }
            else if(id == R.id.openProfile){
                Intent intent = new Intent(context, ChefProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ID",chef_cart.getId()+"");
                intent.putExtra("from","cart");
                context.startActivity(intent);
            }
        }
    }
}
