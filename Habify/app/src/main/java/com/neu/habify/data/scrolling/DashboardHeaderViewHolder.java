package com.neu.habify.data.scrolling;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.neu.habify.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which controls the setup for item_dashboard_header in a recycler view.
 */
public class DashboardHeaderViewHolder extends RecyclerView.ViewHolder {

    private TextView headerMessage;
    private PieChart dailyProgressChart;

    public DashboardHeaderViewHolder(View v) {
        super(v);
        headerMessage = v.findViewById(R.id.header_text);
        dailyProgressChart = v.findViewById(R.id.chart_daily_progress);
    }

    public void setHeaderMessage(String msg) {
        headerMessage.setText(msg);
    }

    public void setDailyProgressChart(int completed, int remaining) {
        // Creating a list of values with labels
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(completed, "Completed"));
        entries.add(new PieEntry(remaining, "Remaining"));
        // Creating a meta object with entries, title, bar color scheme
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(Color.GREEN, Color.GRAY);

        dataSet.setDrawValues(false);
        PieData chartData = new PieData(dataSet);
        // Assigning the data to the chart & invalidate makes it show up?
        dailyProgressChart.getDescription().setEnabled(false);
        dailyProgressChart.setDrawEntryLabels(false);
        dailyProgressChart.setData(chartData);
        dailyProgressChart.invalidate();
    }
}
