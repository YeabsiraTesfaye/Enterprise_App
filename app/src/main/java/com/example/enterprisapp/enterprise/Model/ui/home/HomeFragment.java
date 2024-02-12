package com.example.enterprisapp.enterprise.Model.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.enterprisapp.R;
import com.example.enterprisapp.databinding.FragmentHomeBinding;
import com.example.enterprisapp.enterprise.Model.Enterprise;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    TableLayout payEntListLayout;
    private SharedPreferences sharedPreferences;
    private TableRow pay_header;
    int previousCount = 0;

    public HomeFragment(){

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        sharedPreferences = getActivity().getSharedPreferences("sp",MODE_PRIVATE);
        payEntListLayout = root.findViewById(R.id.payEnterprise);
        pay_header = (TableRow) getLayoutInflater().inflate(R.layout.pay_header,null,false);

        try{
            firestore.collection("enterprises").whereEqualTo("status_type",1).orderBy("name").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    payEntListLayout.removeAllViews();
                    payEntListLayout.addView(pay_header);
                    if(queryDocumentSnapshots.size() != 0){
                        payEntListLayout.setVisibility(View.VISIBLE);
                        for(QueryDocumentSnapshot q: queryDocumentSnapshots){
                            try{
                                Enterprise enterprise = q.toObject(Enterprise.class);
                                View payEntList = getLayoutInflater().inflate(R.layout.pay_ent_list,null,false);
                                TextView name = payEntList.findViewById(R.id.name);
                                TextView total = payEntList.findViewById(R.id.total);
                                TextView registered = payEntList.findViewById(R.id.registered);
                                TextView status = payEntList.findViewById(R.id.pay_ent_status);
                                TextView freq = payEntList.findViewById(R.id.pay_ent_freq);
                                TextView RM = payEntList.findViewById(R.id.pay_ent_RM);
                                name.setText(enterprise.getName());
                                total.setText(enterprise.getNo_of_total_emp()+"");
                                registered.setText(enterprise.getNo_of_reg_emp()+"");
                                status.setText(enterprise.getStatus());
                                freq.setText(enterprise.getFrequency()+"");
                                RM.setText(enterprise.getPM());
                                if(sharedPreferences.getInt("role",0) == 1){
                                    freq.setOnClickListener(click->{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                        builder.setMessage("Increment Frequency?");

                                        builder.setCancelable(true);

                                        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                                            enterprise.setFrequency(enterprise.getFrequency()+1);
                                            firestore.collection("enterprises").document(q.getId()).set(enterprise).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(getContext(), "Date Updated Successfully", Toast.LENGTH_SHORT).show();
                                                    freq.setText(enterprise.getFrequency()+"");
                                                }
                                            });
                                        });

                                        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                                        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                                            // If user click no then dialog box is canceled.
                                            dialog.cancel();
                                        });

                                        // Create the Alert dialog
                                        AlertDialog alertDialog = builder.create();
                                        // Show the Alert Dialog box
                                        alertDialog.show();
                                    });
                                    status.setOnClickListener(click->{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("Status");

                                        // set the custom layout
                                        final View customLayout = getLayoutInflater().inflate(R.layout.edit_statis_layout, null);
                                        builder.setView(customLayout);
                                        EditText editText = customLayout.findViewById(R.id.editText);
                                        editText.setText(enterprise.getStatus());

                                        builder.setNegativeButton("Cancel", (dialog, which)->{
                                            dialog.cancel();
                                        });


                                        // add a button
                                        builder.setPositiveButton("OK", (dialog, which) -> {
                                            if(editText.getText().toString().trim().length() != 0){
                                                enterprise.setStatus(editText.getText().toString());
                                                firestore.collection("enterprises").document(q.getId()).set(enterprise).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getContext(), "Status Updated Successfully", Toast.LENGTH_SHORT).show();
                                                        status.setText(editText.getText().toString());
                                                    }
                                                });
                                            }else{
                                                Toast.makeText(getContext(), "Status can not be empty", Toast.LENGTH_SHORT).show();

                                            }
                                            // send data from the AlertDialog to the Activity

                                        });
                                        // create and show the alert dialog
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    });

                                    total.setOnClickListener(click->{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("Total Employees");

                                        // set the custom layout
                                        final View customLayout = getLayoutInflater().inflate(R.layout.edit_statis_layout, null);
                                        builder.setView(customLayout);
                                        EditText editText = customLayout.findViewById(R.id.editText);
                                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                        editText.setText(enterprise.getNo_of_total_emp()+"");

                                        builder.setNegativeButton("Cancel", (dialog, which)->{
                                            dialog.cancel();
                                        });


                                        // add a button
                                        builder.setPositiveButton("OK", (dialog, which) -> {
                                            if(editText.getText().toString().trim().length() != 0){
                                                enterprise.setNo_of_total_emp(Integer.parseInt(editText.getText().toString()));
                                                firestore.collection("enterprises").document(q.getId()).set(enterprise).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getContext(), "Total Employees Updated Successfully", Toast.LENGTH_SHORT).show();
                                                        total.setText(editText.getText().toString());
                                                    }
                                                });
                                            }else{
                                                Toast.makeText(getContext(), "Status can not be empty", Toast.LENGTH_SHORT).show();

                                            }
                                            // send data from the AlertDialog to the Activity

                                        });
                                        // create and show the alert dialog
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    });

                                    registered.setOnClickListener(click->{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("Registered Employees");

                                        // set the custom layout
                                        final View customLayout = getLayoutInflater().inflate(R.layout.edit_statis_layout, null);
                                        builder.setView(customLayout);
                                        EditText editText = customLayout.findViewById(R.id.editText);
                                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                        editText.setText(enterprise.getNo_of_reg_emp()+"");

                                        builder.setNegativeButton("Cancel", (dialog, which)->{
                                            dialog.cancel();
                                        });


                                        // add a button
                                        builder.setPositiveButton("OK", (dialog, which) -> {
                                            if(editText.getText().toString().trim().length() != 0){
                                                enterprise.setNo_of_reg_emp(Integer.parseInt(editText.getText().toString()));
                                                firestore.collection("enterprises").document(q.getId()).set(enterprise).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getContext(), "Registered Employees Updated Successfully", Toast.LENGTH_SHORT).show();
                                                        registered.setText(editText.getText().toString());
                                                    }
                                                });
                                            }else{
                                                Toast.makeText(getContext(), "Status can not be empty", Toast.LENGTH_SHORT).show();

                                            }
                                            // send data from the AlertDialog to the Activity

                                        });
                                        // create and show the alert dialog
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    });
                                    if(firebaseUser.getEmail().trim().equals("amzpaulos@gmail.com")|| firebaseUser.getEmail().trim().equals("hannahmequanint@gmail.com")){
                                        name.setOnLongClickListener(new View.OnLongClickListener() {
                                            @Override
                                            public boolean onLongClick(View v) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                                builder.setMessage("Delete?");

                                                builder.setCancelable(true);

                                                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                                                    enterprise.setFrequency(enterprise.getFrequency()+1);
                                                    firestore.collection("enterprises").document(q.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(getContext(), "Delete Successfully", Toast.LENGTH_SHORT).show();
                                                            payEntListLayout.removeView(payEntList);
                                                        }
                                                    });
                                                });

                                                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                                                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                                                    // If user click no then dialog box is canceled.
                                                    dialog.cancel();
                                                });

                                                // Create the Alert dialog
                                                AlertDialog alertDialog = builder.create();
                                                // Show the Alert Dialog box
                                                alertDialog.show();
                                                return false;
                                            }
                                        });

                                    }

                                }
                                else{
                                    status.setTooltipText(enterprise.getStatus());
                                    status.setOnClickListener(click->{
                                        status.performLongClick();
                                    });


                                }

                                payEntListLayout.addView(payEntList);
                            }catch (Exception e){

                            }

                        }
                    }
                    else{
                        payEntListLayout.setVisibility(View.GONE);
                    }

                }
            });

        }catch (Exception e){

        }
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshDataChange();
                handler.postDelayed(this,5000);
            }
        },5000);


        return root;
    }

    private void refreshDataChange() {
        try{
            firestore.collection("enterprises").whereEqualTo("status_type",1).orderBy("name").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.size() != previousCount){
                        payEntListLayout.removeAllViews();
                        payEntListLayout.addView(pay_header);
                        if(queryDocumentSnapshots.size() != 0){
                            payEntListLayout.setVisibility(View.VISIBLE);
                            for(QueryDocumentSnapshot q: queryDocumentSnapshots){
                                try{
                                    Enterprise enterprise = q.toObject(Enterprise.class);
                                    View payEntList = getLayoutInflater().inflate(R.layout.pay_ent_list,null,false);
                                    TextView name = payEntList.findViewById(R.id.name);
                                    TextView total = payEntList.findViewById(R.id.total);
                                    TextView registered = payEntList.findViewById(R.id.registered);
                                    TextView status = payEntList.findViewById(R.id.pay_ent_status);
                                    TextView freq = payEntList.findViewById(R.id.pay_ent_freq);
                                    TextView RM = payEntList.findViewById(R.id.pay_ent_RM);
                                    name.setText(enterprise.getName());
                                    total.setText(enterprise.getNo_of_total_emp()+"");
                                    registered.setText(enterprise.getNo_of_reg_emp()+"");
                                    status.setText(enterprise.getStatus());
                                    freq.setText(enterprise.getFrequency()+"");
                                    RM.setText(enterprise.getPM());
                                    if(sharedPreferences.getInt("role",0) == 1){
                                        freq.setOnClickListener(click->{
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                            builder.setMessage("Increment Frequency?");

                                            builder.setCancelable(true);

                                            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                                                enterprise.setFrequency(enterprise.getFrequency()+1);
                                                firestore.collection("enterprises").document(q.getId()).set(enterprise).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getContext(), "Date Updated Successfully", Toast.LENGTH_SHORT).show();
                                                        freq.setText(enterprise.getFrequency()+"");
                                                    }
                                                });
                                            });

                                            // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                                            builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                                                // If user click no then dialog box is canceled.
                                                dialog.cancel();
                                            });

                                            // Create the Alert dialog
                                            AlertDialog alertDialog = builder.create();
                                            // Show the Alert Dialog box
                                            alertDialog.show();
                                        });
                                        status.setOnClickListener(click->{
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle("Status");

                                            // set the custom layout
                                            final View customLayout = getLayoutInflater().inflate(R.layout.edit_statis_layout, null);
                                            builder.setView(customLayout);
                                            EditText editText = customLayout.findViewById(R.id.editText);
                                            editText.setText(enterprise.getStatus());

                                            builder.setNegativeButton("Cancel", (dialog, which)->{
                                                dialog.cancel();
                                            });


                                            // add a button
                                            builder.setPositiveButton("OK", (dialog, which) -> {
                                                if(editText.getText().toString().trim().length() != 0){
                                                    enterprise.setStatus(editText.getText().toString());
                                                    firestore.collection("enterprises").document(q.getId()).set(enterprise).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(getContext(), "Status Updated Successfully", Toast.LENGTH_SHORT).show();
                                                            status.setText(editText.getText().toString());
                                                        }
                                                    });
                                                }else{
                                                    Toast.makeText(getContext(), "Status can not be empty", Toast.LENGTH_SHORT).show();

                                                }
                                                // send data from the AlertDialog to the Activity

                                            });
                                            // create and show the alert dialog
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        });

                                        total.setOnClickListener(click->{
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle("Total Employees");

                                            // set the custom layout
                                            final View customLayout = getLayoutInflater().inflate(R.layout.edit_statis_layout, null);
                                            builder.setView(customLayout);
                                            EditText editText = customLayout.findViewById(R.id.editText);
                                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                            editText.setText(enterprise.getNo_of_total_emp()+"");

                                            builder.setNegativeButton("Cancel", (dialog, which)->{
                                                dialog.cancel();
                                            });


                                            // add a button
                                            builder.setPositiveButton("OK", (dialog, which) -> {
                                                if(editText.getText().toString().trim().length() != 0){
                                                    enterprise.setNo_of_total_emp(Integer.parseInt(editText.getText().toString()));
                                                    firestore.collection("enterprises").document(q.getId()).set(enterprise).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(getContext(), "Total Employees Updated Successfully", Toast.LENGTH_SHORT).show();
                                                            total.setText(editText.getText().toString());
                                                        }
                                                    });
                                                }else{
                                                    Toast.makeText(getContext(), "Status can not be empty", Toast.LENGTH_SHORT).show();

                                                }
                                                // send data from the AlertDialog to the Activity

                                            });
                                            // create and show the alert dialog
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        });

                                        registered.setOnClickListener(click->{
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle("Registered Employees");

                                            // set the custom layout
                                            final View customLayout = getLayoutInflater().inflate(R.layout.edit_statis_layout, null);
                                            builder.setView(customLayout);
                                            EditText editText = customLayout.findViewById(R.id.editText);
                                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                            editText.setText(enterprise.getNo_of_reg_emp()+"");

                                            builder.setNegativeButton("Cancel", (dialog, which)->{
                                                dialog.cancel();
                                            });


                                            // add a button
                                            builder.setPositiveButton("OK", (dialog, which) -> {
                                                if(editText.getText().toString().trim().length() != 0){
                                                    enterprise.setNo_of_reg_emp(Integer.parseInt(editText.getText().toString()));
                                                    firestore.collection("enterprises").document(q.getId()).set(enterprise).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(getContext(), "Registered Employees Updated Successfully", Toast.LENGTH_SHORT).show();
                                                            registered.setText(editText.getText().toString());
                                                        }
                                                    });
                                                }else{
                                                    Toast.makeText(getContext(), "Status can not be empty", Toast.LENGTH_SHORT).show();

                                                }
                                                // send data from the AlertDialog to the Activity

                                            });
                                            // create and show the alert dialog
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        });
                                        if(firebaseUser.getEmail().trim().equals("amzpaulos@gmail.com")|| firebaseUser.getEmail().trim().equals("hannahmequanint@gmail.com")){
                                            name.setOnLongClickListener(new View.OnLongClickListener() {
                                                @Override
                                                public boolean onLongClick(View v) {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                                    builder.setMessage("Delete?");

                                                    builder.setCancelable(true);

                                                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                                                        enterprise.setFrequency(enterprise.getFrequency()+1);
                                                        firestore.collection("enterprises").document(q.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(getContext(), "Delete Successfully", Toast.LENGTH_SHORT).show();
                                                                payEntListLayout.removeView(payEntList);
                                                            }
                                                        });
                                                    });

                                                    // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                                                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                                                        // If user click no then dialog box is canceled.
                                                        dialog.cancel();
                                                    });

                                                    // Create the Alert dialog
                                                    AlertDialog alertDialog = builder.create();
                                                    // Show the Alert Dialog box
                                                    alertDialog.show();
                                                    return false;
                                                }
                                            });

                                        }

                                    }
                                    else{
                                        status.setTooltipText(enterprise.getStatus());
                                        status.setOnClickListener(click->{
                                            status.performLongClick();
                                        });


                                    }

                                    payEntListLayout.addView(payEntList);
                                }catch (Exception e){

                                }

                            }
                        }
                        else{
                            payEntListLayout.setVisibility(View.GONE);
                        }
                    }
                }
            });

        }catch (Exception e){

        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}