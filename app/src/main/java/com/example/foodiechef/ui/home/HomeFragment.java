package com.example.foodiechef.ui.home;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.foodiechef.CategoryActivity;
import com.example.foodiechef.NavigationActivity;
import com.example.foodiechef.R;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private HomeViewModel homeViewModel;
    private LinearLayout breakfast,lunch,dinner,fastfood;
    private ImageView menu;
    private Bundle bundle;
    private Resources res;
    private Drawable drawable;
    private Intent intent;
    String title = "title";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        breakfast = root.findViewById(R.id.breakfast);
        lunch = root.findViewById(R.id.lunch);
        dinner = root.findViewById(R.id.dinner);
        fastfood = root.findViewById(R.id.fastfood);
        menu = root.findViewById(R.id.menu);
        res = getResources();
        bundle = new Bundle();

        breakfast.setOnClickListener(this);
        lunch.setOnClickListener(this);
        dinner.setOnClickListener(this);
        fastfood.setOnClickListener(this);
        menu.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {

        intent = new Intent(getActivity(), CategoryActivity.class);
        int x = v.getId();
        switch (x){
            case R.id.breakfast:
                intent.putExtra(title,"Breakfast");
                getActivity().startActivity(intent);
                break;
            case R.id.lunch:
                intent.putExtra(title,"Lunch");
                getActivity().startActivity(intent);
                break;
            case R.id.dinner:
                intent.putExtra(title,"Dinner");
                getActivity().startActivity(intent);
                break;
            case R.id.fastfood:
                intent.putExtra(title,"Fast Food");
                getActivity().startActivity(intent);
                break;
            case R.id.menu:
//                getActivity().finish();
                NavigationActivity.getDrawer();
        }
    }
}