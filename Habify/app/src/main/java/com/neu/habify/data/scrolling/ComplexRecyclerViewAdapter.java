package com.neu.habify.data.scrolling;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.neu.habify.R;
import com.neu.habify.data.entity.Badge;
import com.neu.habify.data.entity.DetailedHabitData;
import com.neu.habify.data.entity.Habit;

import java.util.List;

/**
 * Class which supports adding multiple types of list items to the same recycler view. Most
 * fragments will use this adapter for the recycler to produce the typical scrolling interaction
 * users expect from modern apps.
 *
 * To add a new list item:
 * 1. Create a class to be the model, i.e. Habit
 * 2. Create a layout for the list iten, i.e. item_dashboard_habit
 * 3. Create a ViewHolder class, i.e. HabitViewHolder
 * 4. Update this class:
 *    - Add a private final int TYPE
 *    - Add additional else if statement to getItemViewType
 *    - Add additional case to onCreateViewHolder
 *    - Add additional case to onBindViewHolder
 *    - Add a method to configure the new type of view holder
 * 5. Add the RecyclerView to the fragment layout, i.e. dashboard_fragment.xml
 * 6. Add private recyclerView and adapter to the corresponding fragment, i.e. DashboardFragment
 * 7. Override onViewCreated(), and call a recyclerSetup() function from within it
 */
public class ComplexRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String ADAPTER = "Adapter";
    private List<Object> items;
    private RecyclerItemClickListener clickListener;
    private final int TYPE_DASH_HEADER = 0;
    private final int TYPE_HABIT = 1;
    private final int TYPE_LIST_MSG = 2;
    private final int TYPE_BADGE = 3;
    private final int TYPE_DETAILED_HABIT = 4;

    public ComplexRecyclerViewAdapter() {}

    public void setItems(List<Object> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public Object getItemAt(int index) {
        return this.items.get(index);
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return this.items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Habit) {
            return TYPE_HABIT;
        }
        else if (items.get(position) instanceof DashboardHeader) {
            return TYPE_DASH_HEADER;
        }
        else if (items.get(position) instanceof ListMessage) {
            return TYPE_LIST_MSG;
        }
        else if (items.get(position) instanceof Badge) {
            return TYPE_BADGE;
        }
        else if (items.get(position) instanceof DetailedHabitData) {
            return TYPE_DETAILED_HABIT;
        }
        else {
            throw new IllegalArgumentException("Unsupported view type from getItemViewType.");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch(viewType) {
            case TYPE_HABIT:
                View habitView = inflater.inflate(R.layout.item_dashboard_habit, viewGroup, false);
                viewHolder = new HabitViewHolder(habitView);
                break;
            case TYPE_DASH_HEADER:
                View dashHeaderView = inflater.inflate(R.layout.item_dashboard_header, viewGroup, false);
                viewHolder = new DashboardHeaderViewHolder(dashHeaderView);
                break;
            case TYPE_LIST_MSG:
                View listMessageView = inflater.inflate(R.layout.item_list_message, viewGroup, false);
                viewHolder = new ListMessageViewHolder(listMessageView);
                break;
            case TYPE_BADGE:
                View badgeView = inflater.inflate(R.layout.item_profile_badge, viewGroup, false);
                viewHolder = new BadgeViewHolder(badgeView);
                break;
            case TYPE_DETAILED_HABIT:
                View detailedHabitView = inflater.inflate(R.layout.item_detailed_dash_habit, viewGroup, false);
                viewHolder = new DetailedHabitDataViewHolder(detailedHabitView);
                break;
            default:
                throw new IllegalArgumentException("Unsupported view type from onCreateViewHolder.");
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch(viewHolder.getItemViewType()) {
            case TYPE_HABIT:
                HabitViewHolder habitViewHolder = (HabitViewHolder) viewHolder;
                configureHabitViewHolder(habitViewHolder, position);
                break;
            case TYPE_DASH_HEADER:
                DashboardHeaderViewHolder dashHeaderViewHolder = (DashboardHeaderViewHolder) viewHolder;
                configureDashboardHeaderViewHolder(dashHeaderViewHolder, position);
                break;
            case TYPE_LIST_MSG:
                ListMessageViewHolder listMessageViewHolder = (ListMessageViewHolder) viewHolder;
                configureListMessageViewHolder(listMessageViewHolder, position);
                break;
            case TYPE_BADGE:
                BadgeViewHolder badgeViewHolder = (BadgeViewHolder) viewHolder;
                configureBadgeViewHolder(badgeViewHolder, position);
                break;
            case TYPE_DETAILED_HABIT:
                DetailedHabitDataViewHolder detailedHabitDataViewHolder = (DetailedHabitDataViewHolder) viewHolder;
                configureDetailedHabitDataViewHolder(detailedHabitDataViewHolder, position);
                break;
            default:
                throw new IllegalArgumentException("Unsupported view type from onBindViewHolder.");
        }
    }

    private void configureHabitViewHolder(HabitViewHolder viewHolder, int position) {
        Habit habit = (Habit) items.get(position);
        if (habit != null) {
            // TODO convert manage display to detailed habit data
            viewHolder.receiveClickListener(this.clickListener);
            viewHolder.setHabitId(habit.getId());
            viewHolder.setHabitName(habit.getName().substring(0, 1).toUpperCase() + habit.getName().substring(1));
            viewHolder.setHabitTime(habit.getStartTime());
            // Remove the placeholder zeros, this information needs to already exist here, use detailed habit instead
            viewHolder.setHabitLocation("");
            viewHolder.setHabitConsistency("");
        }
    }

    private void configureDashboardHeaderViewHolder(DashboardHeaderViewHolder viewHolder, int position) {
        DashboardHeader header = (DashboardHeader) items.get(position);
        if (header != null) {
            viewHolder.setHeaderMessage(header.getTestMessage());
            viewHolder.setDailyProgressChart(header.getCompleted(),header.getRemaining());
        }
    }

    private void configureListMessageViewHolder(ListMessageViewHolder viewHolder, int position) {
        ListMessage listMessage = (ListMessage) items.get(position);
        if (listMessage != null) {
            viewHolder.setMessage(listMessage.getMessage());
        }
    }

    private void configureBadgeViewHolder(BadgeViewHolder viewHolder, int position) {
        Badge badge = (Badge) items.get(position);
        if (badge != null) {
            viewHolder.setName(badge.getName());
            viewHolder.setValue(badge.getValue());
            viewHolder.setIcon(badge.getIcon());
        }
    }

    private void configureDetailedHabitDataViewHolder(DetailedHabitDataViewHolder viewHolder, int position) {
        DetailedHabitData detHabit = (DetailedHabitData) items.get(position);
        if (detHabit != null) {
            viewHolder.receiveClickListener(this.clickListener);
            viewHolder.setHabitName(detHabit.getHabitName());
            viewHolder.setLocationName(detHabit.getLocationName());
            viewHolder.setLocationAddress(detHabit.getLocationAddress());
            viewHolder.setTimes(detHabit.getStartTime(), detHabit.getEndTime());

            // Handling implied "continuous" or "bounded" habit styles
            int completed;
            int total;
            if (detHabit.getBound() == null) {
                completed = detHabit.getSuccesses();
                total = detHabit.getSuccesses() + detHabit.getFailures();
            }
            else {
                completed = detHabit.getSuccesses();
                total = detHabit.getBound();
            }

            // Pass the determined metrics into the progress bar to get a visual consistency rating
            viewHolder.setConsistencyBar(completed, total);

            // Set the status icon depending on how the user performed
            // Mark Successful: Habit is done for today, and was evaluated as true
            if (detHabit.getCompleted() == 1 && detHabit.getSucceededToday() == 1) {
                Log.i(ADAPTER, "Habit Status Image - Success. detHabit.completed = " + detHabit.getCompleted() + ", detHabit.succeededtoday = " + detHabit.getSucceededToday());
                viewHolder.setStatusImage(DetailedHabitDataViewHolder.STATUS_ICON_SUCCESS);
            }
            else if (detHabit.getCompleted() == 1 && detHabit.getSucceededToday() == 0) {
                Log.i(ADAPTER, "Habit Status Image - Failed. detHabit.completed = " + detHabit.getCompleted() + ", detHabit.succeededtoday = " + detHabit.getSucceededToday());
                viewHolder.setStatusImage(DetailedHabitDataViewHolder.STATUS_ICON_FAILED);
            }
            else if (detHabit.getCompleted() == 0) {
                Log.i(ADAPTER, "Habit Status Image - Pending. detHabit.completed = " + detHabit.getCompleted() + ", detHabit.succeededtoday = " + detHabit.getSucceededToday());
                viewHolder.setStatusImage(DetailedHabitDataViewHolder.STATUS_ICON_PENDING);
            }
            else {
                throw new IllegalStateException("Habit must either be successful, pending, or failed!");
            }
        }
    }

    public void setOnItemClickListener(RecyclerItemClickListener listener) {
        this.clickListener = listener;
    }
}
