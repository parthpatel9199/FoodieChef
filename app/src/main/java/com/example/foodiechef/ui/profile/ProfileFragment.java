package com.example.foodiechef.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.example.foodiechef.R;
import com.example.foodiechef.StartPageAdapter;
import com.example.foodiechef.ui.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.foodiechef.NavigationActivity.user_image;
import static com.example.foodiechef.NavigationActivity.user_name;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private CircleImageView user_image1;
    private ImageView edit_image,demo;
    public static TextView user_name1;
    private Bitmap bm;
    int SELECT_IMAGE = 0;
    int REQUEST_CAMERA = 1;
    int PIC_CROP = 2;
    private String link;
    Uri picuri;

    private FirebaseStorage storage;
    private StorageReference reference;
    private DatabaseReference database;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();
        user_image1 = root.findViewById(R.id.user_image);
        user_name1 = root.findViewById(R.id.user_name);
        edit_image = root.findViewById(R.id.edit_image);
        demo = root.findViewById(R.id.demo);

        ViewPager viewPager = root.findViewById(R.id.viewpager2);
        String profile = "profile";
        TabLayout tabLayout = root.findViewById(R.id.tab_layout_profile);
        tabLayout.setupWithViewPager(viewPager);

        setupViewPager(viewPager);

        edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        setProfile();

        return root;
    }

    private void setProfile() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        if (preferences.getString("image", "1").equals("1")) {
            user_image1.setImageResource(R.drawable.git);
        } else {
            Picasso.get().load(preferences.getString("image", "1")).placeholder(R.drawable.git).into(user_image1);
        }
        user_name1.setText(preferences.getString("name", "username"));
        user_name.setText(preferences.getString("name", "username"));

    }

    private void setupViewPager(ViewPager viewPager) {
        ProfilePageAdapter pagerAdapter = new ProfilePageAdapter(getActivity().getApplicationContext(), getChildFragmentManager(), "profile");
        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private void selectImage() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle("Select One");

        LinearLayout camera = dialog.findViewById(R.id.camera);
        LinearLayout gallery = dialog.findViewById(R.id.gallery);
        Button cancel = dialog.findViewById(R.id.cancel);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(chooserIntent, REQUEST_CAMERA);
                dialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE);
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {
                if (data != null) {
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        user_image1.setImageBitmap(bm);
                        user_image.setImageBitmap(bm);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == REQUEST_CAMERA) {
                Log.e("fun",data.getData().toString());
//                CropImage();
                bm = (Bitmap) data.getExtras().get("data");
                user_image1.setImageBitmap(bm);
                user_image.setImageBitmap(bm);
            }
            else if(requestCode == PIC_CROP){
                Bundle bitmap = (Bundle) data.getExtras();
                Bitmap bitmap1 = (Bitmap) bitmap.get("data");
                demo.setImageBitmap(bitmap1);
            }

            /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] b = stream.toByteArray();
            final String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference reference1 = reference.child(uID);
            reference1.putBytes(b)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit();
                            String link = "https://firebasestorage.googleapis.com/v0/b/foodie-chef.appspot.com/o/" + uID + "?alt=media";
                            editor.putString("image", link);
                            database.child("users").child(uID).child("image").setValue(link);
                            editor.commit();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });*/
        }

    }

    public void CropImage(){
        Log.e("fun","called");
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        Log.e("fun",picuri.toString());
//        cropIntent.setDataAndType(picuri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, PIC_CROP);
    }

}