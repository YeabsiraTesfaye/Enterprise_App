package com.example.enterprisapp.car.requestSender;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.enterprisapp.R;
import com.example.enterprisapp.car.Model.Request;
import com.example.enterprisapp.enterprise.Model.MainApplication.HomeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class StatusViewActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firestore;
    CardView no_req;
    LinearLayout card;
    int status = 0;
    String[] status_text = {"PENDING","ACCEPTED","DECLINED","DONE"};
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_view);

        sharedPreferences = getSharedPreferences("sp",MODE_PRIVATE);
        card = findViewById(R.id.card);
        no_req = findViewById(R.id.no_req);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        getStatus();
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getStatus();
                handler.postDelayed(this,1000);
            }
        },1000);
    }
    void getStatus(){
        try{
            firestore.collection("requests").whereEqualTo("nameOfEmployee", firebaseUser.getDisplayName()).orderBy("requestTime").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    if(queryDocumentSnapshots.size() > 0){

                        no_req.setVisibility(View.GONE);
                        card.setVisibility(View.VISIBLE);
                        card.removeAllViews();
                        for (QueryDocumentSnapshot q: queryDocumentSnapshots){
                            Request request = q.toObject(Request.class);
                            View view = getLayoutInflater().inflate(R.layout.all_req_view,null,false);
                            TextView nameTIL = view.findViewById(R.id.emp_name);
                            TextView reasonTIL = view.findViewById(R.id.reason1);
                            TextView needed_timeTIL = view.findViewById(R.id.time_needed);
                            TextView req_timeTIL = view.findViewById(R.id.req_time);
                            TextView statusTIL = view.findViewById(R.id.status);
                            TextView remark = view.findViewById(R.id.remark);
                            Button cancel = view.findViewById(R.id.cancel_request);
                            TextView fromTIL = view.findViewById(R.id.from);
                            TextView toTIL = view.findViewById(R.id.to);

                            status = request.getStatus();
                            nameTIL.setText(request.getNameOfEmployee());
                            reasonTIL.setText(request.getReason());
                            fromTIL.setText(request.getFrom());
                            toTIL.setText(request.getTo());

                            cancel.setOnClickListener(click->{
                                AlertDialog.Builder builder = new AlertDialog.Builder(StatusViewActivity.this);

                                // Set the message show for the Alert time
//                            builder.setMessage("Change ");

                                // Set Alert Title
                                builder.setMessage("Are you sure?");

                                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                                builder.setCancelable(true);

                                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                                    // When the user click yes button then app will close

                                    firestore.collection("requests").document(q.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(StatusViewActivity.this, "Date Updated Successfully", Toast.LENGTH_SHORT).show();

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

                            int hr2 = request.getForWhen().toDate().getHours();
                            if(hr2 == 0){
                                hr2 = 12;
                            }
                            if(hr2 > 12){
                                hr2 -= 12;
                            }

                            String[] arr_for_date = request.getForWhen().toDate().toString().split(" ");
                            String str_for_date = arr_for_date[0]+" "+arr_for_date[1]+" "+arr_for_date[2]+" "+arr_for_date[5] + " at " + hr2+":"+request.getForWhen().toDate().getMinutes();
                            needed_timeTIL.setText(str_for_date);


                            int hr1 = request.getRequestTime().toDate().getHours();
                            if(hr1 == 0){
                                hr1 = 12;
                            }
                            if(hr1 > 12){
                                hr1 -= 12;
                            }
                            String[] arr_req_date = request.getRequestTime().toDate().toString().split(" ");
                            String str_req_date = arr_req_date[0]+" "+arr_req_date[1]+" "+arr_req_date[2]+" "+arr_req_date[5] + " at " + hr1+":"+request.getForWhen().toDate().getMinutes();
                            req_timeTIL.setText(str_req_date);

                            remark.setText(request.getRemark());
                            statusTIL.setText(status_text[request.getStatus()-1]);
                            if(request.getStatus() == 1){
                                statusTIL.setTextColor(Color.parseColor("#8c7300"));
                                remark.setVisibility(View.GONE);
                                cancel.setVisibility(View.VISIBLE);

                            }else if(request.getStatus() == 2){
                                statusTIL.setTextColor(Color.parseColor("#09b500"));
                                remark.setVisibility(View.GONE);
                                cancel.setVisibility(View.GONE);

                            }else if(request.getStatus() == 3){
                                statusTIL.setTextColor(Color.parseColor("#ff0000"));
                                cancel.setVisibility(View.GONE);
                                remark.setVisibility(View.VISIBLE);
                            }
                            card.addView(view);

                        }
                    }else{
                        card.setVisibility(View.GONE);
                        no_req.setVisibility(View.VISIBLE);
                    }

                }
            });
        }catch(Exception e){

        }

    }
    @Override
    public void onBackPressed() {
        if(status != 1){
            startActivity(new Intent(StatusViewActivity.this, RequestActivity.class));
        }else{
            if(sharedPreferences.getInt("role", 0) == 1 || sharedPreferences.getInt("role", 0) == 2 ){
                startActivity(new Intent(StatusViewActivity.this, HomeActivity.class));
            }
        }
    }
}