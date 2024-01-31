package com.example.enterprisapp.enterprise.Model.ui.notifications;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.enterprisapp.R;
import com.example.enterprisapp.databinding.FragmentNotificationsBinding;
import com.example.enterprisapp.enterprise.Model.Enterprise;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    FirebaseFirestore firestore;
    TableLayout underCommEntListLayout;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TableRow under_comm_header;

    int previousCount = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        sharedPreferences = getActivity().getSharedPreferences("sp",MODE_PRIVATE);

        under_comm_header = (TableRow) getLayoutInflater().inflate(R.layout.under_comm_header,null,false);



        underCommEntListLayout = root.findViewById(R.id.underComm);

        try {
            firestore.collection("enterprises").whereEqualTo("status_type",3).orderBy("name").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.size() != 0){
                        underCommEntListLayout.setVisibility(View.VISIBLE);
                        underCommEntListLayout.removeAllViews();
                        underCommEntListLayout.addView(under_comm_header);
                        for(QueryDocumentSnapshot q: queryDocumentSnapshots){
                            try {
                                Enterprise enterprise = q.toObject(Enterprise.class);
                                View payEntList = getLayoutInflater().inflate(R.layout.under_communication_ent_layout,null,false);

                                TextView name = payEntList.findViewById(R.id.name);
                                TextView total = payEntList.findViewById(R.id.total);
                                TextView lastInteraction = payEntList.findViewById(R.id.last_interaction);
                                TextView status = payEntList.findViewById(R.id.status);
                                TextView totalInteraction = payEntList.findViewById(R.id.TotalInteractions);
                                TextView RM = payEntList.findViewById(R.id.rm);

                                name.setText(enterprise.getName());
                                total.setText(enterprise.getNo_of_total_emp()+"");


                                String[] arr_date = enterprise.getDate().toDate().toString().split(" ");
                                String str_date = arr_date[0]+" "+arr_date[1]+" "+arr_date[2]+" "+arr_date[5];
                                lastInteraction.setText(str_date);

                                status.setText(enterprise.getStatus());
                                totalInteraction.setText(enterprise.getFrequency()+"");
                                RM.setText(enterprise.getPM());

                                if(sharedPreferences.getInt("role",0) == 1){
                                    lastInteraction.setOnClickListener(click->{
                                        final Calendar c = Calendar.getInstance();
                                        int mYear = c.get(Calendar.YEAR);
                                        int mMonth = c.get(Calendar.MONTH);
                                        int mDay = c.get(Calendar.DAY_OF_MONTH);
                                        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                                                new DatePickerDialog.OnDateSetListener() {
                                                    @Override
                                                    public void onDateSet(DatePicker view, int year,
                                                                          int monthOfYear, int dayOfMonth) {

                                                        c.set(year,monthOfYear,dayOfMonth);
                                                        Timestamp latestInteraction = new Timestamp(c.getTime());
                                                        enterprise.setDate(latestInteraction);
                                                        firestore.collection("enterprises").document(q.getId()).set(enterprise).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(getContext(), "Date Updated Successfully", Toast.LENGTH_SHORT).show();

                                                                String[] arr_date = latestInteraction.toDate().toString().split(" ");
                                                                String str_date = arr_date[0]+" "+arr_date[1]+" "+arr_date[2]+" "+arr_date[5];
                                                                lastInteraction.setText(str_date);

//                                                            lastInteraction.setText(latestInteraction.toDate().toString());
                                                            }
                                                        });

                                                    }
                                                }, mYear, mMonth, mDay);
                                        datePickerDialog.show();
                                    });
                                    totalInteraction.setOnClickListener(click->{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                        // Set the message show for the Alert time
//                            builder.setMessage("Change ");

                                        // Set Alert Title
                                        builder.setMessage("Increment Interaction Counter?");

                                        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                                        builder.setCancelable(true);

                                        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                                        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                                            // When the user click yes button then app will close
                                            enterprise.setFrequency(enterprise.getFrequency()+1);
                                            firestore.collection("enterprises").document(q.getId()).set(enterprise).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(getContext(), "Date Updated Successfully", Toast.LENGTH_SHORT).show();
                                                    totalInteraction.setText(enterprise.getFrequency()+"");
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

                                    if(firebaseUser.getEmail().trim().equals("amzpaulos@gmail.com")){
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
                                                            underCommEntListLayout.removeView(payEntList);
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

                                } else {
                                    status.setTooltipText(enterprise.getStatus());
                                    status.setOnClickListener(click -> {
                                        status.performLongClick();
                                    });
                                }
                                underCommEntListLayout.addView(payEntList);
                            }catch (Exception e){

                            }

                        }
                    }
                    else{
                        underCommEntListLayout.setVisibility(View.GONE);
                    }
                }
            });

            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshDataChange();
                    handler.postDelayed(this,5000);
                }
            },5000);

        }catch (Exception e){

        }


        return root;
    }

    void refreshDataChange(){
        firestore.collection("enterprises").whereEqualTo("status_type",3).orderBy("name").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size() != previousCount){
                    if(queryDocumentSnapshots.size() != 0){
                        underCommEntListLayout.setVisibility(View.VISIBLE);
                        underCommEntListLayout.removeAllViews();
                        underCommEntListLayout.addView(under_comm_header);
                        for(QueryDocumentSnapshot q: queryDocumentSnapshots){
                            try {
                                Enterprise enterprise = q.toObject(Enterprise.class);
                                View payEntList = getLayoutInflater().inflate(R.layout.under_communication_ent_layout,null,false);

                                TextView name = payEntList.findViewById(R.id.name);
                                TextView total = payEntList.findViewById(R.id.total);
                                TextView lastInteraction = payEntList.findViewById(R.id.last_interaction);
                                TextView status = payEntList.findViewById(R.id.status);
                                TextView totalInteraction = payEntList.findViewById(R.id.TotalInteractions);
                                TextView RM = payEntList.findViewById(R.id.rm);

                                name.setText(enterprise.getName());
                                total.setText(enterprise.getNo_of_total_emp()+"");


                                String[] arr_date = enterprise.getDate().toDate().toString().split(" ");
                                String str_date = arr_date[0]+" "+arr_date[1]+" "+arr_date[2]+" "+arr_date[5];
                                lastInteraction.setText(str_date);

                                status.setText(enterprise.getStatus());
                                totalInteraction.setText(enterprise.getFrequency()+"");
                                RM.setText(enterprise.getPM());

                                if(sharedPreferences.getInt("role",0) == 1){
                                    lastInteraction.setOnClickListener(click->{
                                        final Calendar c = Calendar.getInstance();
                                        int mYear = c.get(Calendar.YEAR);
                                        int mMonth = c.get(Calendar.MONTH);
                                        int mDay = c.get(Calendar.DAY_OF_MONTH);
                                        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                                                new DatePickerDialog.OnDateSetListener() {
                                                    @Override
                                                    public void onDateSet(DatePicker view, int year,
                                                                          int monthOfYear, int dayOfMonth) {

                                                        c.set(year,monthOfYear,dayOfMonth);
                                                        Timestamp latestInteraction = new Timestamp(c.getTime());
                                                        enterprise.setDate(latestInteraction);
                                                        firestore.collection("enterprises").document(q.getId()).set(enterprise).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(getContext(), "Date Updated Successfully", Toast.LENGTH_SHORT).show();

                                                                String[] arr_date = latestInteraction.toDate().toString().split(" ");
                                                                String str_date = arr_date[0]+" "+arr_date[1]+" "+arr_date[2]+" "+arr_date[5];
                                                                lastInteraction.setText(str_date);

//                                                            lastInteraction.setText(latestInteraction.toDate().toString());
                                                            }
                                                        });

                                                    }
                                                }, mYear, mMonth, mDay);
                                        datePickerDialog.show();
                                    });
                                    totalInteraction.setOnClickListener(click->{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                        // Set the message show for the Alert time
//                            builder.setMessage("Change ");

                                        // Set Alert Title
                                        builder.setMessage("Increment Interaction Counter?");

                                        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                                        builder.setCancelable(true);

                                        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                                        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                                            // When the user click yes button then app will close
                                            enterprise.setFrequency(enterprise.getFrequency()+1);
                                            firestore.collection("enterprises").document(q.getId()).set(enterprise).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(getContext(), "Date Updated Successfully", Toast.LENGTH_SHORT).show();
                                                    totalInteraction.setText(enterprise.getFrequency()+"");
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

                                    if(firebaseUser.getEmail().trim().equals("amzpaulos@gmail.com")){
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
                                                            underCommEntListLayout.removeView(payEntList);
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

                                } else {
                                    status.setTooltipText(enterprise.getStatus());
                                    status.setOnClickListener(click -> {
                                        status.performLongClick();
                                    });
                                }
                                underCommEntListLayout.addView(payEntList);
                            }catch (Exception e){

                            }

                        }
                    }
                    else{
                        underCommEntListLayout.setVisibility(View.GONE);
                    }
                }

            }
        });

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}