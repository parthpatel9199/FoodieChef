package com.example.foodiechef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodiechef.ui.Food;
import com.example.foodiechef.ui.history.HistoryFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

public class AddFoodActivity extends AppCompatActivity {

    private TextView title;
    private ImageView back;
    private Button cancel, add;
    private ImageView food_image,openmap;
    private ProgressBar progressBar;
    private Spinner spinner,hour,minute;
    private EditText food_category;
    private TextInputEditText food_name, food_price, food_ingrident;
    public static EditText location_here;
    int SELECT_IMAGE = 0;
    int REQUEST_CAMERA = 1;
    private Bitmap bm;
    private DatabaseReference database;
    private StorageReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        database = FirebaseDatabase.getInstance().getReference();
        reference = FirebaseStorage.getInstance().getReference();
        title = findViewById(R.id.title);
        progressBar = findViewById(R.id.upload);
        spinner = findViewById(R.id.food_category);
        hour = findViewById(R.id.hour);
        minute = findViewById(R.id.minute);
        food_category = findViewById(R.id.food_category_edit);
        food_name = findViewById(R.id.food_name);
        food_price = findViewById(R.id.food_price);
        food_ingrident = findViewById(R.id.food_ingrident);
        back = findViewById(R.id.backToProfile);
        cancel = findViewById(R.id.cancel);
        add = findViewById(R.id.addFood);
        openmap = findViewById(R.id.openmap);
        food_image = findViewById(R.id.food_image);
        location_here = findViewById(R.id.location_here);

        location_here.setFocusable(false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (preferences.getString("location","abc").equals("abc")){}else {
            location_here.setText(preferences.getString("location","abc"));
        }

        openmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFoodActivity.this,MapActivity.class);
                intent.putExtra("from","AddFood");
                startActivity(intent);
            }
        });

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.category_array)) {
            @Override
            public int getCount() {
                return 4;
            }
        };
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(4);
        ArrayAdapter arrayAdapter1 = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.hour_array));
        hour.setAdapter(arrayAdapter1);
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.minute_array));
        minute.setAdapter(arrayAdapter2);

        if(getIntent().getStringExtra("food") != null){
            title.setText("Edit Food");
            add.setText("Edit");
            database.child("foods").child(getIntent().getStringExtra("food")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Food food = dataSnapshot.getValue(Food.class);
                    food_name.setText(food.getName());
                    switch (food.getCategory()){
                        case "Breakfast":
                            spinner.setSelection(0);
                            break;
                        case "Lunch":
                            spinner.setSelection(1);
                            break;
                        case "Dinner":
                            spinner.setSelection(2);
                            break;
                        case "Fast Food":
                            spinner.setSelection(3);
                            break;
                    }
                    food_category.setText(food.getCategory());
                    food_price.setText(food.getPrice());
                    food_ingrident.setText(food.getIngredient());
                    Picasso.get().load(food.getImage()).into(food_image);
                    String time = food.getTime();
                    if(time.matches("[0-9] hr")){
                        hour.setSelection(Integer.parseInt(time.split(" ")[0]));
                    }
                    else if(time.matches("[0-9][0-9] min")){
                        if(time.split(" ")[0].equals("15")){
                            minute.setSelection(1);
                        }else if(time.split(" ")[0].equals("30")){
                            minute.setSelection(2);
                        }else if(time.split(" ")[0].equals("45")){
                            minute.setSelection(3);
                        }
                    }
                    else {
                        hour.setSelection(Integer.parseInt(time.split(" ")[0]));
                        if(time.split(" ")[2].equals("15")){
                            minute.setSelection(1);
                        }else if(time.split(" ")[2].equals("30")){
                            minute.setSelection(2);
                        }else if(time.split(" ")[2].equals("45")){
                            minute.setSelection(3);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


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

        food_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        food_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpinnerController();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                food_category.setText(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood();
            }
        });
    }

    private void selectImage() {

        final Dialog dialog = new Dialog(AddFoodActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle("Select One");

        LinearLayout camera = dialog.findViewById(R.id.camera);
        LinearLayout gallery = dialog.findViewById(R.id.gallery);
        Button cancel = dialog.findViewById(R.id.cancel);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
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
                        bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                        food_image.setImageBitmap(bm);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == REQUEST_CAMERA) {
                bm = (Bitmap) data.getExtras().get("data");
                food_image.setImageBitmap(bm);
            }
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    public void SpinnerController() {
        spinner.performClick();
    }

    public void addFood() {
        final String name = food_name.getText().toString().trim();
        final String category = food_category.getText().toString().trim();
        final String price = food_price.getText().toString().trim();
        final String ingrident = food_ingrident.getText().toString().trim();
        final String time;
        final String location = location_here.getText().toString().trim();

        if (name.equals("") || category.equals("") || price.equals("") || ingrident.equals("")
                || food_image.getDrawable() == getResources().getDrawable(R.drawable.ic_photo_black_24dp)||location.equals("")
                || (hour.getSelectedItem().toString().equals("0") && minute.getSelectedItem().toString().equals("0"))) {
            Toast.makeText(getApplicationContext(), "Credentials Required!!", Toast.LENGTH_SHORT).show();
        } else {
            if(hour.getSelectedItem().toString().equals("0")){
                time = minute.getSelectedItem().toString() + " min";
            }else if (minute.getSelectedItem().toString().equals("0")){
                time = hour.getSelectedItem().toString() + " hr";
            }
            else {
                time = hour.getSelectedItem().toString() + " hr " + minute.getSelectedItem().toString() + " min";
            }
            progressBar.setVisibility(View.VISIBLE);
            final String fID;
            if(add.getText().equals("Edit")){
                fID = getIntent().getStringExtra("food");
            }else {
                fID = database.child("foods").push().getKey();
            }
            final String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BitmapDrawable drawable = (BitmapDrawable) food_image.getDrawable();
            Bitmap bm = drawable.getBitmap();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] b = stream.toByteArray();
            final StorageReference reference1 = reference.child(fID);
            reference1.putBytes(b)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            reference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(AddFoodActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                    Food food = new Food(fID,name,category,ingrident,price,time,uri.toString(),uID);
                                    database.child("foods").child(fID).setValue(food);
                                    database.child("users").child(uID).child("location").setValue(location);
                                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                                    editor.putString("location", location);
                                    editor.commit();
                                    AddFoodActivity.this.finish();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddFoodActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
