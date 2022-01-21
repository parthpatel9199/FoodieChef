package com.example.foodiechef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodiechef.ui.Cart;
import com.example.foodiechef.ui.Chef_cart;
import com.example.foodiechef.ui.Food;
import com.example.foodiechef.ui.Order;
import com.example.foodiechef.ui.RecyclerAdapter_Checkout;
import com.example.foodiechef.ui.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class CheckoutActivity extends AppCompatActivity implements PaymentResultListener {

    private ImageView back, location;
    private CircleImageView chef_image;
    private RecyclerView rv;
    private List<Cart> carts;
    private LinearLayout delivery, pickup;
    private String DeliveryType;
    private TextView proceed, chef_name, chef_email, chef_mobile, delivery_name, subtotal_text, delivery_charge, gst, total_price, cancel;
    public static TextView delivery_add;
    private String chef_token, order_details = "", chef_Address, chef_id;
    private List<String> foodIds;
    private DatabaseReference database;
    long maxOrderId = 1;
    private String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Checkout.preload(getApplicationContext());

        database = FirebaseDatabase.getInstance().getReference();
        foodIds = new ArrayList<>();
        back = findViewById(R.id.back);
        cancel = findViewById(R.id.cancel);
        delivery = findViewById(R.id.delivery);
        pickup = findViewById(R.id.pickup);
        proceed = findViewById(R.id.proceedToPay);
        rv = findViewById(R.id.checkout_list);
        chef_image = findViewById(R.id.chef_image);
        chef_name = findViewById(R.id.chef_name);
        chef_email = findViewById(R.id.chef_email);
        chef_mobile = findViewById(R.id.chef_mobile);
        delivery_add = findViewById(R.id.delivery_add);
        delivery_name = findViewById(R.id.delivery_name);
        subtotal_text = findViewById(R.id.subtotal);
        delivery_charge = findViewById(R.id.delivery_charge);
        gst = findViewById(R.id.gst);
        total_price = findViewById(R.id.total_price);
        location = findViewById(R.id.edit_location);

        carts = new ArrayList<>();

        DeliveryType = "delivery";
        selectDeliveryType(delivery, pickup);

        final double[] subtotal = {0};

        database.child("carts").child(uID).child(getIntent().getStringExtra("ID"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Chef_cart chef_cart = dataSnapshot.getValue(Chef_cart.class);
                        for (Cart cart : chef_cart.getCart()) {
                            carts.add(cart);
                            foodIds.add(cart.getItemID());
                            subtotal[0] += Integer.parseInt(cart.getPrice());
                        }
                        getOrderNames();
                        gst.setText(subtotal[0] * 0.18 + "");
                        subtotal_text.setText(subtotal[0] + "");
                        total_price.setText(subtotal[0] + Double.parseDouble((String) gst.getText()) + Double.parseDouble((String) delivery_charge.getText()) + "");
                        RecyclerAdapter_Checkout recyclerAdapter = new RecyclerAdapter_Checkout(getBaseContext(), carts);
                        rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                        rv.setAdapter(recyclerAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        database.child("users").child(getIntent().getStringExtra("ID"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user.getImage().equals("1")) {
                            chef_image.setImageResource(R.drawable.git);
                        } else {
                            Picasso.get().load(user.getImage()).placeholder(R.drawable.git).into(chef_image);
                        }
                        chef_id = user.getId();
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
        database.child("orders").child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    for (DataSnapshot orders : child.getChildren()) {
                        maxOrderId += 1;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setVisibility(View.VISIBLE);
                DeliveryType = "delivery";
                selectDeliveryType(delivery, pickup);
                SetDeliveryDetails();
            }
        });

        pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setVisibility(View.GONE);
                DeliveryType = "pickup";
                selectDeliveryType(pickup, delivery);
                SetDeliveryDetails();
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderComplete = "Done";
                if (DeliveryType.equals("delivery")) {
                    if (delivery_add.getText().toString().equals("Location here")) {
                        orderComplete = "NotDone";
                        Toast.makeText(CheckoutActivity.this, "Location Required", Toast.LENGTH_LONG).show();
                    } else {
                        database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("location").setValue(delivery_add.getText().toString());
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        editor.putString("location", delivery_add.getText().toString());
                        editor.commit();
                    }
                }
                if (orderComplete.equals("Done")) {
                    startPayment();
                }
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckoutActivity.this, MapActivity.class);
                intent.putExtra("from", "checkout");
                startActivity(intent);
            }
        });

        SetDeliveryDetails();
    }

    private void selectDeliveryType(LinearLayout l1, LinearLayout l2) {
        if (l2.getBackground() == getDrawable(R.drawable.rounded_fb)) {
            l1.setBackground(getDrawable(R.drawable.selected_option));
        } else {
            l2.setBackground(getDrawable(R.drawable.rounded_fb));
            l1.setBackground(getDrawable(R.drawable.selected_option));
        }
    }

    private void SetDeliveryDetails() {
        if (DeliveryType.equals("delivery")) {
            delivery_name.setText("Delivery Address :");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (preferences.getString("location", "abc").equals("abc")) {
                delivery_add.setText(getResources().getString(R.string.location_here));
            } else {
                delivery_add.setText(preferences.getString("location", "abc"));
            }
            delivery_charge.setText("20.0");
            total_price.setText(Double.parseDouble((String) subtotal_text.getText()) + Double.parseDouble((String) gst.getText()) + Double.parseDouble((String) delivery_charge.getText()) + "");
        } else if (DeliveryType.equals("pickup")) {
            delivery_name.setText("Drop up Address :");
            delivery_add.setText(chef_Address);
            delivery_charge.setText("0");
            total_price.setText(Double.parseDouble((String) subtotal_text.getText()) + Double.parseDouble((String) gst.getText()) + Double.parseDouble((String) delivery_charge.getText()) + "");
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    private void getOrderNames() {
        for (String id : foodIds) {
            database.child("foods").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Food food = dataSnapshot.getValue(Food.class);
                    order_details += food.getName() + "  ";
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
            notification.put("body", maxOrderId);
            notification.put("to", "chef");
            notification.put("id", uID);
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

    public void startPayment() {
        Checkout checkout = new Checkout();
        final Activity activity = this;
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Parth");
            options.put("description", order_details);
            options.put("currency", "INR");
            double price = Double.parseDouble(total_price.getText().toString()) * 100;
            options.put("amount", price + "");
            checkout.open(activity, options);

        } catch (Exception e) {
            Log.e("Error", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        sendToChef();
        database.child("orders").child(uID).child(getIntent().getStringExtra("ID"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Order> orders = new ArrayList<>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            orders.add(child.getValue(Order.class));
                        }
                        Date d = Calendar.getInstance().getTime();
                        Order order = new Order(String.valueOf(maxOrderId), getIntent().getStringExtra("ID"), uID, total_price.getText().toString(), DeliveryType, d, "0", carts);
                        orders.add(order);
                        database.child("orders").child(uID).child(getIntent().getStringExtra("ID")).setValue(orders);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        Intent intent = new Intent(CheckoutActivity.this, OrderCompleteActivity.class);
        intent.putExtra("ID", getIntent().getStringExtra("ID"));
        intent.putExtra("orderID", maxOrderId + "");
        intent.putExtra("orderPrice", total_price.getText().toString());
        intent.putExtra("orderDetail", order_details);
        startActivity(intent);
        onBackPressed();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this,"Payment Failed!",Toast.LENGTH_SHORT).show();
    }
}
