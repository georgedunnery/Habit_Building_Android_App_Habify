package com.neu.habify.ui.dashboard;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.neu.habify.data.entity.DetailedHabitData;
import com.neu.habify.data.entity.Habit;
import com.neu.habify.data.scrolling.ComplexRecyclerViewAdapter;
import com.neu.habify.R;
import com.neu.habify.data.scrolling.DashboardHeader;
import com.neu.habify.data.scrolling.ListMessage;
import com.neu.habify.data.scrolling.RecyclerItemClickListener;
import com.neu.habify.ui.notification.HabitNoticeReceiver;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static android.content.Context.ALARM_SERVICE;

public class DashboardFragment extends Fragment {

    private static final String DASH_FRAG_TAG = "DashboardFragment";
    private final int LOCATION_PERMISSION = 1;
    private boolean hasLocationPermission = false;
    private boolean requestingLocationUpdates = false;
    private int locationCalls = 0;

    private LocationCallback locationCallback;
    private FusedLocationProviderClient locationClient;
    private android.location.Location mostRecentLocation;
    private LocationRequest locationRequest;

    private DashboardViewModel dashboardViewModel;
    private RecyclerView recyclerView;
    private ComplexRecyclerViewAdapter adapter;
    private Habit confirmingHabit;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        return root;
    }



    // Moved the request for permissions out of the onCreate, trigger it when pressing an item


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Set the client, request, and callback
        locationClient = LocationServices.getFusedLocationProviderClient(this.getContext());
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // Once per second, and we should stop it after a couple updates
        locationRequest.setInterval(1000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    // Keep overwriting with the most recent location from the request
                    // getLastLocation == last element in locationResult.getLocations()
                    // Unnecessary to iterate through all Locations, only want most recent entry
                    mostRecentLocation = locationResult.getLastLocation();
                    Log.i(DASH_FRAG_TAG, "onLocationResult valid");
                    foundLocation();
                }
                else {
                    Log.i(DASH_FRAG_TAG, "onLocationResult was null");
                }
            }
        };
    }

    private void checkPermissionStatus() {
        // Check for permissions and ask for GPS to support gps verification feature
        if (!hasLocationPermission) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {
                Log.i(DASH_FRAG_TAG, "Permissions requested from onCreate");
                setupPermissions();
            } else {
                hasLocationPermission = true;
            }
        }
    }

    private void setupPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasLocationPermission = true;
                Log.i(DASH_FRAG_TAG, "onRequestPermissionsResult - Location permission granted");
                Toast.makeText(getContext(),
                        "Location Permission Granted", Toast.LENGTH_LONG).show();
            }
            else {
                hasLocationPermission = false;
                Log.i(DASH_FRAG_TAG, "onRequestPermissionsResult - Location permission denied");
                Toast.makeText(getContext(),
                        "Location Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Provide a callback so we can wait to call observerSetup() until repo is ready, avoiding a null pointer exception
        Log.i(DASH_FRAG_TAG, "on view created");
        this.dashboardViewModel.receiveCallback(this);
        recyclerSetup();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dashboard_top_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_calendar:
                Navigation.findNavController(getView()).navigate(R.id.nav_dashboard_to_calendar);
                return true;
            case R.id.action_statistics:
                Navigation.findNavController(getView()).navigate(R.id.nav_dashboard_to_stats);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void notifyHabitsListIsReady() {
        Log.i(DASH_FRAG_TAG, "observerSetup");
        observerSetup();
    }

    private void observerSetup() {
        dashboardViewModel.getHabits().observe(getViewLifecycleOwner(), new Observer<List<DetailedHabitData>>() {
            @Override
            public void onChanged(List<DetailedHabitData> listItems) {
                Log.i(DASH_FRAG_TAG, "observed items = " + listItems.size());
                setListItems(listItems);
            }
        });
    }

    /**
     * This sets up the recycler.
     */
    private void recyclerSetup() {
        this.recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_dashboard);
        this.adapter = new ComplexRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Log.i(DASH_FRAG_TAG, "recyclerSetup");

        // This adapter listener is called by the habit view holder onItemClick method,
        // which is choreographed by the recyclerItemClickListener in the ComplexRecyclerViewAdapter
        adapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Object object = adapter.getItemAt(position);
                // Clicking on anything other than a habit list item will have no effect
                if (object instanceof Habit) {
                    Habit habit = (Habit) object;
                    Log.i(DASH_FRAG_TAG, "Clicked on the list item " + habit.getName() + " at position=" + position);
                    recordHabitDialog(habit);
                }
                if (object instanceof DetailedHabitData) {
                    DetailedHabitData detHab = (DetailedHabitData) object;
                    Log.i(DASH_FRAG_TAG, "Clicked on the list item " + detHab.getHabitName() + " at position=" + position);
                    // Only allow recording if the habit is not completed for today
                    if (detHab.getCompleted() == 0) {
                        Habit habit = new Habit(1, detHab.getHabitName(), detHab.getLocationId(), detHab.getGps(), detHab.getCreated(), detHab.getStartTime(), detHab.getEndTime(), detHab.getBound());
                        habit.setId(detHab.getHabitId());
                        recordHabitDialog(habit);
                    }
                }
            }
        });
    }

    private void recordHabitDialog(final Habit habit) {
        this.confirmingHabit = habit;
        String msg = "Have you completed " + habit.getName() + " for today?";
        if (habit.getGps()) {
            msg += " GPS verification is enabled for this habit. Attempting to record completion" +
                    " from a different location will result in the habit being recorded as incomplete.";
        }
        else {
            msg += " GPS verification is not enabled for this habit, so we'll have to take your word for it!";
        }
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Record Habit Completion")
                .setMessage(msg)
                .setPositiveButton("Confirm Habit Completion", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                // If they cancel, we can simply close the dialog without taking action
                Button cancelButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(DASH_FRAG_TAG, "dialog canceled");
                        dialog.dismiss();
                    }
                });

                // If they attempt to confirm, we have to verify gps and insert a record into the database
                final Button confirmButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(DASH_FRAG_TAG, "confirm habit button pressed for " + habit.getName() + " , id=" + habit.getId());
                        // Save a reference to the habit so it can be used outside this listener
                        confirmingHabit = habit;
                        initiateHabitConfirmation();
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }


    private void initiateHabitConfirmation() {
        // If we need the location but permissions are not yet granted, ask for location and cancel
        // out of habit recording process for now
        if (this.confirmingHabit.getGps() && !hasLocationPermission) {
            Toast.makeText(getContext(), "Please enable GPS and try again", Toast.LENGTH_LONG).show();
            checkPermissionStatus();
        }
        // If we need location and can get it, start requesting location
        else if (this.confirmingHabit.getGps() && hasLocationPermission) {
            Toast.makeText(getContext(), "Please wait while we verify your location...", Toast.LENGTH_LONG).show();
            Log.i(DASH_FRAG_TAG, "startLocationUpdates");
            startLocationUpdates();

        }
        // If GPS is not enabled, just go ahead and insert the record
        else {
            insertRecord();
        }
    }

    private void startLocationUpdates() {
        requestingLocationUpdates = true;
        locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        locationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.i(DASH_FRAG_TAG, "user location found: lat=" + location.getLatitude() + ", lon=" + location.getLongitude());
                            mostRecentLocation = location;
                            foundLocation();
                        } else {
                            Log.i(DASH_FRAG_TAG, "user location was null");
                        }
                    }
                });
    }

    /**
     * The first call to getLastLocation may be stale, so we want to use at least the second location
     * returned from the onSuccessListener. Updates run once per second, and we will accumulate the
     * number of successes here. Then, the second update location will be used, and the location
     * requests will be disengaged to make best use of user battery.
     *
     * The verification process should take about 2 seconds.
     */
    private void foundLocation() {
        // Only go on the second update, not the first one, since the data will probably be stale
        this.locationCalls++;
        if (this.locationCalls >= 3) {
            this.locationCalls = 0;
            stopLocationUpdates();
            Log.i(DASH_FRAG_TAG, "using fresh location update: lat=" + mostRecentLocation.getLatitude() +
                    ", lon=" + mostRecentLocation.getLongitude());
            insertRecordWithGpsVerification();
        }
    }

    /**
     * Insert a habit record, comparing current location to expected location based on coordinates
     */
    private void insertRecordWithGpsVerification() {
        Log.i(DASH_FRAG_TAG, "inserting record with gps verification");
        this.dashboardViewModel.setConfirmingHabit(this.confirmingHabit);
        this.dashboardViewModel.recordHabitWithGpsVerification(this.mostRecentLocation);
    }

    /**
     * Insert a habit record without checking gps.
     */
    private void insertRecord() {
        Log.i(DASH_FRAG_TAG, "inserting record");
        this.dashboardViewModel.setConfirmingHabit(this.confirmingHabit);
        this.dashboardViewModel.recordHabitCompletion(true);
    }

    public void reportHabitCompletionStatus(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }


    /**
     * Responsible for casting to a List of Objects to work in the ComplexAdapter.
     * @param observed
     */
    private void setListItems(List<DetailedHabitData> observed) {
        // Will pass a List<Object> to the complex recycler view adapter
        List<Object> dashList = new ArrayList<>();
        if (observed.size() > 0) {
            // Split the list by completed vs remaining
            List<DetailedHabitData> remainingTasks = new ArrayList<>();
            List<DetailedHabitData> completedTasks = new ArrayList<>();
            for (DetailedHabitData dhd : observed) {
                if (dhd.getCompleted() == 0) {
                    remainingTasks.add(dhd);
                }
                else {
                    completedTasks.add(dhd);
                }
            }
            // Set the dashboard header using those counts to set the chart pieces
            dashList.add(new DashboardHeader(getString(R.string.daily_progress), completedTasks.size(), remainingTasks.size()));
            // Add a header for the remaining tasks, and then all of the remaining tasks for today
            dashList.add(new ListMessage("Today's Remaining Habits"));
            dashList.addAll(remainingTasks);
            // Add a header for the completed tasks, then all of the completed tasks for today
            dashList.add(new ListMessage("Today's Completed Habits"));
            dashList.addAll(completedTasks);

            // Send notification for observed tasks that are not yet completed (remainingTasks)
            sendNotification(remainingTasks);

        }
        // Display message saying there are no habits for today
        else {
            dashList.add(new ListMessage(getString(R.string.no_habits_for_today)));
        }
        adapter.setItems(dashList);
        Log.i(DASH_FRAG_TAG, "Set List Items");
    }

    // Shuhong - I updated Habit -> DetailedHabitData and habit.getName is now habit.getHabitName
    // I think this should still be fine
    private void sendNotification(List<DetailedHabitData> observed) {
        int hour;
        int minute;
        final String GROUP_KEY_NOTIFY = "group_key_notify";
        int count = 0;

        // Query the habits list, include a message if there are no habits for today
        if (observed.size() > 0) {

            for (DetailedHabitData habit : observed) {
                String startTime = habit.getStartTime();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

                LocalTime time = LocalTime.parse(startTime.substring(0, 5), formatter);
                hour = time.getHour();
                minute = time.getMinute();
                Calendar calendar = Calendar.getInstance();
                if (startTime.charAt(startTime.length() - 2) == 'P') {
                    hour = hour + 12;

                } else if (minute < 30) {
                    minute = minute + 30;
                    hour = hour - 1;
                }
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);

                Intent intent = new Intent(getActivity(), HabitNoticeReceiver.class);
                intent.putExtra("startTime", habit.getStartTime());
                intent.putExtra("title", habit.getHabitName());
                intent.putExtra("id", count + 1);
                intent.putExtra("group", GROUP_KEY_NOTIFY);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),
                        count,
                        intent, PendingIntent.FLAG_ONE_SHOT);
                count += 1;
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Turn off location updates when the app is terminated
        if (requestingLocationUpdates) {
            stopLocationUpdates();
        }
    }

    private void stopLocationUpdates() {
        locationClient.removeLocationUpdates(locationCallback);
        Log.i(DASH_FRAG_TAG, "stopLocationUpdates");
    }
}