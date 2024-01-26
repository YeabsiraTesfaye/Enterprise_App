package com.example.enterprisapp.car.driver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enterprisapp.MainActivity;
import com.example.enterprisapp.R;
import com.example.enterprisapp.car.Request;
import com.example.enterprisapp.car.adapter.RequestRVAdapter;
import com.example.enterprisapp.databinding.ActivityDriverBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DriverActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    private ArrayList<Request> requestsArrayList;
    private RequestRVAdapter requestRVAdapter;
    CardView no_req;
    private ActivityDriverBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_driver);
        binding = ActivityDriverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarLayout2.findViewById(R.id.toolbar));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.idRVRequests);

        no_req = findViewById(R.id.no_req);

        requestsArrayList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(DriverActivity.this));

        // adding our array list to our recycler view adapter class.
        requestRVAdapter = new RequestRVAdapter(requestsArrayList, DriverActivity.this);

        // setting adapter to our recycler view.
        recyclerView.setAdapter(requestRVAdapter);

        getRequests();
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getRequests();

                handler.postDelayed(this,5000);
            }
        },1000);
    }
    void getRequests(){
        try{
            firestore.collection("requests").whereEqualTo("status",2).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            requestsArrayList.removeAll(requestsArrayList);
                            recyclerView.removeAllViews();
                            if (!queryDocumentSnapshots.isEmpty()) {
                                no_req.setVisibility(View.GONE);

                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot d : list) {
                                    Request request = d.toObject(Request.class);
                                    requestsArrayList.add(request);
                                }
                                requestRVAdapter.notifyDataSetChanged();
                            } else {
                                no_req.setVisibility(View.VISIBLE);
//                                    Toast.makeText(getContext(), "You don't have requests yet.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DriverActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception e){

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.logout){
            googleSignInClient = GoogleSignIn.getClient(DriverActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

            // Sign out from google
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // Check condition
                    if (task.isSuccessful()) {
                        // bWhen task is successful sign out from firebase
                        firebaseAuth.signOut();
                        // Display Toast
                        Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
                        // Finish activity
                        startActivity(new Intent(DriverActivity.this, MainActivity.class));
                        finish();
                    }
                }
            });
            return true;

        }else{
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {

    }
}