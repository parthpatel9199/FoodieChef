package com.example.foodiechef.ui.buy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodiechef.CheckoutActivity;
import com.example.foodiechef.DashboardActivity;
import com.example.foodiechef.OrderDetailsActivity;
import com.example.foodiechef.R;
import com.example.foodiechef.ui.Cart;
import com.example.foodiechef.ui.Chef_cart;
import com.example.foodiechef.ui.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter_Order extends RecyclerView.Adapter<RecyclerAdapter_Order.MyViewHolder> {

    private Context context;
    private List<Order> orders;
    private DatabaseReference database;
    private String b;

    public RecyclerAdapter_Order(Context context, List<Order> orders, String b) {
        this.context = context;
        this.orders = orders;
        this.b = b;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        database = FirebaseDatabase.getInstance().getReference();
        v = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Order order = orders.get(position);
        holder.order_id.setText(position+1 + "");
        holder.order_price.setText(order.getPrice());

        String pattern = "dd MMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(order.getOrder_date());
        holder.order_date.setText(date);

        if (order.getDelivery_type().equals("delivery")) {
            holder.delivery_type.setImageResource(R.drawable.ic_delivery);
        } else {
            holder.delivery_type.setImageResource(R.drawable.ic_pickup);
        }

        if (b.equals("sell")) {
            holder.repeat.setVisibility(View.GONE);
            if(order.getStatus().equals("0")){
                holder.status.setText("Recevied");
            }else if (order.getStatus().equals("1")){
                holder.status.setText("Verifing");
            }else if(order.getStatus().equals("2")){
                holder.status.setText("Preparing");
            }else if(order.getStatus().equals("3")){
                holder.status.setText("Prepared");
            }else if(order.getStatus().equals("4")) {
                holder.status.setText("Delivered");
            }else if(order.getStatus().equals("99")){
                holder.status.setText("Rejected");
            }
        }else {
            if(order.getStatus().equals("0")){
                holder.status.setText("Placed");
            }else if (order.getStatus().equals("1")){
                holder.status.setText("Accepted");
            }else if(order.getStatus().equals("2")){
                holder.status.setText("Preparing");
            }else if(order.getStatus().equals("3")) {
                holder.status.setText("Prepared");
            }else if(order.getStatus().equals("4")){
                holder.status.setText("Delivered");
            }else if(order.getStatus().equals("99")){
                holder.status.setText("Rejected");
            }
        }

        MyOnClickListener listener = new MyOnClickListener(order);
        holder.order_detail.setOnClickListener(listener);

        holder.repeat.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView order_id;
        private TextView order_price ,status;
        private ImageView delivery_type;
        private ImageView order_detail;
        private TextView order_date;
        private TextView repeat;

        public MyViewHolder(View itemView) {
            super(itemView);
            order_id = (TextView) itemView.findViewById(R.id.order_id);
            order_price = (TextView) itemView.findViewById(R.id.order_price);
            status = (TextView) itemView.findViewById(R.id.status);
            order_date = (TextView) itemView.findViewById(R.id.order_date);
            delivery_type = (ImageView) itemView.findViewById(R.id.delivery_type);
            order_detail = (ImageView) itemView.findViewById(R.id.order_det);
            repeat = (TextView) itemView.findViewById(R.id.repeat);
        }
    }

    public class MyOnClickListener implements View.OnClickListener {

        public Order order;
        private Intent intent;

        public MyOnClickListener(Order order) {
            this.order = order;
        }

        @Override
        public void onClick(View v) {
            int x = v.getId();
            if (x == R.id.order_det) {
                intent = new Intent(context, OrderDetailsActivity.class);
                if(b.equals("sell")){
                    intent.putExtra("chef",order.getCustomer_id());
                }
                else {
                    intent.putExtra("chef",order.getChef_id());
                }
                intent.putExtra("buy", b);
                intent.putExtra("ID", order.getId());
                context.startActivity(intent);
            } else if (x == R.id.repeat) {
                AddToCart();
            }
        }

        private void AddToCart() {
            final String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            database.child("carts").child(uID).child(order.getChef_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Chef_cart chef_cart = dataSnapshot.getValue(Chef_cart.class);
                            if (chef_cart == null) {
                                chef_cart = new Chef_cart();
                                chef_cart.setId(order.getChef_id());
                                List<Cart> carts = order.getCarts();
                                chef_cart.setCart(carts);
                                Toast.makeText(context, "Added to Cart", Toast.LENGTH_LONG).show();
                            } else {
                                List<Cart> carts = chef_cart.getCart();
                                List<Cart> temporary = order.getCarts();
                                for(Cart cart : carts){
                                    for(Cart temp : order.getCarts()){
                                        if(cart.getItemID().equals(temp.getItemID())){
                                            temporary.remove(temp);
                                            break;
                                        }
                                    }
                                }
                                carts.addAll(temporary);
                                chef_cart.setCart(carts);
                            }
                            database.child("carts").child(uID).child(order.getChef_id()).setValue(chef_cart);
                            intent = new Intent(context, DashboardActivity.class);
                            intent.putExtra("from","repeat");
                            context.startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }
}
