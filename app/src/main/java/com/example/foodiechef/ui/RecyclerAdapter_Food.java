package com.example.foodiechef.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodiechef.DashboardActivity;
import com.example.foodiechef.FoodActivity;
import com.example.foodiechef.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class RecyclerAdapter_Food extends RecyclerView.Adapter<RecyclerAdapter_Food.MyViewHolder> {

    private Context context;
    private List<Food> foods;
    private DatabaseReference database;

    public RecyclerAdapter_Food(Context context, List<Food> foods) {
        this.context = context;
        this.foods = foods;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        database = FirebaseDatabase.getInstance().getReference();
        v = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Food food = foods.get(position);
        holder.food_id = food.getId();
        holder.food_name.setText(food.getName());
        holder.food_ingrident.setText(food.getIngredient());
        holder.food_price.setText(food.getPrice());
        holder.food_time.setText(food.getTime());
        Picasso.get().load(food.getImage()).placeholder(R.drawable.load).into(holder.food_image);
        //holder.food_image.setImageResource(food.getImage());

        MyOnClickListener listener = new MyOnClickListener(food);
        holder.food_image.setOnClickListener(listener);
        holder.food_name.setOnClickListener(listener);
        holder.addToCart.setOnClickListener(listener);
        holder.favourite_food.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public String food_id;
        private TextView food_name;
        private TextView food_ingrident;
        private TextView food_price;
        private TextView food_time;
        private ImageView food_image;
        private CardView favourite_food,addToCart;

        public MyViewHolder(View itemView) {
            super(itemView);
            food_name = (TextView) itemView.findViewById(R.id.food_name);
            food_ingrident = (TextView) itemView.findViewById(R.id.food_ingrident);
            food_price = (TextView) itemView.findViewById(R.id.food_price);
            food_time = (TextView) itemView.findViewById(R.id.food_time);
            addToCart = (CardView) itemView.findViewById(R.id.addToCart);
            favourite_food = (CardView) itemView.findViewById(R.id.favourite_item);
            food_image = (ImageView) itemView.findViewById(R.id.food_image);
        }
    }

    public class MyOnClickListener implements View.OnClickListener {

        public Food food;
        public String foodId;
        private Intent intent;

        public MyOnClickListener(Food food) {
            this.food = food;
            this.foodId = food.getId();
        }

        @Override
        public void onClick(View v) {
            int x = v.getId();
            if (x == R.id.addToCart) {
                AddToCart();
            } else if (x == R.id.favourite_item) {
                AddToFav();
            } else {
                intent = new Intent(context, FoodActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ID", foodId + "");
                context.startActivity(intent);
            }
        }

        private void AddToCart() {
            final String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            database.child("carts").child(uID).child(food.getUserID())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Chef_cart chef_cart = dataSnapshot.getValue(Chef_cart.class);
                            if (chef_cart == null) {
                                chef_cart = new Chef_cart();
                                chef_cart.setId(food.getUserID());
                                List<Cart> carts = new ArrayList<>();
                                Cart cart = new Cart();
                                cart.setItemID(foodId);
                                cart.setItem_quantity();
                                cart.setPrice(food.getPrice());
                                carts.add(cart);
                                chef_cart.setCart(carts);
                                Toast.makeText(context, "Added to Cart", Toast.LENGTH_LONG).show();
                            } else {
                                List<Cart> carts = chef_cart.getCart();
                                for (Cart item : carts) {
                                    if (item.getItemID().equals(foodId)) {
                                        Toast.makeText(context, "Already in Cart", Toast.LENGTH_LONG).show();
                                        break;
                                    } else {
                                        Toast.makeText(context, "Added to Cart", Toast.LENGTH_LONG).show();
                                        Cart cart = new Cart();
                                        cart.setItemID(foodId);
                                        cart.setPrice(food.getPrice());
                                        cart.setItem_quantity();
                                        carts.add(cart);
                                        chef_cart.setCart(carts);
                                        break;
                                    }
                                }
                            }
                            database.child("carts").child(uID).child(food.getUserID()).setValue(chef_cart);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

        private void AddToFav() {
            final List<String> faves = new ArrayList<>();
            database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            for (String f : user.getFavourite()) {
                                faves.add(f);
                            }
                            if (faves.contains(foodId)) {
                                Toast.makeText(context, "Already in Favourite", Toast.LENGTH_LONG).show();
                            } else {
                                faves.add(foodId);
                                Toast.makeText(context, "Add to Favourite", Toast.LENGTH_LONG).show();
                            }
                            database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favourite").setValue(faves);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }
}
