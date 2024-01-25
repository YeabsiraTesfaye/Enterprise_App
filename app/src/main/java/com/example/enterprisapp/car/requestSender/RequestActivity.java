package com.example.enterprisapp.car.requestSender;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.enterprisapp.R;
import com.example.enterprisapp.car.Request;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class RequestActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firestore;

    TextInputLayout reasonTI, timeTI;
    Button requestBtn;
    Timestamp time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        reasonTI = findViewById(R.id.reason);
        timeTI = findViewById(R.id.time);
        requestBtn = findViewById(R.id.request);

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
            requestCar();
        });
    }
    void requestCar(){
        String reason = reasonTI.getEditText().getText().toString();
        if(reason.trim().length() > 0 && time != null){
            Request request = new Request(firebaseUser.getDisplayName(),"",reason,time,Timestamp.now(),1,"");
            firestore.collection("requests").add(request).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    reasonTI.getEditText().setText("");
                    timeTI.getEditText().setText("");
                    time = null;
                    Toast.makeText(RequestActivity.this,"Request Sent Successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(RequestActivity.this,"Fill out all fields!!!", Toast.LENGTH_SHORT).show();
        }
    }
}