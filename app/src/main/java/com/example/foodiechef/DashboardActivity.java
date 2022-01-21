package com.example.foodiechef;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.example.foodiechef.ui.User;
import com.example.foodiechef.ui.cart.CartFragment;
import com.example.foodiechef.ui.chef.ChefFragment;
import com.example.foodiechef.ui.home.HomeFragment;
import com.example.foodiechef.ui.profile.ProfileFragment;
import com.example.foodiechef.ui.search.SearchFragment;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.BubbleToggleView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.Date;

public class DashboardActivity extends NavigationActivity{

    HomeFragment homeFragment;
    SearchFragment searchFragment;
    ChefFragment chefFragment;
    CartFragment cartFragment;
    ProfileFragment profileFragment;
    private DatabaseReference database;
    private BubbleNavigationLinearView navView;
    private BubbleToggleView cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        MobileAds.initialize(this, "ca-app-pub-7534379590446559~8786090198");
        getLayoutInflater().inflate(R.layout.activity_dashboard, frame);

        database = FirebaseDatabase.getInstance().getReference();
        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        chefFragment = new ChefFragment();
        cartFragment = new CartFragment();
        profileFragment = new ProfileFragment();
        navView = findViewById(R.id.nav_view);
        cart = findViewById(R.id.l_item_cart);

        if(getIntent().getStringExtra("from") == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, homeFragment)
                    .commit();
        }else if(getIntent().getStringExtra("from").equals("repeat")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, cartFragment)
                    .commit();
            cart.activate();
        }

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("token").setValue(instanceIdResult.getToken());
            }
        });

        navView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                System.out.println(position + " position ");
                if(position == 1){
                    hideKeyboard();
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,searchFragment)
                           .commit();
               }else if(position == 2){
                    hideKeyboard();
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,chefFragment)
                            .commit();
                }else if(position == 3){
                    hideKeyboard();
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,cartFragment)
                            .commit();
                }
                else if(position == 4){
                    hideKeyboard();
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,profileFragment)
                            .commit();
                }else if(position == 0){
                    hideKeyboard();
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,homeFragment)
                            .commit();
                }
            }
        });


        DatabaseReference ref = database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putString("name",user.getName());
                editor.putString("email",user.getEmail());
                editor.putString("mobile",user.getMobile());
                editor.putString("image",user.getImage());
                editor.putString("location",user.getLocation());
                editor.putString("biography",user.getBiography());
                editor.commit();
                if (user.getImage().equals("1")){
                    user_image.setImageResource(R.drawable.git);
                }
                else {
                    Picasso.get().load(user.getImage()).placeholder(R.drawable.git).into(user_image);
                }
                user_name.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }
}
