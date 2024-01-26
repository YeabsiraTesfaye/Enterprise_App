package com.example.enterprisapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.enterprisapp.MainApplication.HomeActivity;
import com.example.enterprisapp.Model.User;
import com.example.enterprisapp.car.admin.AdminActivity;
import com.example.enterprisapp.car.driver.DriverActivity;
import com.example.enterprisapp.car.requestSender.RequestActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    SignInButton btSignIn;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("sp",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        firestore = FirebaseFirestore.getInstance();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        if(!isConnected()){
//            startActivity(new Intent(LoginActivity.this, NoInternetActivity.class));
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        // Assign variable
        btSignIn = findViewById(R.id.sign_in_button);

        // Initialize sign in options the client-id is copied form google-services.json file
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, googleSignInOptions);

        btSignIn.setOnClickListener(view -> {
            // Initialize sign in intent
            Intent intent = googleSignInClient.getSignInIntent();
            // Start activity for result
            startActivityForResult(intent, 100);
        });

        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        // Initialize firebase user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        // Check condition
        if (firebaseUser != null) {
            if(sharedPreferences.getInt("role",0) == 1 || sharedPreferences.getInt("role",0) == 2){
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            } else if (sharedPreferences.getInt("role",0) == 3) {
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
            }
            else if (sharedPreferences.getInt("role",0) == 4) {
                Intent intent = new Intent(MainActivity.this, DriverActivity.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(MainActivity.this, RequestActivity.class);
                startActivity(intent);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check condition
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        if (requestCode == 100) {
            // When request code is equal to 100 initialize task
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            // check condition
            if (signInAccountTask.isSuccessful()) {
                // When google sign in successful initialize string
                String s = "Google sign in successful";
                // Display Toast
                displayToast(s);
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null initialize auth credential
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        // Check credential
                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Check condition
                                if (task.isSuccessful()) {
                                    // When task is successful redirect to profile activity display Toast
                                    firestore.collection("users").whereEqualTo("email",firebaseAuth.getCurrentUser().getEmail().trim()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if(queryDocumentSnapshots.size() > 0){
                                                for(QueryDocumentSnapshot q: queryDocumentSnapshots){
                                                    User user = q.toObject(User.class);
                                                    myEdit.putInt("role",user.getRole());
                                                    myEdit.commit();

                                                    if(user.getRole() == 1 || user.getRole() == 2){
                                                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                                        startActivity(intent);
                                                    } else if (user.getRole() == 3) {
                                                        Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                                        startActivity(intent);
                                                    } else if (user.getRole() == 4) {
                                                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                    }else{
                                                        Intent intent = new Intent(MainActivity.this, RequestActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            }else{
//                                                Toast.makeText(MainActivity.this, "You don't have permission to use this platform", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(MainActivity.this, RequestActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });

                                    displayToast("Firebase authentication successful");
                                } else {
                                    // When task is unsuccessful display Toast
                                    displayToast("Authentication Failed :" + task.getException().getMessage());
                                }
                            }
                        });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();

    }
}