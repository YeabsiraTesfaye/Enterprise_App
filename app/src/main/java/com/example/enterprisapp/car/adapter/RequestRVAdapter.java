package com.example.enterprisapp.car.adapter;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enterprisapp.R;
import com.example.enterprisapp.Service;
import com.example.enterprisapp.car.Model.Request;
import com.example.enterprisapp.car.driver.MapsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RequestRVAdapter extends RecyclerView.Adapter<RequestRVAdapter.ViewHolder> {
	private static final int REQUEST_LOCATION = 1;
	// creating variables for our ArrayList and context
	private final ArrayList<Request> RequestArrayList;
	SharedPreferences sharedPreferences;
	String[] status_text = {"PENDING","ACCEPTED","DECLINED","DONE", "TRIP STARTED", "CANCELED BY ADMIN", "CANCELED BY USER"};

	private final Context context;
	Button b;
	FirebaseFirestore firestore;

	String lat = "";
	String lng = "";

	// creating constructor for our adapter class
	public RequestRVAdapter(ArrayList<Request> RequestArrayList, Context context) {
		this.RequestArrayList = RequestArrayList;
		this.context = context;
		sharedPreferences = context.getSharedPreferences("sp",MODE_PRIVATE);
		firestore = FirebaseFirestore.getInstance();
	}

	@NonNull
	@Override
	public RequestRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		// passing our layout file for displaying our card item
		return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.request_view_layout, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull RequestRVAdapter.ViewHolder holder, int position) {
		// setting data to our text views from our modal class.
		holder.start.setEnabled(true);
		Request request = RequestArrayList.get(position);
		holder.employee_name.setText(request.getNameOfEmployee());
		holder.reason.setText(request.getReason());
		holder.from.setText(request.getFrom());
		holder.to.setText(request.getTo());

		int hr1 = request.getForWhen().toDate().getHours();
		if(hr1 == 0){
			hr1 = 12;
		}

		String[] arr_for_date = request.getForWhen().toDate().toString().split(" ");
		String str_for_date = arr_for_date[0]+" "+arr_for_date[1]+" "+arr_for_date[2]+" "+arr_for_date[5] + " at " + hr1+":"+request.getForWhen().toDate().getMinutes();
		holder.time_needed.setText(str_for_date);

		int hr2 = request.getRequestTime().toDate().getHours();
		if(hr2 == 0){
			hr2 = 12;
		}
		String[] arr_req_date = request.getRequestTime().toDate().toString().split(" ");
		String str_req_date = arr_req_date[0]+" "+arr_req_date[1]+" "+arr_req_date[2]+" "+arr_req_date[5] + " at " + hr2+":"+request.getForWhen().toDate().getMinutes();
		holder.requested_time.setText(str_req_date);

		holder.status.setText(status_text[request.getStatus()-1]);
		holder.remark.setText(request.getRemark());
		String d = request.getDistance()+"";
		if(d.trim().length() > 5){
			holder.distance.setText(d.substring(0,5)+" KM");

		}else{
			holder.distance.setText(d+" KM");
		}

		holder.start.setOnClickListener(click->{
			holder.start.setEnabled(false);

			getLocation(request);
		});
		if(sharedPreferences.getInt("role",0) == 3 || sharedPreferences.getInt("role",0) == 2){
			holder.start.setVisibility(View.GONE);
			holder.itemView.setOnClickListener(click->{
				FirebaseFirestore firestore = FirebaseFirestore.getInstance();
				AlertDialog.Builder builder = new AlertDialog.Builder(context);

				// Set the message show for the Alert time
//                            builder.setMessage("Change ");

				// Set Alert Title
				builder.setTitle("Accept Request?");

				// Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
				builder.setCancelable(true);

				// Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
				builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
					// When the user click yes button then app will close
					request.setStatus(2);
					firestore.collection("requests").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
						@Override
						public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
							for (QueryDocumentSnapshot q: queryDocumentSnapshots){
								Request r = q.toObject(Request.class);
								if(r.getNameOfEmployee().equals(request.getNameOfEmployee()) && r.getRequestTime().compareTo(request.getRequestTime()) == 0){
									firestore.collection("requests").document(q.getId()).set(request).addOnSuccessListener(new OnSuccessListener<Void>() {
										@Override
										public void onSuccess(Void unused) {
											Toast.makeText(context, "Date Updated Successfully", Toast.LENGTH_SHORT).show();
											holder.status.setText("ACCEPTED");
											holder.status.setTextColor(Color.parseColor("#0fb000"));
											holder.itemView.setOnClickListener(click->{

											});

										}
									});
								}
							}
						}
					});

				});

				// Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
				builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
					AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
					builder2.setTitle("Why?");
					request.setStatus(3);
					// set the custom layout
					final View customLayout = View.inflate(context,R.layout.edit_statis_layout, null);
					builder2.setView(customLayout);
					EditText editText = customLayout.findViewById(R.id.editText);

					builder2.setNegativeButton("Cancel", (dialog1, which1)->{
						dialog1.cancel();
					});


					// add a button
					builder2.setPositiveButton("OK", (dialog1, which1) -> {
						if(editText.getText().toString().trim().length() != 0){
							request.setRemark(editText.getText().toString());
							firestore.collection("requests").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
								@Override
								public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
									for (QueryDocumentSnapshot q: queryDocumentSnapshots){
										Request r = q.toObject(Request.class);
										if(r.getNameOfEmployee().equals(request.getNameOfEmployee()) && r.getRequestTime().compareTo(request.getRequestTime()) == 0){
											firestore.collection("requests").document(q.getId()).set(request).addOnSuccessListener(new OnSuccessListener<Void>() {
												@Override
												public void onSuccess(Void unused) {
													holder.status.setText("DECLINED");
													holder.status.setTextColor(Color.parseColor("#8a0000"));
													holder.itemView.setOnClickListener(click->{

													});
													Toast.makeText(context, "Request Declined", Toast.LENGTH_SHORT).show();
												}
											});
										}
									}
								}
							});
						}else{
							Toast.makeText(context, "Reason can not be empty", Toast.LENGTH_SHORT).show();

						}
						// send data from the AlertDialog to the Activity

					});
					// create and show the alert dialog
					AlertDialog dialog2 = builder2.create();
					dialog2.show();
					request.setStatus(3);


					// If user click no then dialog box is canceled.
					dialog.cancel();
				});

				// Create the Alert dialog
				AlertDialog alertDialog = builder.create();
				// Show the Alert Dialog box
				alertDialog.show();

			});
			if(request.getStatus() == 1){
				holder.status.setTextColor(Color.parseColor("#a3a300"));
				holder.remark.setVisibility(View.GONE);
				holder.distance.setVisibility(View.GONE);

			}else if(request.getStatus() == 2){
				holder.status.setTextColor(Color.parseColor("#0fb000"));
				holder.remark.setVisibility(View.GONE);
				holder.distance.setVisibility(View.GONE);

				holder.itemView.setOnClickListener(click->{

				});
			}else if(request.getStatus() == 3){
				holder.status.setTextColor(Color.parseColor("#8a0000"));
				holder.remark.setTextColor(Color.parseColor("#8a0000"));
				holder.remark.setVisibility(View.VISIBLE);
				holder.distance.setVisibility(View.GONE);
				holder.itemView.setOnClickListener(click->{

				});
			}else if(request.getStatus() == 4){
				holder.status.setTextColor(Color.parseColor("#000000"));
				holder.remark.setVisibility(View.GONE);
				holder.distance.setVisibility(View.VISIBLE);
				holder.itemView.setOnClickListener(click->{

				});
			}else if(request.getStatus() == 5){
				holder.status.setTextColor(Color.parseColor("#8a0000"));
				holder.remark.setVisibility(View.GONE);
				holder.distance.setVisibility(View.GONE);

				holder.itemView.setOnClickListener(click->{


				});

			}else if(request.getStatus() == 6){
				holder.status.setTextColor(Color.parseColor("#8a0000"));
				holder.remark.setVisibility(View.GONE);
				holder.distance.setVisibility(View.GONE);

				holder.itemView.setOnClickListener(click->{

				});

			}

		}
		else {
			holder.reason.setVisibility(View.GONE);
//			holder.status.setVisibility(View.GONE);
//			holder.time_needed.setVisibility(View.GONE);
			holder.requested_time.setVisibility(View.GONE);
//			holder.rf.setVisibility(View.GONE);
			holder.ro.setVisibility(View.GONE);
			holder.r.setVisibility(View.GONE);
			holder.start.setVisibility(View.VISIBLE);
		}



	}

	@Override
	public int getItemCount() {
		// returning the size of our array list.
		return RequestArrayList.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		// creating variables for our text views.
		private final TextView employee_name;
		private final TextView reason;
		private final TextView time_needed;
		private final TextView requested_time;
		private final TextView status;
		private final TextView remark,r, from, to, distance;
		private final LinearLayout rf,ro;
		private final Button start;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			// initializing our text views.
			employee_name = itemView.findViewById(R.id.emp_name);
			reason = itemView.findViewById(R.id.reason1);
			time_needed = itemView.findViewById(R.id.time_needed);
			requested_time = itemView.findViewById(R.id.req_time);
			status = itemView.findViewById(R.id.status);
			remark = itemView.findViewById(R.id.remark);
			rf = itemView.findViewById(R.id.rf);
			ro = itemView.findViewById(R.id.ro);
			r = itemView.findViewById(R.id.r);
			from = itemView.findViewById(R.id.from);
			to = itemView.findViewById(R.id.to);
			start = itemView.findViewById(R.id.start);
			distance = itemView.findViewById(R.id.distance);
			b = itemView.findViewById(R.id.start);
		}
	}

	private void getLocation(Request request) {
		if(Timestamp.now().compareTo(request.getForWhen()) < 0){
			Toast.makeText(context,"You cant start trip before the time it is requested for",Toast.LENGTH_SHORT).show();
		}else{
			FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
			if (ActivityCompat.checkSelfPermission(
					context,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
					context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
			} else {
				client.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener() {
					@Override
					public void onSuccess(Object o) {
						if (o != null) {
							Location here = (Location)o;
							lat = here.getLatitude()+"";
							lng = here.getLongitude()+"";
							Intent intent =new Intent(context, MapsActivity.class);
							request.setStarted(Timestamp.now());

							request.setStatus(5);

							firestore.collection("requests").whereEqualTo("requestTime",request.getRequestTime()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
								@Override
								public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
									for (QueryDocumentSnapshot q: queryDocumentSnapshots){
										Request r = q.toObject(Request.class);
										firestore.collection("requests").document(q.getId()).set(request);

										SharedPreferences.Editor editor = sharedPreferences.edit();
										editor.putString("distance","0");
										editor.putString("lat", lat);
										editor.putString("lng", lng);
										editor.putString("id",q.getId());
										editor.commit();
										context.stopService(new Intent(context, Service.class));
										context.startActivity(intent);
									}

								}
							}).addOnFailureListener(new OnFailureListener() {
								@Override
								public void onFailure(@NonNull Exception e) {

								}
							});
						}
					}
				});
			}
		}

	}
}