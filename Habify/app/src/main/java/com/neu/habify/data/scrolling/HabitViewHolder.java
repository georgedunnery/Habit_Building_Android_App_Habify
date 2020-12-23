package com.neu.habify.data.scrolling;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.neu.habify.R;

/**
 * Class which controls the setup for item_dashboard_habit in a recycler view.
 */
public class HabitViewHolder extends RecyclerView.ViewHolder {

    private static final String DASH_HABIT_VH = "DashHabitViewHolder";
    private View view;
    private Integer habitId;
    private TextView habitName;
    private TextView habitTime;
    private TextView habitLocation;
    private TextView habitConsistency;

    public HabitViewHolder(View v) {
        super(v);
        view = v;
        habitName = v.findViewById(R.id.text_habit_name);
        habitTime = v.findViewById(R.id.text_habit_time);
        habitLocation = v.findViewById(R.id.text_habit_location);
        habitConsistency = v.findViewById(R.id.text_habit_consistency);
    }

    public void setHabitId(Integer id) {habitId = id;}
    public void setHabitName(String name) {habitName.setText(name);}
    public void setHabitTime(String time) {habitTime.setText(time);}
    public void setHabitLocation(String location) {habitLocation.setText(location);}
    public void setHabitConsistency(String consistency) {habitConsistency.setText(consistency);}

    /**
     * This then calls to the adapter.setOnItemClickListener in the recyclerSetup() method of the
     * appropriate fragment (in this case, dashboard fragment).
     * @param listener
     */
    public void receiveClickListener(final RecyclerItemClickListener listener) {
        this.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(DASH_HABIT_VH, "on click registered");
                if (listener == null) {
                    Log.i(DASH_HABIT_VH, "WARNING: listener reference is null, no action taken");
                }
                else {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }

                }
            }
        });
    }
}
