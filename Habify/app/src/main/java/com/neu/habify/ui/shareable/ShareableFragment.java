package com.neu.habify.ui.shareable;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neu.habify.R;
import com.neu.habify.data.scrolling.ComplexRecyclerViewAdapter;

import java.util.LinkedList;

public class ShareableFragment extends Fragment {

    private ShareableViewModel shareableViewModel;
    private RecyclerView recyclerView;
    private ComplexRecyclerViewAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareableViewModel =
                ViewModelProviders.of(this).get(ShareableViewModel.class);
        View root = inflater.inflate(R.layout.fragment_shareable, container, false);
//        final TextView textView = root.findViewById(R.id.text_shareable);
//        shareableViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // listener setup()
        // observer setup() to get data from database
        recyclerSetup();
    }

    private void recyclerSetup() {
        this.recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_shareable);
        this.adapter = new ComplexRecyclerViewAdapter();

        // TODO Set list through a database query
        this.adapter.setItems(new LinkedList<Object>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // This adds the horizontal gray lines between the list items
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

}
