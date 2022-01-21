package com.example.foodiechef.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerAdapter_Review extends RecyclerView.Adapter<RecyclerAdapter_Review.MyViewHolder> {

    private Context context;
    private List<Review> reviews;
    private DatabaseReference database;

    public RecyclerAdapter_Review(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        database = FirebaseDatabase.getInstance().getReference();
        v = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Review review = reviews.get(position);

        holder.feedback.setText(review.getFeedback());
        holder.ratingBar.setRating(review.getStar());
        database.child("users").child(review.getUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.user_name.setText(user.getName());
                if(user.getImage().equals("1")){
                    holder.user_image.setImageResource(R.drawable.git);
                }else {
                    Picasso.get().load(user.getImage()).placeholder(R.drawable.git).into(holder.user_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView user_image;
        private TextView user_name,feedback;
        private RatingBar ratingBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            user_image = (CircleImageView) itemView.findViewById(R.id.user_image);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            feedback = (TextView) itemView.findViewById(R.id.feedback);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rate);
        }
    }
}
