package com.example.enterprisapp.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.enterprisapp.Model.Enterprise;
import com.example.enterprisapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;


public class AddToPayFragment extends Fragment {
    Timestamp date;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    TextInputLayout name;
    TextInputLayout total;
    TextInputLayout registered;
    TextView datePicker;
    Button add;
    View root;
    ProgressBar progressBar;

    public AddToPayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_to_pay, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        progressBar = getActivity().findViewById(R.id.progress_bar);
        firestore = FirebaseFirestore.getInstance();

        name = root.findViewById(R.id.name);
        total = root.findViewById(R.id.no_of_emp);
        registered = root.findViewById(R.id.no_of_reg_emp);
        datePicker = root.findViewById(R.id.appointment1);
        add = root.findViewById(R.id.saveToPayEnterprise);

        datePicker.setOnClickListener(click ->{
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        final Calendar c = Calendar.getInstance();
                        c.set(year,monthOfYear,dayOfMonth);

                        date = new Timestamp(c.getTime());

                        datePicker.setText(date.toDate().toString());

                    }
                }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        add.setOnClickListener(click->{
            if(
                    name.getEditText().getText().toString().trim().length() != 0 &&
                            registered.getEditText().getText().toString().trim().length() != 0 &&
                            total.getEditText().getText().toString().trim().length() != 0 &&
                            datePicker.getText().toString().trim().length() != 0
            ){
                progressBar.setVisibility(View.VISIBLE);
                Enterprise enterprise = new Enterprise(name.getEditText().getText().toString(),
                        firebaseUser.getDisplayName(), date,2,Integer.parseInt(total.getEditText().getText().toString()),Integer.parseInt(registered.getEditText().getText().toString()));

                firestore.collection("enterprises").add(enterprise).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(),"Enterprise Added Successfully",Toast.LENGTH_SHORT).show();
                        name.getEditText().setText("");
                        datePicker.setText("");
                        total.getEditText().setText("");
                        registered.getEditText().setText("");
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }else{
                Toast.makeText(getContext(), "Please Fill out All fields", Toast.LENGTH_SHORT).show();
            }
        });




        return root;
    }
}