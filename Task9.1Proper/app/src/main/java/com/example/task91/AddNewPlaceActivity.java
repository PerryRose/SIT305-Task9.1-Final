package com.example.task91;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.task91.data.DatabaseHelper;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class AddNewPlaceActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    LocationManager locationManager;
    LocationListener locationListener;
    AutocompleteSupportFragment autocompleteFragment;
    Location locationToSave = new Location("dummyProvider");
    EditText placeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place);

        databaseHelper = new DatabaseHelper(this);

        setUpWidgets();
        setUpPlaces();
        setUpLocationService();
    }

    private void setUpWidgets()
    {
        placeName = findViewById(R.id.placeNameEditText);
    }

    private void setUpLocationService() {
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

            }
        };

        // Check for permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public void getCurrentLocation(View view) {
        // Check for permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        // Get Location
        locationToSave = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Set Fragment text to Coordinates
        autocompleteFragment.setText(locationToSave.getLatitude() + ", " + locationToSave.getLongitude());
    }

    private void setUpPlaces()
    {
        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.API_KEY));

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.

                locationToSave.setLatitude(place.getLatLng().latitude);
                locationToSave.setLongitude(place.getLatLng().longitude);
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check permissions
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }


    public void showLocationOnMap(View view)
    {
        if (locationToSave.getLatitude() != 0 && locationToSave.getLongitude() != 0)
        {
            Intent intent = new Intent(AddNewPlaceActivity.this, ShowOnMapActivity.class);
            intent.putExtra("Lat", locationToSave.getLatitude());
            intent.putExtra("Lon", locationToSave.getLongitude());
            startActivity(intent);
        }
        else
        {
            Toast.makeText(AddNewPlaceActivity.this, "Please select a location", Toast.LENGTH_LONG).show();
        }
    }

    private void resetFields()
    {
        placeName.setText("");
        autocompleteFragment.setText("");
        locationToSave = new Location("dummyProvider");
    }

    public void saveLocation(View view) {
        if (locationToSave.getLatitude() != 0 && locationToSave.getLongitude() != 0)
        {
            if (!placeName.getText().toString().isEmpty()) {
                // Save Location into database
                com.example.task91.model.Location location = new com.example.task91.model.Location();
                location.setName(placeName.getText().toString());
                location.setLatitude("" + locationToSave.getLatitude());
                location.setLongitude("" + locationToSave.getLongitude());

                long result = databaseHelper.insertLocation(location);

                // If successful
                if (result > -1) {

                    Toast.makeText(AddNewPlaceActivity.this, "Successfully added location", Toast.LENGTH_LONG).show();
                    resetFields();

                } else {
                    // Error
                    Toast.makeText(AddNewPlaceActivity.this, "Could not add location", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                // No name
                Toast.makeText(AddNewPlaceActivity.this, "Please enter a location name", Toast.LENGTH_LONG).show();
            }

        }
        else
        {
            // No location set
            Toast.makeText(AddNewPlaceActivity.this, "Please select a location", Toast.LENGTH_LONG).show();
        }
    }
}