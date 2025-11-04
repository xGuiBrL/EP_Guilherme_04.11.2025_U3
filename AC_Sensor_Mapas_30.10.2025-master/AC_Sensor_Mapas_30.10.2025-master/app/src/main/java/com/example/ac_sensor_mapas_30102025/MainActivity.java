package com.example.ac_sensor_mapas_30102025;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sensor_container, new SensorFragment(), "SENSOR_FRAGMENT")
                    .replace(R.id.map_container, new MapFragmentCustom(), "MAP_FRAGMENT")
                    .commit();
        }
    }

    public void onMovementChanged(boolean moving) {
        MapFragmentCustom mapFragment = (MapFragmentCustom) getSupportFragmentManager().findFragmentByTag("MAP_FRAGMENT");
        if (mapFragment != null) {
            mapFragment.setMovementState(moving);
        }
    }
}