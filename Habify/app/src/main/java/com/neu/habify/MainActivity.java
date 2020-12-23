package com.neu.habify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.neu.habify.data.entity.User;
import com.neu.habify.data.room.DataSetupRepository;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private final String MAIN_ACTIVITY = "MainActivity";
    private DataSetupRepository setupRepo;
    private EditText firstName;
    private EditText lastName;
    private EditText userName;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManager = getSystemService(NotificationManager.class);

        createNotificationChannel(
                "Channel1",
                "Habit",
                "Habit Channel");

        Log.i(MAIN_ACTIVITY, "running data setup routine");
        setupRepo = new DataSetupRepository(getApplication());
        setupRepo.verifyData(this);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_profile, R.id.navigation_dashboard, R.id.navigation_manage)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp()
                || super.onSupportNavigateUp();
    }

    protected void createNotificationChannel(String id, String name,
                                             String description) {
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel =
                    new NotificationChannel(id, name, importance);

            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
//            channel.setVibrationPattern(
//                    new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void setupUserDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_first_time_user_setup)
                .setTitle("Profile: First Time Setup")
                .setMessage("Please enter the following information for your profile.")
                .setPositiveButton("Confirm", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button confirmButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Scrape the text that the user gave
                        String inputFirstName = firstName.getText().toString();
                        String inputLastName = lastName.getText().toString();
                        String inputUserName = userName.getText().toString();
                        // Validate the entries, checking for nulls
                        if (inputFirstName != null && !inputFirstName.equals("") &&
                                inputLastName != null && !inputLastName.equals("") &&
                                inputUserName != null && !inputUserName.equals("")) {
                            provideDataSetupRepoWithUserInfo(inputFirstName, inputLastName, inputUserName);
                            dialog.dismiss();
                        }
                        // Otherwise the dialog should stay open and tell the user how to proceed
                        else {
                            Toast.makeText(getApplicationContext(),"Please enter all fields",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        // Assign EditTexts after showing the dialog, so they exist when we try to find them
        dialog.show();
        firstName = dialog.findViewById(R.id.input_first_name);
        lastName = dialog.findViewById(R.id.input_last_name);
        userName = dialog.findViewById(R.id.input_user_name);
    }

    public void provideDataSetupRepoWithUserInfo(String first, String last, String username) {
        this.setupRepo.setupUser(first, last, username);
    }
}
