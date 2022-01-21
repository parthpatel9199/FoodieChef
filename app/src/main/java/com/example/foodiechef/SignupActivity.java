package com.example.foodiechef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.foodiechef.ui.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText username,email,mobile,password,con_pass;
    private LinearLayout signup;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setBackgroundDrawableResource(R.drawable.back2);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference("users");
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile);
        password = findViewById(R.id.pass);
        con_pass = findViewById(R.id.con_pass);
        signup = findViewById(R.id.signup_but);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailID = email.getText().toString().trim();
                final String pass = password.getText().toString().trim();
                String c_pass = con_pass.getText().toString().trim();
                final String name = username.getText().toString().trim();
                final String mobileno = mobile.getText().toString().trim();

                if (emailID.equals("")||pass.equals("")||name.equals("")||c_pass.equals("")||mobileno.equals("")){
                    Toast.makeText(getApplicationContext(),"Credentials Required!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(mobileno.length() < 10){
                        Toast.makeText(getApplicationContext(), "Valid Mobile no. required!",Toast.LENGTH_SHORT).show();
                    }
                    else if (pass.length() < 6 ){
                        Toast.makeText(getApplicationContext(),"Password should be greater than 6 character", Toast.LENGTH_SHORT).show();
                    }
                    else if (!pass.equals(c_pass)){
                        Toast.makeText(getApplicationContext(),"Password should be same as Confirm Password", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        mAuth.createUserWithEmailAndPassword(emailID,pass)
                                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()){
                                            try {
                                                throw task.getException();
                                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                                Toast.makeText(SignupActivity.this, "Invalid email.",
                                                        Toast.LENGTH_SHORT).show();
                                            } catch(FirebaseAuthUserCollisionException e) {
                                                Toast.makeText(SignupActivity.this, "User already Registered.",
                                                        Toast.LENGTH_SHORT).show();
                                            } catch(Exception e) {
                                                Log.e("Error", e.getMessage());
                                            }
                                        }
                                        else {
                                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                            User user = new User(userId,name,pass,emailID,mobileno,"1");

                                            mReference.child(userId).setValue(user);

                                            startActivity(new Intent(SignupActivity.this,DashboardActivity.class));
                                            finish();
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    public void ChangeToLogin(View view) {
        Intent signuppage = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(signuppage);
        finish();
    }
}
