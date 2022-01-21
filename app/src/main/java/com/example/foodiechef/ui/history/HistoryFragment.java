package com.example.foodiechef.ui.history;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.foodiechef.AddFoodActivity;
import com.example.foodiechef.R;
import com.example.foodiechef.ui.Food;
import com.example.foodiechef.ui.RecyclerAdapter_Food;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;
import com.wangjie.rapidfloatingactionbutton.util.RFABShape;
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    private RecyclerView rv;
    private List<Food> foods;
    private DatabaseReference database;
    private SharedPreferences preferences;
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel =
                ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        rfaLayout = (RapidFloatingActionLayout) root.findViewById(R.id.rfal);
        rfaButton = (RapidFloatingActionButton) root.findViewById(R.id.rfab);
        rv = root.findViewById(R.id.food_list);
        final Bundle argument = getArguments();

        foods = new ArrayList<>();

        final String uID = preferences.getString("chefID", "abc");
        database.child("foods").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (argument.getString("profile").equals("chef")) {
                    foods = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getValue(Food.class).getUserID().equals(uID)) {
                            foods.add(child.getValue(Food.class));
                        }
                    }
                    rfaButton.setVisibility(View.GONE);
                    RecyclerAdapter_Food recyclerAdapterFood = new RecyclerAdapter_Food(getContext(), foods);
                    rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                    rv.setAdapter(recyclerAdapterFood);
                } else {
                    foods = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getValue(Food.class).getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && child.getValue(Food.class).getStatus() != 1 ) {
                            foods.add(child.getValue(Food.class));
                        }
                    }
                    RecyclerAdapter_History recyclerAdapter = new RecyclerAdapter_History(getContext(), foods,"add");
                    rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                    rv.setAdapter(recyclerAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        {
            RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(getContext());
            rfaContent.setOnRapidFloatingActionContentLabelListListener(new RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener() {
                @Override
                public void onRFACItemLabelClick(int position, RFACLabelItem item) {
                    if(position == 0){
                        RecyclerAdapter_History recyclerAdapter = new RecyclerAdapter_History(getContext(), foods,"delete");
                        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rv.setAdapter(recyclerAdapter);
                        rfaButton.performClick();
                    } else if(position == 1){
                        RecyclerAdapter_History recyclerAdapter = new RecyclerAdapter_History(getContext(), foods,"add");
                        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rv.setAdapter(recyclerAdapter);
                        rfaButton.performClick();
                        Intent intent = new Intent(getActivity(), AddFoodActivity.class);
                        startActivity(intent);
                    } else if(position == 2){
                        RecyclerAdapter_History recyclerAdapter = new RecyclerAdapter_History(getContext(), foods,"edit");
                        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rv.setAdapter(recyclerAdapter);
                        rfaButton.performClick();
                    }
                }

                @Override
                public void onRFACItemIconClick(int position, RFACLabelItem item) {
                    if(position == 0){
                        RecyclerAdapter_History recyclerAdapter = new RecyclerAdapter_History(getContext(), foods,"delete");
                        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rv.setAdapter(recyclerAdapter);
                        rfaButton.performClick();
                    } else if(position == 1){
                        RecyclerAdapter_History recyclerAdapter = new RecyclerAdapter_History(getContext(), foods,"add");
                        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rv.setAdapter(recyclerAdapter);
                        rfaButton.performClick();
                        Intent intent = new Intent(getActivity(), AddFoodActivity.class);
                        startActivity(intent);
                    } else if(position == 2){
                        RecyclerAdapter_History recyclerAdapter = new RecyclerAdapter_History(getContext(), foods,"edit");
                        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rv.setAdapter(recyclerAdapter);
                        rfaButton.performClick();
                    }
                }
            });
            List<RFACLabelItem> items = new ArrayList<>();
            items.add(new RFACLabelItem<Integer>()
                    .setLabel("Delete")
                    .setResId(R.drawable.ic_delete_black_20dp)
                    /*.setIconNormalColor(R.color.app)
                    .setIconPressedColor(R.color.colorPrimaryDark)*/
                    .setWrapper(0)
            );
            items.add(new RFACLabelItem<Integer>()
                    .setLabel("Add")
                    .setResId(R.drawable.ic_add_black_24dp)
                    /*.setIconNormalColor(R.color.app)
                    .setIconPressedColor(R.color.colorPrimaryDark)*/
                    .setWrapper(1)
            );
            items.add(new RFACLabelItem<Integer>()
                    .setLabel("Edit")
                    .setResId(R.drawable.ic_edit_black_24dp)
                    /*.setIconNormalColor(R.color.app)
                    .setIconPressedColor(R.color.colorPrimaryDark)*/
                    .setWrapper(2)
            );
            rfaContent
                    .setItems(items)
                    .setIconShadowRadius(RFABTextUtil.dip2px(getContext(), 5))
                    .setIconShadowColor(0xff888888)
                    .setIconShadowDy(RFABTextUtil.dip2px(getContext(), 1))
            ;

            rfabHelper = new RapidFloatingActionHelper(
                    getContext(),
                    rfaLayout,
                    rfaButton,
                    rfaContent
            ).build();
        }

        return root;
    }
}