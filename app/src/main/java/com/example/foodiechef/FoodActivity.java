package com.example.foodiechef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodiechef.ui.Cart;
import com.example.foodiechef.ui.Chef_cart;
import com.example.foodiechef.ui.Food;
import com.example.foodiechef.ui.Review;
import com.example.foodiechef.ui.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FoodActivity extends AppCompatActivity {

    private ImageView backToCate,food_image;
    private TextView food_name,food_price,food_ingrident,food_time,chef_name;
    private Button addToCart,share;
    private RatingBar ratingBar;
    private CircleImageView chef_image;
    private DatabaseReference database;
    private String food_id,title;
    private Food food;
    private Uri link;
    public static String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        database = FirebaseDatabase.getInstance().getReference();
        food_image = findViewById(R.id.food_image);
        backToCate = findViewById(R.id.backToCate);
        food_name = findViewById(R.id.food_name);
        food_ingrident = findViewById(R.id.food_ingrident);
        food_price = findViewById(R.id.food_price);
        food_time = findViewById(R.id.food_time);
        addToCart = findViewById(R.id.addToCart);
        chef_name = findViewById(R.id.chef_name);
        chef_image = findViewById(R.id.chef_image);
        ratingBar = findViewById(R.id.rating);
        share = findViewById(R.id.share);

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddToCart();
            }
        });

        backToCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLink();
            }
        });

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                        }

                        if(deepLink == null){
                            status = "normal";
                            food_id = getIntent().getStringExtra("ID");
                        }else {
                            status = "url";
                            food_id =  deepLink.getPath().substring(1);
                        }
                        database.child("foods").child(food_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                food = dataSnapshot.getValue(Food.class);
                                title = food.getCategory();
                                food_name.setText(food.getName());
                                food_ingrident.setText(food.getIngredient());
                                food_price.setText(food.getPrice());
                                food_time.setText(food.getTime());
                                Picasso.get().load(food.getImage()).placeholder(R.drawable.load).into(food_image);
                                database.child("users").child(food.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        chef_name.setText(user.getName());
                                        if(user.getImage().equals("1")){
                                            chef_image.setImageResource(R.drawable.git);
                                        }else {
                                            Picasso.get().load(user.getImage()).placeholder(R.drawable.load).into(chef_image);
                                        }
                                        List<Review> reviews;
                                        if (user.getReviews() != null) {
                                            reviews = user.getReviews();
                                        } else {
                                            reviews = new ArrayList<>();
                                        }
                                        if (reviews.size() == 0) {
                                            ratingBar.setRating(0);
                                        } else {
                                            float stars = 0;
                                            for (Review review : reviews) {
                                                stars += review.getStar();
                                            }
                                            stars = stars / reviews.size();
                                            ratingBar.setRating(stars);
                                        }
                                        MyOnClickListener listener = new MyOnClickListener(user.getId());
                                        chef_name.setOnClickListener(listener);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        link = buildDeepLink(Uri.parse("https://foodiechef.page.link/"+food_id),0);
                        Log.e("link",link.toString());
                        shortDeepLink();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error", "getDynamicLink:onFailure", e);
                    }
                });


    }

    private void AddToCart() {
        final String  uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.child("carts").child(uID).child(food.getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Chef_cart chef_cart = dataSnapshot.getValue(Chef_cart.class);
                        if(chef_cart == null){
                            chef_cart = new Chef_cart();
                            chef_cart.setId(food.getUserID());
                            List<Cart> carts = new ArrayList<>();
                            Cart cart = new Cart();
                            cart.setItemID(food.getId());
                            cart.setItem_quantity();
                            cart.setPrice(food.getPrice());
                            carts.add(cart);
                            chef_cart.setCart(carts);
                            Toast.makeText(getApplicationContext(),"Added to Cart",Toast.LENGTH_LONG).show();
                        }
                        else {
                            List<Cart> carts = chef_cart.getCart();
                            for (Cart item:carts){
                                if(item.getItemID().equals(food.getId())){
                                    Toast.makeText(getApplicationContext(),"Already in Cart",Toast.LENGTH_LONG).show();
                                    break;
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"Added to Cart",Toast.LENGTH_LONG).show();
                                    Cart cart = new Cart();
                                    cart.setItemID(food.getId());
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

    @Override
    public void onBackPressed() {
        if(status.equals("url")){
            Intent intent = new Intent(FoodActivity.this,CategoryActivity.class);
            intent.putExtra("title",title);
            startActivity(intent);
        }else {
            this.finish();
        }
    }

    public Uri buildDeepLink(@NonNull Uri deepLink, int minVersion) {
        String uriPrefix = "https://foodiechef.page.link";

        DynamicLink.Builder builder = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setDomainUriPrefix(uriPrefix)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder()
                        .setMinimumVersion(minVersion)
                        .build())
                .setLink(deepLink);

        DynamicLink link = builder.buildDynamicLink();
        return link.getUri();
    }

    private void shortDeepLink(){
       Task<ShortDynamicLink> create = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(link)
                .buildShortDynamicLink()
               .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                   @Override
                   public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                       if(task.isSuccessful()){
                           link = task.getResult().getShortLink();
                           Log.e("in",link.toString());
                       }
                   }
               });
    }
    private void shareLink(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, link.toString());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    public class MyOnClickListener implements View.OnClickListener {

        private String userId;
        private Intent intent;

        public MyOnClickListener(String userID) {
            this.userId = userID;
        }

        @Override
        public void onClick(View v) {

            intent = new Intent(FoodActivity.this, ChefProfileActivity.class);
            intent.putExtra("ID", userId + "");
            startActivity(intent);
        }
    }
}
