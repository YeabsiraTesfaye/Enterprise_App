package com.example.enterprisapp.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.enterprisapp.Model.Enterprise;
import com.example.enterprisapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class AddPayFragment extends Fragment {

    View root;
    ProgressBar progressBar;
    FirebaseFirestore firestore;
    TextInputLayout name;
    TextInputLayout freq, total, registered;
//    TextInputLayout rm;
    TextInputLayout status;
    Button add;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    public AddPayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         root = inflater.inflate(R.layout.fragment_add_pay, container, false);

         firebaseAuth = FirebaseAuth.getInstance();
         firebaseUser = firebaseAuth.getCurrentUser();

        progressBar =getActivity().findViewById(R.id.progress_bar);
        firestore = FirebaseFirestore.getInstance();

        name = root.findViewById(R.id.enterpriseName);
        freq = root.findViewById(R.id.freq_add_pay);
        total = root.findViewById(R.id.no_of_emp);
        registered = root.findViewById(R.id.no_of_reg_emp);
//        rm = root.findViewById(R.id.add_pay_relationManager);
        status = root.findViewById(R.id.status_add_pay);
        add = root.findViewById(R.id.saveEnterprise);

        add.setOnClickListener(click->{
            if(
                    name.getEditText().getText().toString().trim().length() != 0 &&
                            freq.getEditText().getText().toString().trim().length() != 0 &&
                            total.getEditText().getText().toString().trim().length() != 0 &&
                            registered.getEditText().getText().toString().trim().length() != 0 &&
                            status.getEditText().getText().toString().trim().length() != 0
            ){
                progressBar.setVisibility(View.VISIBLE);
                Enterprise enterprise = new Enterprise(name.getEditText().getText().toString()
                        , firebaseUser.getDisplayName(),status.getEditText().getText().toString(),
                        Integer.parseInt(freq.getEditText().getText().toString()),
                        Integer.parseInt(registered.getEditText().getText().toString()),
                        Integer.parseInt(total.getEditText().getText().toString()),
                        1);

                firestore.collection("enterprises").add(enterprise).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(),"Enterprise Added Successfully",Toast.LENGTH_SHORT).show();
                        name.getEditText().setText("");
                        freq.getEditText().setText("");
                        total.getEditText().setText("");
                        registered.getEditText().setText("");
                        status.getEditText().setText("");
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