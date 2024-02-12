package com.example.enterprisapp.car.admin.ui.gallery;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.enterprisapp.R;
import com.example.enterprisapp.car.Model.Request;
import com.example.enterprisapp.databinding.FragmentGalleryBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    TableLayout tableLayout;
    FirebaseFirestore firestore;
    ArrayList<Integer> fromDate = new ArrayList<>();
    ArrayList<Integer> toDate = new ArrayList<>();

    String[] status_text = {"PENDING","ACCEPTED","DECLINED","DONE", "TRIP STARTED", "CANCELED BY ADMIN", "CANCELED BY USER"};
    String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    Button download;
    View header;
    ArrayList<Request> requests = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        tableLayout = root.findViewById(R.id.report_view);
        firestore = FirebaseFirestore.getInstance();
        download = root.findViewById(R.id.download);
        header = LayoutInflater.from(getContext()).inflate(R.layout.admin_report_layout,null,false);
        TextView name = header.findViewById(R.id.name);
        TextView from = header.findViewById(R.id.from);
        TextView to = header.findViewById(R.id.to);
        TextView start = header.findViewById(R.id.start);
        TextView end = header.findViewById(R.id.end);
        TextView req = header.findViewById(R.id.requested_on);
        TextView forr = header.findViewById(R.id.requested_for);
        TextView reason = header.findViewById(R.id.reason);
        TextView distance = header.findViewById(R.id.distance);
        TextView status = header.findViewById(R.id.status);
        name.setBackgroundColor(Color.parseColor("#F6D122"));
        from.setBackgroundColor(Color.parseColor("#F6D122"));
        to.setBackgroundColor(Color.parseColor("#F6D122"));
        start.setBackgroundColor(Color.parseColor("#F6D122"));
        end.setBackgroundColor(Color.parseColor("#F6D122"));
        req.setBackgroundColor(Color.parseColor("#F6D122"));
        forr.setBackgroundColor(Color.parseColor("#F6D122"));
        reason.setBackgroundColor(Color.parseColor("#F6D122"));
        distance.setBackgroundColor(Color.parseColor("#F6D122"));
        status.setBackgroundColor(Color.parseColor("#F6D122"));

        TextInputLayout fromDP = root.findViewById(R.id.fromDP);
        TextInputLayout toDP = root.findViewById(R.id.toDP);

        askForPermissions();

        fromDP.getEditText().setOnClickListener(click->{
            final Calendar c = Calendar.getInstance();

            // on below line we are getting
            // our day, month and year.
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // on below line we are creating a variable for date picker dialog.
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    // on below line we are passing context.
                    getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // on below line we are setting date to our text view.
                            fromDate.removeAll(fromDate);
                            fromDate.add(0,dayOfMonth);
                            fromDate.add(1,monthOfYear);
                            fromDate.add(2,year);

                            fromDP.getEditText().setText(months[monthOfYear]+" "+dayOfMonth+" "+year);
                            Toast.makeText(getContext(), toDate.size()+"",Toast.LENGTH_SHORT).show();
                            if(toDate.size() == 3){
                                filter();
                            }else{
                                download.setVisibility(View.GONE);
                            }
                        }
                    },
                    // on below line we are passing year,
                    // month and day for selected date in our date picker.
                    year, month, day);
            // at last we are calling show to
            // display our date picker dialog.
            datePickerDialog.show();
        });
        toDP.getEditText().setOnClickListener(click->{
            final Calendar c = Calendar.getInstance();

            // on below line we are getting
            // our day, month and year.
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // on below line we are creating a variable for date picker dialog.
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    // on below line we are passing context.
                    getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // on below line we are setting date to our text view.
                            toDate.removeAll(toDate);
                            toDate.add(0,dayOfMonth);
                            toDate.add(1,monthOfYear);
                            toDate.add(2,year);
                            toDP.getEditText().setText(months[monthOfYear]+" "+dayOfMonth+" "+year);
                            Toast.makeText(getContext(), fromDate.size()+"",Toast.LENGTH_SHORT).show();

                            if(fromDate.size() == 3){
                                filter();
                            }else{
                                download.setVisibility(View.GONE);
                            }
                        }
                    },
                    // on below line we are passing year,
                    // month and day for selected date in our date picker.
                    year, month, day);
            // at last we are calling show to
            // display our date picker dialog.
            datePickerDialog.show();
        });
        download.setOnClickListener(click->{
            ExportExcel exportExcel = new ExportExcel();
            exportExcel.RequestCustomers(getContext(),requests,
                    months[fromDate.get(1)]+" "+fromDate.get(0)+" "+fromDate.get(2)+" to "+ months[toDate.get(1)]+" "+toDate.get(0)+" "+toDate.get(2));
        });
        Calendar cal = Calendar.getInstance();
        int res = cal.getActualMaximum(Calendar.DATE);
        fromDate.removeAll(fromDate);
        fromDate.add(0,1);
        fromDate.add(1,Timestamp.now().toDate().getMonth());
        fromDate.add(2,Timestamp.now().toDate().getYear()+1900);

        toDate.removeAll(toDate);
        toDate.add(0,res);
        toDate.add(1,Timestamp.now().toDate().getMonth());
        toDate.add(2,Timestamp.now().toDate().getYear()+1900);
        fromDP.getEditText().setText(months[Timestamp.now().toDate().getMonth()]+" "+1+" "+(Timestamp.now().toDate().getYear()+1900));
        toDP.getEditText().setText(months[Timestamp.now().toDate().getMonth()]+" "+res+" "+(Timestamp.now().toDate().getYear()+1900));
        filter();

//        firestore.collection("requests").whereNotEqualTo("status",1).limit(25).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                tableLayout.removeAllViews();
//                tableLayout.addView(header);
//
//                for (QueryDocumentSnapshot q : queryDocumentSnapshots){
//                    Request request = q.toObject(Request.class);
//                    View rows = LayoutInflater.from(getContext()).inflate(R.layout.admin_report_layout,null,false);
//                    TextView name = rows.findViewById(R.id.name);
//                    TextView from = rows.findViewById(R.id.from);
//                    TextView to = rows.findViewById(R.id.to);
//                    TextView start = rows.findViewById(R.id.start);
//                    TextView end = rows.findViewById(R.id.end);
//                    TextView req = rows.findViewById(R.id.requested_on);
//                    TextView forr = rows.findViewById(R.id.requested_for);
//                    TextView reason = rows.findViewById(R.id.reason);
//                    TextView distance = rows.findViewById(R.id.distance);
//                    TextView status = rows.findViewById(R.id.status);
//
//                    if(
//                            request.getForWhen().toDate().getDate() >= 1&&
//                                    request.getForWhen().toDate().getDate() <= 31&&
//                                    request.getForWhen().toDate().getMonth() == Timestamp.now().toDate().getMonth()&&
//                                    request.getForWhen().toDate().getMonth() == Timestamp.now().toDate().getMonth()&&
//                                    request.getForWhen().toDate().getYear() == Timestamp.now().toDate().getYear()&&
//                                    request.getForWhen().toDate().getYear() == Timestamp.now().toDate().getYear()){
//
//
//
//
//
//
//                        name.setText(request.getNameOfEmployee());
//                        from.setText(request.getFrom());
//                        to.setText(request.getTo());
////                    start.setText(request.getStarted().toDate().toString());
////                    end.setText(request.getEnded().toDate().toString());
//                        req.setText(request.getRequestTime().toDate().toString());
//                        forr.setText(request.getForWhen().toDate().toString());
//                        reason.setText(request.getReason());
//
//                        String d = request.getDistance()+"";
//                        if(d.trim().length() > 5){
//                            distance.setText(d.substring(0,5)+" KM");
//                        }else{
//                            distance.setText(d+" KM");
//                        }
//                        status.setText(status_text[request.getStatus()-1]);
//
//                        tableLayout.addView(rows);
//                    }
//
//
//                }
//            }
//        });



        return root;
    }

    void filter(){
        firestore.collection("requests").whereNotEqualTo("status",1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size() > 0){
                    download.setVisibility(View.VISIBLE);
                    tableLayout.removeAllViews();
                    tableLayout.addView(header);

                    for (QueryDocumentSnapshot q : queryDocumentSnapshots){
                        Request request = q.toObject(Request.class);

                            if(
                                request.getForWhen().toDate().getDate() >= fromDate.get(0)&&
                                        request.getForWhen().toDate().getDate() <= toDate.get(0)&&
                                        request.getForWhen().toDate().getMonth()>= fromDate.get(1)&&
                                        request.getForWhen().toDate().getMonth()<= toDate.get(1)&&
                                        request.getForWhen().toDate().getYear()+1900>= fromDate.get(2)&&
                                        request.getForWhen().toDate().getYear()+1900<= toDate.get(2)){

                            requests.add(request);
                            View rows = LayoutInflater.from(getContext()).inflate(R.layout.admin_report_layout,null,false);
                            TextView name = rows.findViewById(R.id.name);
                            TextView from = rows.findViewById(R.id.from);
                            TextView to = rows.findViewById(R.id.to);
                            TextView start = rows.findViewById(R.id.start);
                            TextView end = rows.findViewById(R.id.end);
                            TextView req = rows.findViewById(R.id.requested_on);
                            TextView forr = rows.findViewById(R.id.requested_for);
                            TextView reason = rows.findViewById(R.id.reason);
                            TextView distance = rows.findViewById(R.id.distance);
                            TextView status = rows.findViewById(R.id.status);

                            name.setText(request.getNameOfEmployee());
                            from.setText(request.getFrom());
                            to.setText(request.getTo());
//                    start.setText(request.getStarted().toDate().toString());
//                    end.setText(request.getEnded().toDate().toString());
                            req.setText(request.getRequestTime().toDate().toString());
                            forr.setText(request.getForWhen().toDate().toString());
                            reason.setText(request.getReason());

                            String d = request.getDistance()+"";
                            if(d.trim().length() > 5){
                                distance.setText(d.substring(0,5)+" KM");

                            }else{
                                distance.setText(d+" KM");
                            }
                            status.setText(status_text[request.getStatus()-1]);

                            tableLayout.addView(rows);

                        }

                    }
                }else{
                    download.setVisibility(View.GONE);

                }

            }
        });

    }

    public void askForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}