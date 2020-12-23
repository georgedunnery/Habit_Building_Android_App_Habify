package com.neu.habify.ui.preferences;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.neu.habify.R;
import com.neu.habify.data.scrolling.ComplexRecyclerViewAdapter;

import java.util.LinkedList;

public class PreferencesFragment extends Fragment {

    private PreferencesViewModel preferencesViewModel;
    private RecyclerView recyclerView;
    private ComplexRecyclerViewAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        preferencesViewModel =
                ViewModelProviders.of(this).get(PreferencesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_preferences, container, false);
//        final TextView textView = root.findViewById(R.id.text_preferences);
//        preferencesViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.preferences_top_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_preferences) {
            Toast.makeText(getContext(), "Preferences Saved", Toast.LENGTH_LONG).show();
            // TODO save user preferences
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // listener setup()
        // observer setup() to get data from database
        recyclerSetup();
    }

    private void recyclerSetup() {
        this.recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_preferences);
        this.adapter = new ComplexRecyclerViewAdapter();

        // TODO Set list through a database query
        this.adapter.setItems(new LinkedList<Object>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // This adds the horizontal gray lines between the list items
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }
}
