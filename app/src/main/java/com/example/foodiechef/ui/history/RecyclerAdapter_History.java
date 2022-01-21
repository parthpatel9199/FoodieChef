package com.example.foodiechef.ui.history;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodiechef.AddFoodActivity;
import com.example.foodiechef.DashboardActivity;
import com.example.foodiechef.R;
import com.example.foodiechef.ui.Food;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerAdapter_History extends RecyclerView.Adapter<RecyclerAdapter_History.MyViewHolder> {

    private Context context;
    private List<Food> foods;
    private String status;
    private DatabaseReference database;

    public RecyclerAdapter_History(Context context, List<Food> foods,String status) {
        this.context = context;
        this.foods = foods;
        this.status = status;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(v);
        database = FirebaseDatabase.getInstance().getReference();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Food food = foods.get(position);
        holder.food_id = food.getId();
        holder.food_name.setText(food.getName());
        holder.food_ingrident.setText(food.getIngredient());
        holder.food_price.setText(food.getPrice());
        holder.food_time.setText(food.getTime());
        Picasso.get().load(food.getImage()).placeholder(R.drawable.load).into(holder.food_image);

        if(status == "add"){
            holder.EditORDel.setVisibility(View.GONE);
        }else if (status == "delete"){
            holder.icon.setImageResource(R.drawable.ic_delete_black_24dp);
        }else if(status == "edit"){
            holder.icon.setImageResource(R.drawable.ic_edit_black_24dp);
        }

        MyOnClickListener listener = new MyOnClickListener(food.getId());
        holder.EditORDel.setOnClickListener(listener);
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
        private ImageView food_image,icon;
        private CardView EditORDel;

        public MyViewHolder(View itemView) {
            super(itemView);
            food_name = (TextView) itemView.findViewById(R.id.food_name);
            food_ingrident = (TextView) itemView.findViewById(R.id.food_ingrident);
            food_price = (TextView) itemView.findViewById(R.id.food_price);
            food_time = (TextView) itemView.findViewById(R.id.food_time);
            food_image = (ImageView) itemView.findViewById(R.id.food_image);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            EditORDel = (CardView) itemView.findViewById(R.id.editOrDel);
        }
    }

    public class MyOnClickListener implements View.OnClickListener {

        public String foodId;

        public MyOnClickListener(String foodid) {
            this.foodId = foodid;
        }

        @Override
        public void onClick(View v) {
            if (status.equals("delete")) {
                DeleteFood();
            }else if (status.equals("edit")){
                EditFood();
            }
        }

        private void DeleteFood() {
            database.child("foods").child(foodId).child("status").setValue(1);
        }

        private void EditFood() {
            Intent intent = new Intent(context, AddFoodActivity.class);
            intent.putExtra("food",foodId);
            context.startActivity(intent);
        }
    }
}
