package com.example.ep_guilherme_04112025_u3;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends AppCompatActivity implements fragment_sensor.OnMovementListener {

    private fragment_map fragmentMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.card_sensor, new fragment_sensor())
                    .commit();

            fragmentMap = new fragment_map();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.card_map, fragmentMap)
                    .commit();
        }
    }

    @Override
    public void onMovementChanged(String movementType) {
        if (fragmentMap != null) {
            fragmentMap.updateMarker(movementType);
        }
    }
}