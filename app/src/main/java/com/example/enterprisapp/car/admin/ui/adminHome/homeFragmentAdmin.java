package com.example.enterprisapp.car.admin.ui.adminHome;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enterprisapp.R;
import com.example.enterprisapp.car.Request;
import com.example.enterprisapp.car.adapter.RequestRVAdapter;
import com.example.enterprisapp.databinding.FragmentHomeAdminBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class homeFragmentAdmin extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<Request> requestsArrayList;
    private RequestRVAdapter requestRVAdapter;
    private FragmentHomeAdminBinding binding;
    FirebaseFirestore firestore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeAdminBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        firestore = FirebaseFirestore.getInstance();
        recyclerView = root.findViewById(R.id.idRVRequests);

        requestsArrayList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // adding our array list to our recycler view adapter class.
        requestRVAdapter = new RequestRVAdapter(requestsArrayList, getContext());

        // setting adapter to our recycler view.
        recyclerView.setAdapter(requestRVAdapter);

        getRequests();
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getRequests();

                handler.postDelayed(this,5000);
            }
        },1000);



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void getRequests(){
        try {
            firestore.collection("requests").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            requestsArrayList.removeAll(requestsArrayList);
                            recyclerView.removeAllViews();
                            if (!queryDocumentSnapshots.isEmpty()) {
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot d : list) {
                                    Request request = d.toObject(Request.class);
                                    requestsArrayList.add(request);
                                }
                                requestRVAdapter.notifyDataSetChanged();
                            } else {
//                                    Toast.makeText(getContext(), "You don't have requests yet.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception e){

        }

    }

}