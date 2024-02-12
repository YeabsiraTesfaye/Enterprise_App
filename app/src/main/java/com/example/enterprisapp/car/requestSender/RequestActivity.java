package com.example.enterprisapp.car.requestSender;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.enterprisapp.R;
import com.example.enterprisapp.car.Model.Request;
import com.example.enterprisapp.enterprise.Model.MainApplication.HomeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public class RequestActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firestore;

    TextInputLayout reasonTI, timeTI, fromTI, toTI;
    Button requestBtn;
    Timestamp time;
    ProgressBar progressBar;

    FloatingActionButton info;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        sharedPreferences = getSharedPreferences("sp",MODE_PRIVATE);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        reasonTI = findViewById(R.id.reason);
        timeTI = findViewById(R.id.time);
        fromTI = findViewById(R.id.from);
        toTI = findViewById(R.id.to);
        requestBtn = findViewById(R.id.request);

        progressBar = findViewById(R.id.progress_bar);
        requestBtn.setEnabled(false);

        info = findViewById(R.id.info);
        info.setOnClickListener(click->{
            startActivity(new Intent(RequestActivity.this, StatusViewActivity.class));
        });
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getRequest(handler);
                    handler.postDelayed(this,1000);
                }
            },1000);
    }
    void requestCar(){
        String reason = reasonTI.getEditText().getText().toString();
        String from = fromTI.getEditText().getText().toString();
        String to = toTI.getEditText().getText().toString();
        if(reason.trim().length() > 0 && from.trim().length() > 0 && to.trim().length() > 0 && time != null){
            if(time.compareTo(Timestamp.now()) < 0){
                Toast.makeText(RequestActivity.this, "Time for request can not be before now", Toast.LENGTH_SHORT).show();
            }else{
                Request request = new Request(firebaseUser.getDisplayName(),"",reason,time,Timestamp.now(),1,"",from,to,0, null, null);
                firestore.collection("requests").add(request).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        reasonTI.getEditText().setText("");
                        timeTI.getEditText().setText("");
                        fromTI.getEditText().setText("");
                        toTI.getEditText().setText("");

                        time = null;
                        progressBar.setVisibility(View.GONE);
                        requestBtn.setEnabled(true);

                        Toast.makeText(RequestActivity.this,"Request Sent Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else{
            Toast.makeText(RequestActivity.this,"Fill out all fields!!!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            requestBtn.setEnabled(true);
        }
    }

    void getRequest(Handler handler){
        try {
            firestore.collection("requests").whereEqualTo("nameOfEmployee", firebaseUser.getDisplayName()).whereEqualTo("status", 1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.size() != 0){
                        finish();
                        startActivity(new Intent(RequestActivity.this, StatusViewActivity.class));
                        handler.removeCallbacksAndMessages(null);
                        startActivity(new Intent(RequestActivity.this, StatusViewActivity.class));
                    }else{
                        progressBar.setVisibility(View.GONE);
                        requestBtn.setEnabled(true);
                        timeTI.getEditText().setOnClickListener(click->{
                            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(timeTI.getApplicationWindowToken(),0);
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR);
                            int mMonth = c.get(Calendar.MONTH);
                            int mDay = c.get(Calendar.DAY_OF_MONTH);
                            DatePickerDialog datePickerDialog = new DatePickerDialog(RequestActivity.this,
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {

                                            final Calendar c = Calendar.getInstance();
                                            // on below line we are getting our hour, minute.
                                            int hour = c.get(Calendar.HOUR_OF_DAY);
                                            int minute = c.get(Calendar.MINUTE);
                                            // on below line we are initializing our Time Picker Dialog
                                            TimePickerDialog timePickerDialog = new TimePickerDialog(RequestActivity.this,
                                                    new TimePickerDialog.OnTimeSetListener() {
                                                        @Override
                                                        public void onTimeSet(TimePicker view, int hourOfDay,
                                                                              int minute) {
                                                            final Calendar c = Calendar.getInstance();
                                                            c.set(year,monthOfYear,dayOfMonth,hourOfDay,minute);

                                                            time = new Timestamp(c.getTime());


                                                            timeTI.getEditText().setText(time.toDate()+"");
                                                        }
                                                    }, hour, minute, false);
                                            timePickerDialog.show();
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        });
                        requestBtn.setOnClickListener(click ->{
                            progressBar.setVisibility(View.VISIBLE);
                            requestBtn.setEnabled(false);
                            requestCar();
                        });
                    }
                }
            });
        }catch (Exception e){

        }


    }

    @Override
    public void onBackPressed() {
        if(sharedPreferences.getInt("role", 0) == 1 || sharedPreferences.getInt("role", 0) == 2 ){
            startActivity(new Intent(RequestActivity.this, HomeActivity.class));
        }
    }
}