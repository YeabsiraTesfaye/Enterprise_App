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
import com.example.enterprisapp.car.Model.Request;
import com.example.enterprisapp.car.adapter.RequestRVAdapter;
import com.example.enterprisapp.databinding.FragmentHomeAdminBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class homeFragmentAdmin extends Fragment {
    DocumentSnapshot lastVisible;
    int position = 0;
    private RecyclerView recyclerView;
    private ArrayList<Request> requestsArrayList;
    private RequestRVAdapter requestRVAdapter;
    private FragmentHomeAdminBinding binding;
    FirebaseFirestore firestore;
    int previousSize = 0;

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
        previousSize = 0;

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
            firestore.collection("requests")
                    .orderBy("requestTime", Query.Direction.DESCENDING)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.size() != previousSize){
                        previousSize = queryDocumentSnapshots.size();
                        Query firstQuery = firestore.collection("requests")
                                .orderBy("requestTime", Query.Direction.DESCENDING).limit(10);
                        firstQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot documentSnapshots) {
                                if(position < documentSnapshots.size()){
                                    position += documentSnapshots.size();
                                }
                                lastVisible = documentSnapshots.getDocuments()
                                        .get(documentSnapshots.size() -1);
                                requestsArrayList.removeAll(requestsArrayList);
                                recyclerView.removeAllViews();
                                if (!documentSnapshots.isEmpty()) {
                                    List<DocumentSnapshot> list = documentSnapshots.getDocuments();
                                    for (DocumentSnapshot d : list) {
                                        Request request = d.toObject(Request.class);
                                        requestsArrayList.add(request);
                                    }
                                    requestRVAdapter.notifyDataSetChanged();
                                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                        @Override
                                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                            super.onScrollStateChanged(recyclerView, newState);
                                            if (!recyclerView.canScrollVertically(1)) {
                                                if(position < queryDocumentSnapshots.size()){
                                                    Toast.makeText(getContext(), position+" "+queryDocumentSnapshots.size(), Toast.LENGTH_LONG).show();
                                                    Query next = firestore.collection("requests")
                                                            .orderBy("requestTime", Query.Direction.DESCENDING)
                                                            .startAfter(lastVisible)
                                                            .limit(10);
                                                    next.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots1) {
                                                            if(queryDocumentSnapshots1.size() > 1){
                                                                position += queryDocumentSnapshots1.size();
                                                                lastVisible = queryDocumentSnapshots1.getDocuments()
                                                                        .get(queryDocumentSnapshots1.size() -1);
                                                                if (!queryDocumentSnapshots1.isEmpty()) {
                                                                    List<DocumentSnapshot> list1 = queryDocumentSnapshots1.getDocuments();
                                                                    for (DocumentSnapshot d : list1) {
                                                                        Request request1 = d.toObject(Request.class);
                                                                        requestsArrayList.add(request1);
                                                                    }
                                                                    requestRVAdapter.notifyDataSetChanged();
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });
                                } else {
//                                    Toast.makeText(getContext(), "You don't have requests yet.", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });
                    }


                }
            });

        }catch (Exception e){

        }
    }

}