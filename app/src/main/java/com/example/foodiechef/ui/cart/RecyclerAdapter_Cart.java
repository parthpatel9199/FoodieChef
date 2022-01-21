package com.example.foodiechef.ui.cart;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodiechef.R;
import com.example.foodiechef.ui.Cart;
import com.example.foodiechef.ui.Chef_cart;
import com.example.foodiechef.ui.Food;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.example.foodiechef.ui.cart.CartFragment.chef_carts;
import static com.example.foodiechef.ui.cart.CartFragment.recyclerAdapter;
import static com.example.foodiechef.ui.cart.CartFragment.rv;


public class RecyclerAdapter_Cart extends RecyclerView.Adapter<RecyclerAdapter_Cart.MyViewHolder> {

    private Context context;
    public List<Cart> carts;
    private String chef_id;
    private DatabaseReference database;

    public RecyclerAdapter_Cart(Context context, List<Cart> carts) {
        this.context = context;
        this.carts = carts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        database = FirebaseDatabase.getInstance().getReference();
        v = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Cart cart = carts.get(position);
        holder.cart_price.setText(cart.getPrice());
        holder.cart_quantity.setText(cart.getItem_quantity() + "");

        database.child("foods").child(cart.getItemID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Food food = dataSnapshot.getValue(Food.class);
                chef_id = food.getUserID();
                if(food.getStatus() == 1){
                    holder.delete_item.performClick();
                }else {
                    holder.cart_name.setText(food.getName());
                    Picasso.get().load(food.getImage()).placeholder(R.drawable.load).into(holder.food_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        MyOnClickListener listener = new MyOnClickListener(cart);
        holder.delete_item.setOnClickListener(listener);
        holder.add_item.setOnClickListener(listener);
        holder.remove_item.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView cart_name;
        private TextView cart_price;
        private TextView cart_quantity;
        private ImageView add_item;
        private ImageView remove_item;
        private ImageView delete_item;
        private ImageView food_image;

        public MyViewHolder(View itemView) {
            super(itemView);
            cart_name = (TextView) itemView.findViewById(R.id.food_name);
            cart_price = (TextView) itemView.findViewById(R.id.food_price);
            cart_quantity = (TextView) itemView.findViewById(R.id.quantity);
            add_item = (ImageView) itemView.findViewById(R.id.add_item);
            remove_item = (ImageView) itemView.findViewById(R.id.remove_item);
            delete_item = (ImageView) itemView.findViewById(R.id.delete_item);
            food_image = (ImageView) itemView.findViewById(R.id.food_image);
        }
    }

    public class MyOnClickListener implements View.OnClickListener {

        public Cart cart;

        public MyOnClickListener(Cart cart) {
            this.cart = cart;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.delete_item) {
                DeleteFromCart();
            } else if (id == R.id.add_item) {
                AddQuantity();
            } else if (id == R.id.remove_item) {
                RemoveQuantity();
            }
        }

        public void DeleteFromCart() {
            final String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            for (Chef_cart chef_cart : chef_carts) {
                if (chef_cart.getId().equals(chef_id)) {
                    for (Cart cart1 : chef_cart.getCart()) {
                        if (cart1.getItemID().equals(cart.getItemID())) {
                            chef_cart.getCart().remove(cart1);
                            break;
                        }
                    }
                    if (chef_cart.getCart().size() == 0) {
                        chef_carts.remove(chef_cart);
                        database.child("carts").child(uID).child(chef_id).removeValue();
                        recyclerAdapter.notifyDataSetChanged();
                        break;
                    } else {
                        database.child("carts").child(uID).child(chef_id).setValue(chef_cart);
                        recyclerAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }

        private void AddQuantity() {
            final String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            for (Chef_cart chef_cart : chef_carts) {
                if (chef_cart.getId().equals(chef_id)) {
                    for (Cart cart1 : chef_cart.getCart()) {
                        if (cart1.getItemID().equals(cart.getItemID())) {
                            int price = Integer.parseInt(cart1.getPrice()) + Integer.parseInt(cart1.getPrice()) / cart1.item_quantity;
                            cart1.setPrice(price + "");
                            cart1.item_quantity += 1;
                            break;
                        }
                    }
                    database.child("carts").child(uID).child(chef_id).setValue(chef_cart);
                    recyclerAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }

        public void RemoveQuantity() {
            final String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            for (Chef_cart chef_cart : chef_carts) {
                if (chef_cart.getId().equals(chef_id)) {
                    for (Cart cart1 : chef_cart.getCart()) {
                        if (cart1.getItemID().equals(cart.getItemID())) {
                            int price = Integer.parseInt(cart1.getPrice()) - Integer.parseInt(cart1.getPrice()) / cart1.item_quantity;
                            cart1.setPrice(price + "");
                            cart1.item_quantity -= 1;
                            if (cart1.item_quantity == 0) {
                                chef_cart.getCart().remove(cart1);
                            }
                            break;
                        }
                    }
                    if (chef_cart.getCart().size() == 0) {
                        chef_carts.remove(chef_cart);
                        database.child("carts").child(uID).child(chef_id).removeValue();
                        recyclerAdapter.notifyDataSetChanged();
                        break;
                    } else {
                        database.child("carts").child(uID).child(chef_id).setValue(chef_cart);
                        recyclerAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }
}
