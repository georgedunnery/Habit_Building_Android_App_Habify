package com.neu.habify.data.scrolling;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.neu.habify.R;
import com.neu.habify.data.entity.DetailedHabitData;

public class DetailedHabitDataViewHolder extends RecyclerView.ViewHolder {

    private static final String DETDASH_HABIT_VH = "DetailedHabitVH";
    private View view;
    private ImageView statusImage;
    public static final int STATUS_ICON_SUCCESS = 0;
    public static final int STATUS_ICON_PENDING = 1;
    public static final int STATUS_ICON_FAILED = 2;
    private TextView habitName;
    private TextView locationName;
    private TextView locationAddress;
    private TextView times;
    private ProgressBar consistencyBar;

    public DetailedHabitDataViewHolder(View v) {
        super(v);
        this.view = v;
        this.statusImage = v.findViewById(R.id.det_dash_image);
        this.habitName = v.findViewById(R.id.text_detdash_habit_name);
        this.locationName = v.findViewById(R.id.text_detdash_location_name);
        this.locationAddress = v.findViewById(R.id.text_detdash_location_address);
        this.times = v.findViewById(R.id.text_detdash_times);
        this.consistencyBar = v.findViewById(R.id.progbar_detdash);
    }

    // TODO handle setting the status image
    public void setStatusImage(int statusIcon) {
        switch(statusIcon) {
            case STATUS_ICON_SUCCESS:
                this.statusImage.setImageResource(R.drawable.star);
                break;
            case STATUS_ICON_PENDING:
                this.statusImage.setImageResource(R.drawable.pending);
                break;
            case STATUS_ICON_FAILED:
                this.statusImage.setImageResource(R.drawable.failed);
                break;
            default:
                throw new IllegalArgumentException("Unsupported image status icon requested [ComplexRecyclerViewAdapter -> invalid static int -> DetailedHabitViewHolder]");
        }
    }

    public void setHabitName(String name) { this.habitName.setText(name); }
    public void setLocationName(String name) { this.locationName.setText(name); }
    public void setLocationAddress(String address) { this.locationAddress.setText(address); }
    public void setTimes(String start, String end) {
        String timeRange = start + " - " + end;
        this.times.setText(timeRange);
    }

    /**
     * If continuous, please use completed = successes while setting total as successes + failures
     * This method expects that completed <= total, and both are non negative
     *
     * @param completed
     * @param total
     */
    public void setConsistencyBar(Integer completed, Integer total) {
        Integer max = this.consistencyBar.getMax();
        // Avoiding a divide by zero exception
        Log.i(DETDASH_HABIT_VH, "completed = " + completed + ", total = " + total);
        if (completed > 0 && total == 0) {
            this.consistencyBar.setProgress(max);
        }
        else if (completed == 0 && total == 0) {
            this.consistencyBar.setProgress(0);
        }
        // Calculate a decimal, multiply up into the integer range of the progress bar, cast to int
        // This is necessary because the progress bar expects an integer for .setProgress()
        else {
            Double progress = completed.doubleValue() / total.doubleValue();
            progress = progress * max;
            this.consistencyBar.setProgress(progress.intValue());
        }
    }





    /**
     * This then calls to the adapter.setOnItemClickListener in the recyclerSetup() method of the
     * appropriate fragment (in this case, dashboard fragment).
     * @param listener
     */
    public void receiveClickListener(final RecyclerItemClickListener listener) {
        this.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(DETDASH_HABIT_VH, "on click registered");
                if (listener == null) {
                    Log.i(DETDASH_HABIT_VH, "WARNING: listener reference is null, no action taken");
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
