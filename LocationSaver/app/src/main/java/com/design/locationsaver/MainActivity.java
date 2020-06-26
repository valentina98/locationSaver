package com.design.locationsaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    Button btn_save, btn_view_all;
    EditText et_place_name, et_x, et_y;
    Switch sw_currentLocation;
    ListView lv_locationList;

    ArrayAdapter locationArrayAdapter;
    DataBaseHelper dataBaseHelper;

    // Location request is a config file for all settings related to FusedLocationProviderClient.
    LocationRequest locationRequest;

    // Google's API location services.
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_save = findViewById(R.id.save_btn);
        btn_view_all = findViewById(R.id.view_all_btn);
        et_place_name = findViewById(R.id.place_name);
        et_x = findViewById(R.id.x_axis);
        et_y = findViewById(R.id.y_axis);
        sw_currentLocation = findViewById(R.id.current_location_switch);
        lv_locationList = findViewById(R.id.location_list);

        dataBaseHelper = new DataBaseHelper(MainActivity.this);

        showLocationsOnListView(dataBaseHelper);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    LocationModel locationModel;

                    locationModel = new LocationModel(-1, et_place_name.getText().toString(), Double.parseDouble(et_x.getText().toString()), Double.parseDouble(et_y.getText().toString()), sw_currentLocation.isChecked());

                    DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
                    dataBaseHelper.addOne(locationModel);

                    Toast.makeText(MainActivity.this, "Success saving location.", Toast.LENGTH_SHORT).show();

                    showLocationsOnListView(dataBaseHelper);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error saving Location.", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(MainActivity.this, "Success: " + success, Toast.LENGTH_SHORT).show();

                // Set all properties of LocationRequest
                locationRequest = new LocationRequest();
                locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
                locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
                locationRequest.setPriority(locationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            }

        });

        btn_view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<LocationModel> allLocations = dataBaseHelper.getAllLocations();

                showLocationsOnListView(dataBaseHelper);

                //Toast.makeText(MainActivity.this, allLocations.toString(), Toast.LENGTH_SHORT).show();

            }
        });

        lv_locationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LocationModel clickedLocation = (LocationModel) parent.getItemAtPosition(position);
                dataBaseHelper.deleteOne(clickedLocation);
                showLocationsOnListView(dataBaseHelper);
                String deleteMessage = "Deleted place '" + clickedLocation.getPlaceName() + "', x: " + clickedLocation.getX() + ", y: " + clickedLocation.getY() ;
                Toast.makeText(MainActivity.this, deleteMessage, Toast.LENGTH_SHORT).show();

            }
        });

        sw_currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sw_currentLocation.isChecked()) {
                    //locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    updateGPS();
                    Toast.makeText(MainActivity.this, "Using GPS.", Toast.LENGTH_SHORT).show(); //"Using GPS sensors."
                }
                else {
                    //locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    Toast.makeText(MainActivity.this, "Using manual location.", Toast.LENGTH_SHORT).show(); //"Using Towers + WIFI."
                }
            }
        });

        //updateGPS();
    } // end of onCreate method

    private void showLocationsOnListView(DataBaseHelper dataBaseHelper) {
        // fills the list with our data
        locationArrayAdapter = new ArrayAdapter<LocationModel>(MainActivity.this, android.R.layout.simple_list_item_1, dataBaseHelper.getAllLocations());
        lv_locationList.setAdapter((locationArrayAdapter));
    }

    // GPS methods

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1 && hasAllPermissionsGranted(grantResults)) {
            updateGPS();
        }
        else {
            Toast.makeText(MainActivity.this, "This app requires permission to be granted in order to work properly.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValues(location);
                }
            });
        }
        else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION); //
        }
    }

    private void updateUIValues(Location location) {
        // update the text view objects with a new location.

        et_x.setText(String.valueOf(location.getLatitude()));
        et_y.setText(String.valueOf(location.getLongitude()));
        //et_y.setText(String.valueOf(location.getLongitude()));
        //Toast.makeText(MainActivity.this, "Accuracy: " + location.getAccuracy(), Toast.LENGTH_SHORT).show();

        /*
        if (location.hasAltitude()) {
            tv_altitude.setText("Altitude: " + String.valueOf(location.getLongitude()));
        }
        else {
            tv_altitude.setText("Altitude not available");
        }
        if (location.hasSpeed()) {
            tv_speed.setText("Speed: " + String.valueOf(location.getSpeed()));
        }
        else {
            tv_speed.setText("Speed not available");
        }*/

    }
}