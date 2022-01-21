package com.example.foodiechef.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodiechef.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerAdapter_Checkout extends RecyclerView.Adapter<RecyclerAdapter_Checkout.MyViewHolder> {

    private Context context;
    private List<Cart> carts;
    private DatabaseReference database;

    public RecyclerAdapter_Checkout(Context context, List<Cart> carts) {
        this.context = context;
        this.carts = carts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        database = FirebaseDatabase.getInstance().getReference();
        v = LayoutInflater.from(context).inflate(R.layout.item_checkout, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Cart cart = carts.get(position);

        holder.food_quantity.setText(cart.getItem_quantity()+"");
        holder.food_price.setText(cart.getPrice());
        database.child("foods").child(cart.getItemID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Food food = dataSnapshot.getValue(Food.class);
                holder.food_name.setText(food.getName());
                Picasso.get().load(food.getImage()).placeholder(R.drawable.load).into(holder.food_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView food_name;
        private TextView food_price;
        private TextView food_quantity;
        private ImageView food_image;

        public MyViewHolder(View itemView) {
            super(itemView);
            food_name = (TextView) itemView.findViewById(R.id.food_name);
            food_price = (TextView) itemView.findViewById(R.id.price);
            food_quantity = (TextView) itemView.findViewById(R.id.food_quantity);
            food_image = (ImageView) itemView.findViewById(R.id.food_image);
        }
    }
}
