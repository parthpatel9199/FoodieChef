package com.example.foodiechef.ui.about;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodiechef.MapActivity;
import com.example.foodiechef.R;
import com.example.foodiechef.ReviewActivity;
import com.example.foodiechef.ui.Review;
import com.example.foodiechef.ui.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.foodiechef.NavigationActivity.user_name;
import static com.example.foodiechef.ui.profile.ProfileFragment.user_name1;


public class AboutFragment extends Fragment {

    private AboutViewModel aboutViewModel;
    public String enable;
    private EditText name, email, mobile, bio;
    private TextView rating, number;
    private ImageView review;
    private RatingBar star;
    public static EditText location;
    public static double latitude, longitude;
    private ProgressBar p1, p2, p3, p4, p5;
    private FloatingActionButton edit;
    private SharedPreferences preferences;
    private DatabaseReference database;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        aboutViewModel =
                ViewModelProviders.of(this).get(AboutViewModel.class);
        View root = inflater.inflate(R.layout.fragment_about, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        database = FirebaseDatabase.getInstance().getReference();
        edit = root.findViewById(R.id.edit_profile);
        review = root.findViewById(R.id.review);
        rating = root.findViewById(R.id.rating);
        number = root.findViewById(R.id.number);
        star = root.findViewById(R.id.star);
        name = root.findViewById(R.id.name_here);
        email = root.findViewById(R.id.email_here);
        mobile = root.findViewById(R.id.mobile_here);
        location = root.findViewById(R.id.location_here);
        bio = root.findViewById(R.id.biographay_here);
        p1 = root.findViewById(R.id.progressBar1);
        p2 = root.findViewById(R.id.progressBar2);
        p3 = root.findViewById(R.id.progressBar3);
        p4 = root.findViewById(R.id.progressBar4);
        p5 = root.findViewById(R.id.progressBar5);
        enable = "edit";

        name.setFocusable(false);
        email.setFocusable(false);
        mobile.setFocusable(false);
        location.setFocusable(false);
        bio.setFocusable(false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        final Bundle argument = getArguments();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (argument.getString("profile").equals("chef")) {
                    edit.hide();
                    name.setText(preferences.getString("chefName", "abc"));
                    email.setText(preferences.getString("chefEmail", "abc"));
                    mobile.setText(preferences.getString("chefMobile", "abc"));
                    if (preferences.getString("chefLocation", getResources().getString(R.string.location_here)) != null) {
                        location.setText(preferences.getString("chefLocation", getResources().getString(R.string.location_here)));
                    }
                    if (preferences.getString("chefBio", getResources().getString(R.string.biography_here)) != null) {
                        bio.setText(preferences.getString("chefBio", getResources().getString(R.string.biography_here)));
                    }
                } else {
                    name.setText(preferences.getString("name", "abc"));
                    email.setText(preferences.getString("email", "abc"));
                    mobile.setText(preferences.getString("mobile", "abc"));
                    if (preferences.getString("location", getResources().getString(R.string.location_here)) != null) {
                        location.setText(preferences.getString("location", getResources().getString(R.string.location_here)));
                    }
                    if (preferences.getString("biography", getResources().getString(R.string.biography_here)) != null) {
                        bio.setText(preferences.getString("biography", getResources().getString(R.string.biography_here)));
                    }
                }
                setRating();
            }
        },500);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enable.equals("edit")) {
                    name.setFocusableInTouchMode(true);
                    mobile.setFocusableInTouchMode(true);
                    bio.setFocusableInTouchMode(true);
                    enable = "save";
                    edit.setImageResource(R.drawable.ic_check_black_24dp);
                } else {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Toast.makeText(getActivity(), "Saved", Toast.LENGTH_LONG).show();
                    name.setFocusable(false);
                    mobile.setFocusable(false);
                    bio.setFocusable(false);
                    enable = "edit";
                    edit.setImageResource(R.drawable.ic_edit_black_24dp);

                    user_name.setText(name.getText().toString());
                    user_name1.setText(name.getText().toString());
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    database.child("users").child(uid).child("name").setValue(name.getText().toString());
                    database.child("users").child(uid).child("mobile").setValue(mobile.getText().toString());
                    database.child("users").child(uid).child("location").setValue(location.getText().toString());
                    database.child("users").child(uid).child("biography").setValue(bio.getText().toString());
                }
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enable.equals("edit")) {

                } else {
                    Intent intent = new Intent(getActivity(), MapActivity.class);
                    startActivity(intent);
                }
            }
        });

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ReviewActivity.class);
                if (argument.getString("profile").equals("chef")) {
                    intent.putExtra("ID", getActivity().getIntent().getStringExtra("ID"));
                }
                startActivity(intent);
            }
        });

        return root;
    }

    private void setRating() {
        if (getArguments().getString("profile").equals("chef")) {
            database.child("users").child(preferences.getString("chefID", "abc")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    List<Review> reviews;
                    if (user.getReviews() != null) {
                        reviews = user.getReviews();
                    } else {
                        reviews = new ArrayList<>();
                    }
                    if (reviews.size() == 0) {
                        star.setRating(0);
                        rating.setText("0");
                        number.setText("0");
                        review.setClickable(false);
                    } else {
                        float stars = 0;
                        float pb1 = 0, pb2 = 0, pb3 = 0, pb4 = 0, pb5 = 0;
                        for (Review review : reviews) {
                            stars += review.getStar();
                            switch (review.getStar()) {
                                case 1:
                                    pb1 += 1;
                                    break;
                                case 2:
                                    pb2 += 1;
                                    break;
                                case 3:
                                    pb3 += 1;
                                    break;
                                case 4:
                                    pb4 += 1;
                                    break;
                                case 5:
                                    pb5 += 1;
                                    break;
                            }
                        }
                        stars = stars / reviews.size();
                        star.setRating(stars);
                        rating.setText(stars + "");
                        number.setText(reviews.size() + "");
                        p1.setProgress((int) (pb1 / reviews.size() * 100));
                        p2.setProgress((int) (pb2 / reviews.size() * 100));
                        p3.setProgress((int) (pb3 / reviews.size() * 100));
                        p4.setProgress((int) (pb4 / reviews.size() * 100));
                        p5.setProgress((int) (pb5 / reviews.size() * 100));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    List<Review> reviews;
                    if (user.getReviews() != null) {
                        reviews = user.getReviews();
                    } else {
                        reviews = new ArrayList<>();
                    }
                    if (reviews.size() == 0) {
                        star.setRating(0);
                        rating.setText("0");
                        number.setText("0");
                        review.setClickable(false);
                    } else {
                        float stars = 0;
                        float pb1 = 0, pb2 = 0, pb3 = 0, pb4 = 0, pb5 = 0;
                        for (Review review : reviews) {
                            stars += review.getStar();
                            switch (review.getStar()) {
                                case 1:
                                    pb1 += 1;
                                    break;
                                case 2:
                                    pb2 += 1;
                                    break;
                                case 3:
                                    pb3 += 1;
                                    break;
                                case 4:
                                    pb4 += 1;
                                    break;
                                case 5:
                                    pb5 += 1;
                                    break;
                            }
                        }
                        stars = stars / reviews.size();
                        star.setRating(stars);
                        rating.setText(stars + "");
                        number.setText(reviews.size() + "");
                        p1.setProgress((int) (pb1 / reviews.size() * 100));
                        p2.setProgress((int) (pb2 / reviews.size() * 100));
                        p3.setProgress((int) (pb3 / reviews.size() * 100));
                        p4.setProgress((int) (pb4 / reviews.size() * 100));
                        p5.setProgress((int) (pb5 / reviews.size() * 100));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}