package com.example.enterprisapp.enterprise.Model.MainApplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.enterprisapp.MainActivity;
import com.example.enterprisapp.R;
import com.example.enterprisapp.car.admin.AdminActivity;
import com.example.enterprisapp.car.requestSender.RequestActivity;
import com.example.enterprisapp.databinding.ActivityHome2Binding;
import com.example.enterprisapp.enterprise.Model.Enterprise;
import com.example.enterprisapp.enterprise.Model.ui.AddPayFragment;
import com.example.enterprisapp.enterprise.Model.ui.AddToPayFragment;
import com.example.enterprisapp.enterprise.Model.ui.addUnderCommunicationFragment;
import com.example.enterprisapp.enterprise.Model.ui.dashboard.DashboardFragment;
import com.example.enterprisapp.enterprise.Model.ui.home.HomeFragment;
import com.example.enterprisapp.enterprise.Model.ui.notifications.NotificationsFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity
        implements BottomNavigationView
        .OnNavigationItemSelectedListener {

    FloatingActionButton addEnterprise, addMerchant, addCustomer;

    // Use the ExtendedFloatingActionButton to handle the
    // parent FAB
    ExtendedFloatingActionButton mAddFab;

    // to check whether sub FABs are visible or not

    TextView addEnterpriseActionText, addMerchantActionText, addCustomerActionText;
    Boolean isAllFabsVisible;
    FirebaseFirestore firestore;
    int reg_emp, exp_emp, pay_ent, to_pay_ent, und_comm_ent = 0;


    private ActivityHome2Binding binding;
    GoogleSignInClient googleSignInClient;
FirebaseAuth firebaseAuth;
FirebaseUser firebaseUser;

    BottomNavigationView bottomNavigationView;
    private OnBackPressedCallback onBackPressedCallback;
    private SharedPreferences sharedPreferences;
    TextView total_emp_tv, reg_emp_tv, pay_ent_tv, to_pay_ent_tv,under_comm_tv;
    ImageButton requestCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        androidx.appcompat.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(Color.WHITE);//for red colored toolbar title
        setSupportActionBar(toolbar);

        total_emp_tv = findViewById(R.id.totalExpectedEnterprises);
        reg_emp_tv = findViewById(R.id.totalRegisteredEnterprises);
        pay_ent_tv= findViewById(R.id.totalPaid);
        to_pay_ent_tv = findViewById(R.id.totalExpected);
        under_comm_tv = findViewById(R.id.totalUnderComm);
        sharedPreferences = getSharedPreferences("sp",MODE_PRIVATE);

        requestCar = findViewById(R.id.request);
        requestCar.setOnClickListener(click->{
            if(sharedPreferences.getInt("role",0) == 2){
                PopupMenu popup = new PopupMenu(this, click);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.role_1, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.requestCar){
                            startActivity(new Intent(HomeActivity.this, RequestActivity.class));
                            return true;

                        }else if(item.getItemId() == R.id.approveRequest){
                            startActivity(new Intent(HomeActivity.this, AdminActivity.class));
                            return true;

                        }
                        return false;
                    }
                });
            }
            else{
                startActivity(new Intent(HomeActivity.this, RequestActivity.class));
            }
        });




        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);

        bottomNavigationView
                .setOnNavigationItemSelectedListener(this);

        firestore = FirebaseFirestore.getInstance();

        mAddFab = findViewById(R.id.add_fab);
        // FAB button
        addEnterprise = findViewById(R.id.add_enterprise_fab);
        addMerchant = findViewById(R.id.add_merchant_fab);
        addCustomer = findViewById(R.id.add_customer_fab);

        addEnterpriseActionText = findViewById(R.id.add_enterprise_action_text);
        addMerchantActionText = findViewById(R.id.add_merchant_action_text);
        addCustomerActionText = findViewById(R.id.add_customer_action_text);


        addEnterprise.setVisibility(View.GONE);
        addMerchant.setVisibility(View.GONE);
        addCustomer.setVisibility(View.GONE);
        addEnterpriseActionText.setVisibility(View.GONE);
        addMerchantActionText.setVisibility(View.GONE);
        addCustomerActionText.setVisibility(View.GONE);
        isAllFabsVisible = false;
        mAddFab.shrink();
        mAddFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {

                            // when isAllFabsVisible becomes
                            // true make all the action name
                            // texts and FABs VISIBLE.
                            addEnterprise.show();
                            addMerchant.show();
                            addCustomer.show();

                            addEnterpriseActionText.setVisibility(View.VISIBLE);
                            addMerchantActionText.setVisibility(View.VISIBLE);
                            addCustomerActionText.setVisibility(View.VISIBLE);
                            mAddFab.extend();
                            isAllFabsVisible = true;
                        } else {
                            addEnterprise.hide();
                            addMerchant.hide();
                            addCustomer.hide();

                            addEnterpriseActionText.setVisibility(View.GONE);
                            addMerchantActionText.setVisibility(View.GONE);
                            addCustomerActionText.setVisibility(View.GONE);
                            mAddFab.shrink();
                            isAllFabsVisible = false;
                        }
                    }
                });
        addMerchant.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAddFab.performClick();
                        Fragment addToPayFragment = new AddToPayFragment();
                        FragmentManager fm = getSupportFragmentManager();
                        fm.beginTransaction().replace(R.id.flFragment,addToPayFragment).commit();
                    }
                });
        addEnterprise.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAddFab.performClick();
                        Fragment addPayFragment = new AddPayFragment();
                        FragmentManager fm = getSupportFragmentManager();
                        fm.beginTransaction().replace(R.id.flFragment,addPayFragment).commit();
                    }
                });
        addCustomer.setOnClickListener(click->{
            mAddFab.performClick();
            Fragment addUnderCommunicationFragment = new addUnderCommunicationFragment();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.flFragment,addUnderCommunicationFragment).commit();
        });

        if(sharedPreferences.getInt("role",0) != 1){
            mAddFab.setVisibility(View.GONE);
        }else{
            mAddFab.setVisibility(View.VISIBLE);

        }

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            try{
                                firestore.collection("enterprises").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for(QueryDocumentSnapshot q: queryDocumentSnapshots){
                                            Enterprise enterprise = q.toObject(Enterprise.class);
                                            if(enterprise.getStatus_type() == 1){
                                                pay_ent+=1;
                                                exp_emp += enterprise.getNo_of_total_emp();
                                                reg_emp += enterprise.getNo_of_reg_emp();
                                            } else if (enterprise.getStatus_type() == 2) {
                                                to_pay_ent+=1;
                                                exp_emp += enterprise.getNo_of_total_emp();
                                                reg_emp += enterprise.getNo_of_reg_emp();
                                            }
                                            else if (enterprise.getStatus_type() == 3) {
                                                und_comm_ent+=1;
                                            }


                                        }
                                        total_emp_tv.setText(exp_emp+"");
                                        reg_emp_tv.setText(reg_emp+"");
                                        pay_ent_tv.setText(pay_ent+"");
                                        to_pay_ent_tv.setText(to_pay_ent+"");
                                        under_comm_tv.setText(und_comm_ent+"");
                                        exp_emp =0;
                                        reg_emp=0;
                                        pay_ent=0;
                                        to_pay_ent=0;
                                        und_comm_ent = 0;
                                    }

                                });
                            }catch (Exception e){

                            }
                            //your method here
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000);





        onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Your business logic to handle the back pressed event
                shrinkFab();
//            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_admin2, new DashboardFragment()).commit();

            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

    }

    HomeFragment firstFragment = new HomeFragment();
    DashboardFragment secondFragment = new DashboardFragment();
    NotificationsFragment thirdFragment = new NotificationsFragment();


    @Override
    public boolean
    onNavigationItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.navigation_home){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, firstFragment)
                    .commit();
            shrinkFab();
        } else if (item.getItemId() == R.id.navigation_dashboard) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, secondFragment)
                    .commit();
            shrinkFab();
        }else if(item.getItemId() == R.id.navigation_notifications){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, thirdFragment)
                    .commit();
            shrinkFab();
        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.logout){
            googleSignInClient = GoogleSignIn.getClient(HomeActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

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
                        startActivity(new Intent(HomeActivity.this,MainActivity.class));
                        finish();
                    }
                }
            });
            return true;

        }else{
            return super.onOptionsItemSelected(item);
        }
    }
    void shrinkFab(){
        if (isAllFabsVisible) {
            mAddFab.performClick();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
}