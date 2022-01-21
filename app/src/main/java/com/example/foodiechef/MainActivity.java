package com.example.foodiechef;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.foodiechef.ui.User;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private LinearLayout signin;
    private EditText email, password;
    private TextView forgot;
    private ImageView display;
    private String status_pass;
    private LinearLayout google, fb;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String EMAIL = "email";
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setBackgroundDrawableResource(R.drawable.back1);
        status_pass = "hide";
        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        google = findViewById(R.id.google);
        fb = findViewById(R.id.fb);
        signin = findViewById(R.id.signin);
        display = findViewById(R.id.display_pass);
        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);
        forgot = findViewById(R.id.forgot);

        mCallbackManager = CallbackManager.Factory.create();
        final LoginButton loginButton = findViewById(R.id.facebook);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().equals("") || password.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Credentials Required!!", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                                        editor.putString("login", "yes");
                                        editor.commit();
                                        startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e("cancel", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("Error", "facebook:onError" + error);
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPassword();
            }
        });

        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status_pass.equals("hide")) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    status_pass = "show";
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    status_pass = "hide";
                }
            }
        });

    }

    private void ForgotPassword() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dialog_forgot);

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final EditText email = dialog.findViewById(R.id.email);
        Button reset = dialog.findViewById(R.id.reset);
        Button cancel = dialog.findViewById(R.id.cancel);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailID = email.getText().toString().trim();
                if (emailID.equals("")) {
                    Toast.makeText(getApplicationContext(), "Credentials Required!!", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.sendPasswordResetEmail(emailID)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Reset Password link is sent.", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed to send reset email!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
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

    public void ChangeToSignUp(View view) {
        Intent signuppage = new Intent(MainActivity.this, SignupActivity.class);
        startActivity(signuppage);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(MainActivity.this, "Google sign in failed", Toast.LENGTH_LONG).show();
                Log.e("error", e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        final String[] status = {"No",""};
        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if(acct.getEmail().equals(child.getValue(User.class).getEmail())){
                        status[0] = "yes";
                        status[1] = child.getValue(User.class).getPassword();
                    }
                }
                if(status[0].equals("yes")){
                    mAuth.signInWithEmailAndPassword(acct.getEmail(), status[1])
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                                        editor.putString("login", "yes");
                                        editor.commit();
                                        startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(MainActivity.this, "Not Registered User", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void handleFacebookAccessToken(final AccessToken token) {

        final String[] email = new String[1];
        final String[] status = {"No",""};

        GraphRequest request = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            email[0] = object.getString("email");
                            Log.e("Login", email[0]);
                            database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot child : dataSnapshot.getChildren()){
                                        if(email[0].equals(child.getValue(User.class).getEmail())){
                                            status[0] = "yes";
                                            status[1] = child.getValue(User.class).getPassword();
                                        }
                                    }
                                    if(status[0].equals("yes")){
                                        mAuth.signInWithEmailAndPassword(email[0], status[1])
                                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            FirebaseUser user = mAuth.getCurrentUser();
                                                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                                                            editor.putString("login", "yes");
                                                            editor.commit();
                                                            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                                                            finish();
                                                        } else {
                                                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }else {
                                        Toast.makeText(MainActivity.this, "Not Registered User", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
