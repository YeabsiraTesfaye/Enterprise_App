package com.example.enterprisapp.enterprise.Model.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enterprisapp.R;
import com.example.enterprisapp.enterprise.Model.Enterprise;

import java.util.ArrayList;

public class EnterpriseRVAdapter extends RecyclerView.Adapter<EnterpriseRVAdapter.ViewHolder> {
	// creating variables for our ArrayList and context
	private ArrayList<Enterprise> EnterpriseArrayList;
	SharedPreferences sharedPreferences;

	private Context context;
	int id;

	// creating constructor for our adapter class
	public EnterpriseRVAdapter(ArrayList<Enterprise> EnterpriseArrayList, Context context, int id) {
		this.EnterpriseArrayList = EnterpriseArrayList;
		this.context = context;
		sharedPreferences = context.getSharedPreferences("sp",MODE_PRIVATE);
		this.id = id;

	}

	@NonNull
	@Override
	public EnterpriseRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		// passing our layout file for displaying our card item
		return new ViewHolder(LayoutInflater.from(context).inflate(id, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull EnterpriseRVAdapter.ViewHolder holder, int position) {
		Enterprise Enterprise = EnterpriseArrayList.get(position);

		if(id == R.layout.pay_ent_list){
			holder.name.setText(Enterprise.getName());
			holder.expected.setText(Enterprise.getNo_of_total_emp());
			holder.registered.setText(Enterprise.getNo_of_reg_emp());
			holder.status.setText(Enterprise.getStatus());
			holder.counter.setText(Enterprise.getFrequency());
			holder.assigned.setText(Enterprise.getPM());

		} else if (id == R.layout.to_pay_ent_layout) {
			holder.name.setText(Enterprise.getName());
			holder.expected.setText(Enterprise.getNo_of_total_emp());
			holder.registered.setText(Enterprise.getNo_of_reg_emp());

			String[] arr_date = Enterprise.getDate().toDate().toString().split(" ");
			String str_date = arr_date[0]+" "+arr_date[1]+" "+arr_date[2]+" "+arr_date[5];

			holder.date.setText(str_date);
			holder.counter.setText(Enterprise.getFrequency());
			holder.assigned.setText(Enterprise.getPM());

		} else if (id == R.layout.under_communication_ent_layout) {
			holder.name.setText(Enterprise.getName());
			holder.expected.setText(Enterprise.getNo_of_total_emp());
			holder.status.setText(Enterprise.getStatus());
			holder.counter.setText(Enterprise.getFrequency());
			holder.assigned.setText(Enterprise.getPM());



			if(sharedPreferences.getInt("role",0) == 1){


			}else if(sharedPreferences.getInt("role",0) == 1) {

			}
		}

	}

	@Override
	public int getItemCount() {
		// returning the size of our array list.
		return EnterpriseArrayList.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		private final TextView name, expected, registered, last_interaction,counter, status,assigned, date;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			// initializing our text views.
			name = itemView.findViewById(R.id.emp_name);
			expected = itemView.findViewById(R.id.reason1);
			registered = itemView.findViewById(R.id.time_needed);
			last_interaction = itemView.findViewById(R.id.req_time);
			counter = itemView.findViewById(R.id.status);
			status = itemView.findViewById(R.id.remark);
			assigned = itemView.findViewById(R.id.rf);
			date = itemView.findViewById(R.id.dueDate);
		}
	}
}
