package com.neu.habify.ui.manage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.neu.habify.R;
import com.neu.habify.data.entity.Habit;
import com.neu.habify.data.scrolling.ComplexRecyclerViewAdapter;
import com.neu.habify.data.scrolling.ListMessage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ManageFragment extends Fragment {

    private ManageViewModel manageViewModel;
    private RecyclerView recyclerView;
    private ComplexRecyclerViewAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        manageViewModel =
                ViewModelProviders.of(this).get(ManageViewModel.class);
        View root = inflater.inflate(R.layout.fragment_manage, container, false);
        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.manage_top_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_preferences) {
            Navigation.findNavController(getView()).navigate(R.id.nav_manage_to_preferences);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        listenerSetup();
        this.manageViewModel.receiveCallback(this);
        recyclerSetup();
    }

    public void notifyHabitsListReady() {
        observerSetup();
    }

    private void observerSetup() {
        manageViewModel.getManageHabits().observe(getViewLifecycleOwner(), new Observer<List<Habit>>() {
            @Override
            public void onChanged(List<Habit> listItems) {
                setListItems(listItems);
            }
        });
    }

    private void setListItems(List<Habit> observed) {
        // Will pass a List<Object> to the complex recycler view adapter
        List<Object> manageList = new ArrayList<>();
        // Query the habits list, include a message if there are no habits for today
        if (observed.size() > 0) {
            // Insert headers for active and archived sections of the list, already ordered from SQL query
            manageList.add(new ListMessage("Active Habits"));
            boolean hasArchiveHeader = false;
            for (int i = 0; i < observed.size(); i++) {
                Habit habit = observed.get(i);
                if (!habit.isActive() && !hasArchiveHeader) {
                    hasArchiveHeader = true;
                    manageList.add(new ListMessage("Archived Habits"));
                }
                manageList.add(habit);
            }
            if (!hasArchiveHeader) {
                hasArchiveHeader = true;
                manageList.add(new ListMessage("Archived Habits"));
            }
        }
        // Otherwise display message saying there are no habits to show yet
        // This will appear for first time users before they create a habit
        else {
            manageList.add(new ListMessage(getString(R.string.no_habits_yet)));
        }
        adapter.setItems(manageList);
    }

    private void listenerSetup() {
        FloatingActionButton addHabitFab = getView().findViewById(R.id.fab_add_habit);
        addHabitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_manage_to_habitsetup);
            }
        });
    }

    private void recyclerSetup() {
        this.recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_manage);
        this.adapter = new ComplexRecyclerViewAdapter();
        this.adapter.setItems(new LinkedList<Object>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // This adds the horizontal gray lines between the list items
    }
}