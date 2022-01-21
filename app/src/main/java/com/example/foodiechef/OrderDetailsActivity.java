package com.example.foodiechef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodiechef.Services.MyFirebaseInstanceService;
import com.example.foodiechef.ui.Cart;
import com.example.foodiechef.ui.Chef_cart;
import com.example.foodiechef.ui.Food;
import com.example.foodiechef.ui.Order;
import com.example.foodiechef.ui.RecyclerAdapter_Checkout;
import com.example.foodiechef.ui.Review;
import com.example.foodiechef.ui.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderDetailsActivity extends AppCompatActivity {

    private List<Cart> carts;
    private RecyclerView rv;
    private ImageView back, delivery_type;
    private CircleImageView chef_image;
    private String DeliveryType, chef_id, chef_Address, chef_token,buy,ID;
    private TextView repeat, order_id, order_date, order_price, order_status, delivery_name, delivery_add, subtotal_text, delivery_charge, gst,
            chef_name, chef_email, chef_mobile, cancel, done, prepared;
    private Button call;
    private DatabaseReference database;
    public static boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        isOpen =true;
        buy = getIntent().getStringExtra("buy");
        ID = getIntent().getStringExtra("ID");
        database = FirebaseDatabase.getInstance().getReference();
        call = findViewById(R.id.call);
        back = findViewById(R.id.menu);
        cancel = findViewById(R.id.cancel);
        done = findViewById(R.id.proceedToPay);
        prepared = findViewById(R.id.prepared);
        repeat = findViewById(R.id.repeat);
        order_id = findViewById(R.id.order_id);
        order_price = findViewById(R.id.order_price);
        order_date = findViewById(R.id.order_date);
        order_status = findViewById(R.id.status);
        delivery_type = findViewById(R.id.delivery_type);
        delivery_name = findViewById(R.id.delivery_add_type);
        delivery_add = findViewById(R.id.delivery_add);
        subtotal_text = findViewById(R.id.subtotal);
        delivery_charge = findViewById(R.id.delivery_charge);
        gst = findViewById(R.id.gst);
        rv = findViewById(R.id.cart_list);
        chef_image = findViewById(R.id.chef_image);
        chef_name = findViewById(R.id.chef_name);
        chef_email = findViewById(R.id.chef_email);
        chef_mobile = findViewById(R.id.chef_mobile);
        chef_id = getIntent().getStringExtra("chef");
        carts = new ArrayList<>();

        prepared.setVisibility(View.GONE);

        MemoryAllocation();

        SetChefDetails();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buy.equals("sell")) {
                    if (order_status.getText().toString().equals("Recevied")) {
                        database.child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    for (DataSnapshot chef : child.getChildren()) {
                                        if (chef.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                            for (DataSnapshot item : chef.getChildren()) {
                                                Order order = item.getValue(Order.class);
                                                if (order.getId().equals(ID)) {
                                                    order.setStatus("99");
                                                    database.child("orders").child(child.getKey()).child(chef.getKey()).child(item.getKey()).setValue(order);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        sendToCustomer();
                    }
                } else {
                    if (order_status.getText().toString().equals("Accepted")) {
                        database.child("orders").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    for (DataSnapshot item : child.getChildren()) {
                                        Order order = item.getValue(Order.class);
                                        if (order.getId().equals(ID)) {
                                            order.setStatus("99");
                                            database.child("orders").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(child.getKey()).child(item.getKey()).setValue(order);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        sendToChef();
                    }
                }
                MemoryAllocation();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buy.equals("sell")) {
                    if (order_status.getText().toString().equals("Recevied")) {
                        database.child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    for (DataSnapshot chef : child.getChildren()) {
                                        if (chef.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                            for (DataSnapshot item : chef.getChildren()) {
                                                Order order = item.getValue(Order.class);
                                                if (order.getId().equals(ID)) {
                                                    order.setStatus("1");
                                                    database.child("orders").child(child.getKey()).child(chef.getKey()).child(item.getKey()).setValue(order);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        sendToCustomer();
                    }
                } else {
                    if (order_status.getText().toString().equals("Accepted")) {
                        database.child("orders").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    for (DataSnapshot item : child.getChildren()) {
                                        Order order = item.getValue(Order.class);
                                        if (order.getId().equals(ID)) {
                                            order.setStatus("2");
                                            database.child("orders").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(child.getKey()).child(item.getKey()).setValue(order);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        sendToChef();
                    }
                }
                MemoryAllocation();
            }
        });

        prepared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buy.equals("sell")) {
                    if (order_status.getText().toString().equals("Preparing")) {
                        database.child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    for (DataSnapshot chef : child.getChildren()) {
                                        if (chef.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                            for (DataSnapshot item : chef.getChildren()) {
                                                Order order = item.getValue(Order.class);
                                                if (order.getId().equals(ID)) {
                                                    order.setStatus("3");
                                                    database.child("orders").child(child.getKey()).child(chef.getKey()).child(item.getKey()).setValue(order);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        sendToCustomer();
                    }
                } else {
                    if (order_status.getText().toString().equals("Prepared")) {
                        database.child("orders").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    for (DataSnapshot item : child.getChildren()) {
                                        Order order = item.getValue(Order.class);
                                        if (order.getId().equals(ID)) {
                                            order.setStatus("4");
                                            database.child("orders").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(child.getKey()).child(item.getKey()).setValue(order);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        sendToChef();
                    }
                    else if(order_status.getText().toString().equals("Delivered")){
                        ReviewChef();
                        return;
                    }
                }
                prepared.setVisibility(View.INVISIBLE);
                MemoryAllocation();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(new String[]{(Manifest.permission.CALL_PHONE)},99);
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddToCart();
            }
        });
    }

    private void MemoryAllocation(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final double[] subtotal = {0};
                if (buy.equals("sell")) {
                    repeat.setVisibility(View.GONE);
                    database.child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                for (DataSnapshot chef : child.getChildren()) {
                                    if (chef.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        for (DataSnapshot item : chef.getChildren()) {
                                            Order order = item.getValue(Order.class);
                                            if (order.getId().equals(ID)) {
                                                order_id.setText(order.getId());
                                                String pattern = "dd MMM yyyy h:m a";
                                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                                String date = simpleDateFormat.format(order.getOrder_date());
                                                order_date.setText(date);
                                                order_price.setText(order.getPrice());
                                                if (order.getDelivery_type().equals("delivery")) {
                                                    DeliveryType = "delivery";
                                                    delivery_type.setImageResource(R.drawable.ic_delivery);
                                                } else {
                                                    DeliveryType = "pickup";
                                                    delivery_type.setImageResource(R.drawable.ic_pickup);
                                                }
                                                if (order.getStatus().equals("0")) {
                                                    order_status.setText("Recevied");
                                                    cancel.setVisibility(View.VISIBLE);
                                                    done.setVisibility(View.VISIBLE);
                                                    cancel.setText("Reject");
                                                    done.setText("Accept");
                                                } else if (order.getStatus().equals("1")) {
                                                    order_status.setText("Verifing");
                                                    cancel.setVisibility(View.INVISIBLE);
                                                    done.setVisibility(View.INVISIBLE);
                                                } else if (order.getStatus().equals("2")) {
                                                    order_status.setText("Preparing");
                                                    cancel.setVisibility(View.INVISIBLE);
                                                    done.setVisibility(View.INVISIBLE);
                                                    prepared.setVisibility(View.VISIBLE);
                                                    prepared.setText("Prepared");
                                                } else if (order.getStatus().equals("3")) {
                                                    order_status.setText("Prepared");
                                                    cancel.setVisibility(View.INVISIBLE);
                                                    done.setVisibility(View.INVISIBLE);
                                                } else if (order.getStatus().equals("4")) {
                                                    order_status.setText("Delivered");
                                                    cancel.setVisibility(View.INVISIBLE);
                                                    done.setVisibility(View.INVISIBLE);
                                                } else if (order.getStatus().equals("99")) {
                                                    order_status.setText("Rejected");
                                                    cancel.setVisibility(View.INVISIBLE);
                                                    done.setVisibility(View.INVISIBLE);
                                                }
                                                carts = order.getCarts();
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            for (Cart cart : carts) {
                                subtotal[0] += Double.parseDouble(cart.getPrice());
                            }
                            SetDeliveryDetails();
                            gst.setText(subtotal[0] * 0.18 + "");
                            subtotal_text.setText(subtotal[0] + "");
                            RecyclerAdapter_Checkout recyclerAdapter = new RecyclerAdapter_Checkout(getBaseContext(), carts);
                            rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                            rv.setAdapter(recyclerAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    database.child("orders").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        for (DataSnapshot item : child.getChildren()) {
                                            Order order = item.getValue(Order.class);
                                            if (order.getId().equals(ID)) {
                                                order_id.setText(order.getId());
                                                String pattern = "dd MMM yyyy h:m a";
                                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                                String date = simpleDateFormat.format(order.getOrder_date());
                                                order_date.setText(date);
                                                order_price.setText(order.getPrice());
                                                if (order.getDelivery_type().equals("delivery")) {
                                                    DeliveryType = "delivery";
                                                    delivery_type.setImageResource(R.drawable.ic_delivery);
                                                } else {
                                                    DeliveryType = "pickup";
                                                    delivery_type.setImageResource(R.drawable.ic_pickup);
                                                }
                                                if (order.getStatus().equals("0")) {
                                                    order_status.setText("Placed");
                                                    cancel.setVisibility(View.INVISIBLE);
                                                    done.setVisibility(View.INVISIBLE);
                                                } else if (order.getStatus().equals("1")) {
                                                    order_status.setText("Accepted");
                                                    cancel.setText("Reject");
                                                    done.setText("Agree");
                                                } else if (order.getStatus().equals("2")) {
                                                    order_status.setText("Preparing");
                                                    cancel.setVisibility(View.INVISIBLE);
                                                    done.setVisibility(View.INVISIBLE);
                                                } else if (order.getStatus().equals("3")) {
                                                    order_status.setText("Prepared");
                                                    cancel.setVisibility(View.INVISIBLE);
                                                    done.setVisibility(View.INVISIBLE);
                                                    prepared.setVisibility(View.VISIBLE);
                                                    prepared.setText("Delivered Or Pickedup");
                                                } else if (order.getStatus().equals("4")) {
                                                    order_status.setText("Delivered");
                                                    cancel.setVisibility(View.INVISIBLE);
                                                    done.setVisibility(View.INVISIBLE);
                                                    prepared.setVisibility(View.VISIBLE);
                                                    prepared.setText("Rate Chef");
                                                } else if (order.getStatus().equals("99")) {
                                                    order_status.setText("Rejected");
                                                    cancel.setVisibility(View.INVISIBLE);
                                                    done.setVisibility(View.INVISIBLE);
                                                }
                                                carts = order.getCarts();
                                                break;
                                            }
                                        }
                                    }
                                    for (Cart cart : carts) {
                                        subtotal[0] += Double.parseDouble(cart.getPrice());
                                    }
                                    SetDeliveryDetails();
                                    gst.setText(subtotal[0] * 0.18 + "");
                                    subtotal_text.setText(subtotal[0] + "");
                                    RecyclerAdapter_Checkout recyclerAdapter = new RecyclerAdapter_Checkout(getBaseContext(), carts);
                                    rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                                    rv.setAdapter(recyclerAdapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }
        }, 200);
    }
    @Override
    public void onBackPressed() {
        this.finish();
    }

    private void ReviewChef(){
        final Dialog dialog = new Dialog(OrderDetailsActivity.this);
        dialog.setContentView(R.layout.custom_dialog_review);

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Button submit = dialog.findViewById(R.id.submit);
        final RatingBar ratingBar = dialog.findViewById(R.id.rate);
        final EditText feedback = dialog.findViewById(R.id.feedback);

        ratingBar.setStepSize(1);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(ratingBar.getRating() == 0.0 || feedback.getText().toString().equals("")){
                   Toast.makeText(OrderDetailsActivity.this,"Review Required",Toast.LENGTH_LONG).show();
               }else {
                   database.child("users").child(chef_id).addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           User user = dataSnapshot.getValue(User.class);
                           List<Review> reviews = user.getReviews();
                           if(reviews == null){
                               reviews = new ArrayList<>();
                           }
                           Review review = new Review((int) ratingBar.getRating(),feedback.getText().toString().trim(),FirebaseAuth.getInstance().getCurrentUser().getUid());
                           reviews.add(review);
                           database.child("users").child(chef_id).child("reviews").setValue(reviews);
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });
                   dialog.dismiss();
               }
            }
        });

        dialog.show();
    }

    private void AddToCart() {
        final String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.child("carts").child(uID).child(order_id.getText().toString().trim())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Chef_cart chef_cart = dataSnapshot.getValue(Chef_cart.class);
                        if (chef_cart == null) {
                            chef_cart = new Chef_cart();
                            chef_cart.setId(chef_id);
                            List<Cart> carts1 = carts;
                            chef_cart.setCart(carts1);
                            Toast.makeText(getApplicationContext(), "Added to Cart", Toast.LENGTH_LONG).show();
                        } else {
                            List<Cart> carts1 = chef_cart.getCart();
                            for (Cart item : carts1) {
                                for (Cart temp : carts) {
                                    if (item.getItemID().equals(temp.getItemID())) {
                                        Toast.makeText(getApplicationContext(), "Already in Cart", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Added to Cart", Toast.LENGTH_LONG).show();
                                        Cart cart = temp;
                                        carts1.add(cart);
                                    }
                                }
                            }
                            chef_cart.setCart(carts1);
                        }
                        database.child("carts").child(uID).child(chef_id).setValue(chef_cart);
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        intent.putExtra("from", "repeat");
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SetChefDetails() {
        database.child("users").child(chef_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getImage().equals("1")) {
                    chef_image.setImageResource(R.drawable.git);
                } else {
                    Picasso.get().load(user.getImage()).placeholder(R.drawable.git).into(chef_image);
                }
                chef_name.setText(user.getName());
                chef_email.setText(user.getEmail());
                chef_mobile.setText(user.getMobile());
                chef_Address = user.getLocation();
                chef_token = user.getToken();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SetDeliveryDetails() {
        if(buy.equals("sell")){
            if (DeliveryType.equals("delivery")) {
                delivery_add.setText(chef_Address);
                delivery_charge.setText("0");
            } else if (DeliveryType.equals("pickup")) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                delivery_add.setText(preferences.getString("location", "abc"));
                delivery_charge.setText("20.0");
            }
        }else {
            if (DeliveryType.equals("delivery")) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                delivery_add.setText(preferences.getString("location", "abc"));
                delivery_charge.setText("20.0");

            } else if (DeliveryType.equals("pickup")) {
                delivery_add.setText(chef_Address);
                delivery_charge.setText("0");

            }
        }
    }

    private void sendToChef() {
        try {

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            final String apiKey = "AIzaSyDJI9yGj8ZYu1bQbqTaa_WUkpsFQW-X6Xo";
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);
            conn.setDoOutput(true);
            JSONObject message = new JSONObject();
            message.put("to", chef_token);
            message.put("priority", "high");

            JSONObject notification = new JSONObject();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            notification.put("title", "from : " + preferences.getString("name", "abc"));
            notification.put("body", order_id.getText().toString());
            notification.put("to","chef");
            notification.put("id",FirebaseAuth.getInstance().getCurrentUser().getUid());
            message.put("data", notification);
            OutputStream os = conn.getOutputStream();
            os.write(message.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + message.toString());
            System.out.println("Response Code : " + responseCode);
            System.out.println("Response Code : " + conn.getResponseMessage());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            Log.e("response", response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("response", "error");
        }
    }

    private void sendToCustomer() {
        try {

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            final String apiKey = "AIzaSyDJI9yGj8ZYu1bQbqTaa_WUkpsFQW-X6Xo";
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);
            conn.setDoOutput(true);
            JSONObject message = new JSONObject();
            message.put("to", chef_token);
            message.put("priority", "high");

            JSONObject notification = new JSONObject();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            notification.put("title", "from : " + preferences.getString("name", "abc"));
            notification.put("body", order_id.getText().toString());
            notification.put("to","customer");
            notification.put("id",FirebaseAuth.getInstance().getCurrentUser().getUid());
            message.put("data", notification);
            OutputStream os = conn.getOutputStream();
            os.write(message.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + message.toString());
            System.out.println("Response Code : " + responseCode);
            System.out.println("Response Code : " + conn.getResponseMessage());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            Log.e("response", response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("response", "error");
        }
    }

    @Override
    protected void onPause() {
        isOpen = false;
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver((receiver),
                new IntentFilter(MyFirebaseInstanceService.REQUEST_ACCEPT));
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    buy = intent.getStringExtra("buy");
                    ID = intent.getStringExtra("ID");
                    chef_id = intent.getStringExtra("chef");

                    prepared.setVisibility(View.INVISIBLE);
                    MemoryAllocation();
                    SetChefDetails();
                    SetDeliveryDetails();
                }
            }, 400);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 99 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+chef_mobile.getText().toString().trim()));
            startActivity(intent);
        }
    }
}
