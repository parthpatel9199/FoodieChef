package com.example.foodiechef.ui.chef;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodiechef.ChefProfileActivity;
import com.example.foodiechef.R;
import com.example.foodiechef.ui.RecyclerAdapter_Food;
import com.example.foodiechef.ui.Review;
import com.example.foodiechef.ui.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerAdapter_Chef extends RecyclerView.Adapter<RecyclerAdapter_Chef.MyViewHolder> {

    private Context context;
    private List<User> users;

    public RecyclerAdapter_Chef(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_chef, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        User user = users.get(position);
        holder.chef_id = user.getId();
        holder.chef_name.setText(user.getName());
        if (user.getImage().equals("1")) {
            holder.chef_image.setImageResource(R.drawable.git);
        } else {
            Picasso.get().load(user.getImage()).placeholder(R.drawable.git).into(holder.chef_image);
        }
        holder.chef_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChefProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ID", holder.chef_id + "");
                context.startActivity(intent);
            }
        });
        List<Review> reviews;
        if (user.getReviews() != null) {
            reviews = user.getReviews();
        } else {
            reviews = new ArrayList<>();
        }
        if (reviews.size() == 0) {
            holder.ratingBar.setRating(0);
        } else {
            float stars = 0;
            for (Review review : reviews) {
                stars += review.getStar();
            }
            stars = stars / reviews.size();
            holder.ratingBar.setRating(stars);
        }

        MyOnClickListener listener = new MyOnClickListener(user);
        holder.openProfile.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public String chef_id;
        private TextView chef_name;
        private CircleImageView chef_image;
        private LinearLayout openProfile;
        private RatingBar ratingBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            chef_name = (TextView) itemView.findViewById(R.id.chef_name);
            chef_image = (CircleImageView) itemView.findViewById(R.id.chef_image);
            openProfile = (LinearLayout) itemView.findViewById(R.id.openProfile);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rating);
        }
    }

    public class MyOnClickListener implements View.OnClickListener {

        public User user;
        public String userId;
        private Intent intent;

        public MyOnClickListener(User user) {
            this.user = user;
            this.userId = user.getId();
        }

        @Override
        public void onClick(View v) {

            intent = new Intent(context, ChefProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("ID", userId + "");
            context.startActivity(intent);
        }
    }
}
