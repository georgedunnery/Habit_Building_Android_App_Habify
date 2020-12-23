package com.neu.habify.ui.habitsetup;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import com.google.android.material.snackbar.Snackbar;
import com.neu.habify.R;
import com.neu.habify.data.entity.Location;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HabitSetupFragment extends Fragment {

    private final String HABIT_SETUP = "HabitSetup";
    private HabitSetupViewModel habitSetupViewModel;
    private EditText habitName;
    private EditText locationName;
    private Switch switchEnableGps;
    private EditText locationAddress;
    private CheckBox checkMonday;
    private CheckBox checkTuesday;
    private CheckBox checkWednesday;
    private CheckBox checkThursday;
    private CheckBox checkFriday;
    private CheckBox checkSaturday;
    private CheckBox checkSunday;
    private Spinner startTime;
    private Spinner endTime;
    private Switch continuous;
    private EditText bound;
    private Button createHabit;
    private LocatorTask mLocatorTask;
    private GeocodeParameters mGeocodeParameters;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        habitSetupViewModel = ViewModelProviders.of(this).get(HabitSetupViewModel.class);
        return inflater.inflate(R.layout.habit_setup_fragment, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        // Find all the views that we will need data from to create the new habit
        this.habitName = v.findViewById(R.id.text_habit_setup_name);
        this.locationName = v.findViewById(R.id.text_habit_setup_location_name);
        this.switchEnableGps = v.findViewById(R.id.switch_enable_gps);
        this.locationAddress = v.findViewById(R.id.text_habit_setup_address);
        this.checkMonday = v.findViewById(R.id.check_mon);
        this.checkTuesday = v.findViewById(R.id.check_tues);
        this.checkWednesday = v.findViewById(R.id.check_wed);
        this.checkThursday = v.findViewById(R.id.check_thurs);
        this.checkFriday = v.findViewById(R.id.check_fri);
        this.checkSaturday = v.findViewById(R.id.check_sat);
        this.checkSunday = v.findViewById(R.id.check_sun);
        this.startTime = v.findViewById(R.id.text_habit_setup_start_time);
        this.endTime = v.findViewById(R.id.text_habit_setup_end_time);
        this.continuous = v.findViewById(R.id.switch_continuous_habit);
        this.bound = v.findViewById(R.id.text_habit_setup_bound);
        habitSetupListener(v);
    }

    private void habitSetupListener(View v) {
        // Setup the logic for checking the validity of the new habit in the OnClickListener
        this.createHabit = v.findViewById(R.id.button_create_habit);
        this.createHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String inputHabitName = habitName.getText().toString();
                final String inputLocationName = locationName.getText().toString();
                final boolean useGps = switchEnableGps.isChecked();
                final String inputLocationAddress = locationAddress.getText().toString();
                final boolean checkMon = checkMonday.isChecked();
                final boolean checkTues = checkTuesday.isChecked();
                final boolean checkWed = checkWednesday.isChecked();
                final boolean checkThurs = checkThursday.isChecked();
                final boolean checkFri = checkFriday.isChecked();
                final boolean checkSat = checkSaturday.isChecked();
                final boolean checkSun = checkSunday.isChecked();
                startTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        final String inputStartTime = adapterView.getItemAtPosition(i).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
                final String inputStartTime = startTime.getSelectedItem().toString();

                endTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        final String inputEndTime = adapterView.getItemAtPosition(i).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
                final String inputEndTime = endTime.getSelectedItem().toString();

                final boolean isContinuous = continuous.isChecked();
                final String inputBound = bound.getText().toString();

                if (inputHabitName.equals("")) {
                    Toast.makeText(v.getContext(),"Please enter a name for the habit.",
                            Toast.LENGTH_LONG).show();
                }
                else if (inputLocationName.equals("")) {
                    Toast.makeText(v.getContext(),"Please enter a name for the location.",
                            Toast.LENGTH_LONG).show();
                }
                else if (inputLocationAddress.equals("")) {
                    Toast.makeText(v.getContext(),"Please enter an address for the habit location, even if the gps is not enabled!",
                            Toast.LENGTH_LONG).show();
                }
                else if (!checkMon && !checkTues && !checkWed && !checkThurs && !checkFri && !checkSat && !checkSun) {
                    Toast.makeText(v.getContext(),"Please select at least one day that this habit occurs on.",
                            Toast.LENGTH_LONG).show();
                }
                else if (inputStartTime.equals("")) {
                    Toast.makeText(v.getContext(),"Please enter a start time for the habit.",
                            Toast.LENGTH_LONG).show();
                }
                else if (inputEndTime.equals("")) {
                    Toast.makeText(v.getContext(),"Please enter an end time for the habit.",
                            Toast.LENGTH_LONG).show();
                }
                else if (!isContinuous && inputBound.equals("")) {
                    Toast.makeText(v.getContext(),"Please set a number of occurrences, or set the habit as continuous.",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    // Package boolean information
                    HashMap<String, Boolean> booleans = new HashMap<String, Boolean>() {{
                        put("Monday", checkMon);
                        put("Tuesday", checkTues);
                        put("Wednesday", checkWed);
                        put("Thursday", checkThurs);
                        put("Friday", checkFri);
                        put("Saturday", checkSat);
                        put("Sunday", checkSun);
                        put("GPS", useGps);
                        put("Continuous", isContinuous);
                    }};
                    // Package string information
                    HashMap<String, String> strings = new HashMap<String, String>() {{
                        put("Habit Name", inputHabitName);
                        put("Location Name", inputLocationName);
                        put("Location Address", inputLocationAddress);
                        put("Start Time", inputStartTime);
                        put("End Time", inputEndTime);
                        put("Bound", inputBound);
                    }};


//                    // TODO lat & long needs to get into the habit setup repo, somehow. see todo message in habit setup repo: HabitSetupRepository -> line 128
//
//                    // You probably need to call this logic in the habit setup repo to accomplish that (not from the fragment here)
//
//                    // You won't be able to use a toast from the habit setup repo - instead, you can make a function
//                    // in this fragment and call it from the habit setup repo by using this.callback.yourFunction()
//
//                    // For example, you could run this where the I specified in the habit setup repo, and in your conditional
//                    // ladder, use a callback to stop the habit creation if the location is invalid
//
//                    // Talk to me if that didn't make sense
//
//                    // As of right now, the lat and long of locations are not being set, just have placeholder values of 0
//
//
//                    if (inputLocationAddress != null && inputLocationAddress.length() > 0) {
//                        String locatorService = "https://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer";
//                        mLocatorTask = new LocatorTask(locatorService);
//                        mGeocodeParameters = new GeocodeParameters();
//                        mGeocodeParameters.getResultAttributeNames().add("*");
//                        mGeocodeParameters.setMaxResults(1);
//
//                        final ListenableFuture<List<GeocodeResult>> geocodeFuture = mLocatorTask.geocodeAsync(inputLocationAddress, mGeocodeParameters);
//                        geocodeFuture.addDoneListener(new Runnable() {
//                            @Override
//                            public void run() {
//                                latitude = 50;
//                                try {
//                                    List<GeocodeResult> geocodeResults = geocodeFuture.get();
//                                    if (geocodeResults.size() > 0) {
//                                        TextView name = getActivity().findViewById(R.id.textView2);
//                                        latitude = Math.round(geocodeResults.get(0).getDisplayLocation().getX()*1000000d)/1000000d;
//                                        longitude = Math.round(geocodeResults.get(0).getDisplayLocation().getY()*1000000d)/1000000d;
//                                        // Location object creation is handled already, just set this.latitude and this.longitude in the habitsetup repo
//                                        //Location location = new Location(inputLocationName,latitude, longitude);
//                                        name.setText(inputLocationAddress +" "+latitude + ", " + longitude);
//                                    }
//                                    else {
//                                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.nothing_found) + " " + inputLocationAddress, Toast.LENGTH_LONG).show();
//                                    }
//                                } catch (InterruptedException | ExecutionException e) {
//                                    // ... determine how you want to handle an error
//                                    Toast.makeText(getActivity().getApplicationContext(),e.getMessage(),
//                                        Toast.LENGTH_LONG).show();
//                                }
//                                geocodeFuture.removeDoneListener(this); // Done searching, remove the listener.
//                            }
//                        });
//                    }
//                    TextView name2 = getActivity().findViewById(R.id.textView);
//                    name2.setText(inputLocationAddress +" "+latitude + ", " + longitude);
                    // Send habit data -> ViewModel -> Repository for insertion routine
                    habitSetupViewModel.provideHabitSetupInfo(booleans, strings);
                    provideCallback();
                }
            }
        });
    }

    private void provideCallback() {
        habitSetupViewModel.setHabitSetupRepoCallback(this);
    }

    public void announceHabitCreated(){
        Toast.makeText(getContext(), "Habit created successfully!", Toast.LENGTH_LONG).show();
        this.habitName.setText("");
        this.locationName.setText("");
        this.switchEnableGps.setChecked(false);
        this.locationAddress.setText("");
        this.checkMonday.setChecked(false);
        this.checkTuesday.setChecked(false);
        this.checkWednesday.setChecked(false);
        this.checkThursday.setChecked(false);
        this.checkFriday.setChecked(false);
        this.checkSaturday.setChecked(false);
        this.checkSunday.setChecked(false);
        this.startTime.setSelection(0);
        this.endTime.setSelection(0);
        this.continuous.setChecked(false);
        this.bound.setText("");
    }
}
