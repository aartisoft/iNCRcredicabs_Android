package com.ncr.interns.codecatchers.incredicabs;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ncr.interns.codecatchers.incredicabs.Adapter.MyRequestAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyRequests extends Fragment {

    RecyclerView recyclerView;
    MyRequestAdapter myRequestAdapter;
    View rootView;
    Context context;
    public MyRequests() {
        // Required empty public constructor
        context = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_my_requests, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView_myRequests);
        myRequestAdapter = new MyRequestAdapter();
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(myRequestAdapter);

        return rootView;
    }

}
