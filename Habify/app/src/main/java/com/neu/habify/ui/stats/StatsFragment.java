package com.neu.habify.ui.stats;

import androidx.lifecycle.Observer;
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
import android.widget.TextView;

import com.neu.habify.R;
import com.neu.habify.data.entity.Badge;
import com.neu.habify.data.entity.Habit;
import com.neu.habify.data.entity.Record;
import com.neu.habify.data.scrolling.ComplexRecyclerViewAdapter;

import java.util.LinkedList;
import java.util.List;

public class StatsFragment extends Fragment {

    private StatsViewModel statsViewModel;
    private RecyclerView recyclerView;
    private ComplexRecyclerViewAdapter adapter;
    private TextView activeHabit;
    private TextView allEarnedBadges;
    private TextView allCompletedTasks;
    private TextView allPerfectDays;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statsViewModel =
                ViewModelProviders.of(this).get(StatsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_stats, container, false);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // listener setup()
        // observer setup() to get data from database
        recyclerSetup();
        setStats();
    }

    private void recyclerSetup() {
        this.recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_stats);
        this.adapter = new ComplexRecyclerViewAdapter();

        // TODO Set list through a database query
        this.adapter.setItems(new LinkedList<Object>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // This adds the horizontal gray lines between the list items
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    /**
     * Call code to set all stats items from here
     */
    private void setStats() {
        setActiveHabits();
        setTotalBadges();
        setCompletedTasks();
        setAllPerfectDays();
    }

    private void setActiveHabits() {
        activeHabit = getView().findViewById(R.id.totalActiveTask);
        this.statsViewModel.getAllActiveHabits().observe(getViewLifecycleOwner(), new Observer<List<Habit>>() {
            @Override
            public void onChanged(List<Habit> habits) {
                if (habits != null) {
                    String number = String.valueOf(habits.size());
                    activeHabit.setText(number);
                }
            }
        });
    }

    private void setTotalBadges() {
        allEarnedBadges = getView().findViewById(R.id.totalBadge);
        this.statsViewModel.getAllEarnedBadges().observe(getViewLifecycleOwner(), new Observer<List<Badge>>() {
            @Override
            public void onChanged(List<Badge> badges) {
                if (badges != null) {
                    String number = String.valueOf(badges.size());
                    allEarnedBadges.setText(number);
                }
            }
        });
    }

    private void setCompletedTasks() {
        allCompletedTasks = getView().findViewById(R.id.totalHabit);
        this.statsViewModel.getAllCompletedTasks().observe(getViewLifecycleOwner(), new Observer<List<Record>>() {
            @Override
            public void onChanged(List<Record> records) {
                if (records != null) {
                    String number = String.valueOf(records.size());
                    allCompletedTasks.setText(number);
                }
            }
        });
    }

    private void setAllPerfectDays() {
        allPerfectDays = getView().findViewById(R.id.totalPerfectDay);
        this.statsViewModel.getAllPerfectDays().observe(getViewLifecycleOwner(), new Observer<List<Record>>() {
            @Override
            public void onChanged(List<Record> records) {
                if (records != null) {
                    String number = String.valueOf(records.size());
                    allPerfectDays.setText(number);
                }
            }
        });
    }

}
