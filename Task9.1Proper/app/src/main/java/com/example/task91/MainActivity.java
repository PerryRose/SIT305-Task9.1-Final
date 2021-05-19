package com.example.task91;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button addNewPlaceButton, showAllPlacesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNewPlaceButton = findViewById(R.id.addNewPlaceBtn);
        showAllPlacesButton = findViewById(R.id.showAllPlacesBtn);

    }

    public void addNewPlace(View view) {
        // Start Add New Place Activity
        Intent intent = new Intent(MainActivity.this, AddNewPlaceActivity.class);
        startActivity(intent);
    }

    public void showAllOnMap(View view) {
        // Start Maps Activity
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }
}