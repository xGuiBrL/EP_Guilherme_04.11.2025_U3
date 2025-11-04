package com.example.ac_sensor_mapas_30102025;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SensorFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private TextView tvX, tvY, tvZ;

    private float lastMagnitude = -1f;
    private static final float MOVEMENT_THRESHOLD = 1.0f;
    private static final long MOVEMENT_UPDATE_INTERVAL = 250; // ms between movement updates

    private android.os.Handler mainHandler;
    private long lastMovementUpdate = 0;
    private boolean lastMovementState = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sensor, container, false);
        tvX = v.findViewById(R.id.tv_x);
        tvY = v.findViewById(R.id.tv_y);
        tvZ = v.findViewById(R.id.tv_z);
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;

        final float x = event.values[0];
        final float y = event.values[1];
        final float z = event.values[2];

        // Update UI on main thread
        mainHandler.post(() -> {
            if (tvX != null) tvX.setText(String.format("X: %.2f", x));
            if (tvY != null) tvY.setText(String.format("Y: %.2f", y));
            if (tvZ != null) tvZ.setText(String.format("Z: %.2f", z));
        });

        // Calculate movement with rate limiting
        float magnitude = (float) Math.sqrt(x * x + y * y + z * z);
        boolean moving = false;
        if (lastMagnitude >= 0) {
            float delta = Math.abs(magnitude - lastMagnitude);
            moving = delta > MOVEMENT_THRESHOLD;
        }
        lastMagnitude = magnitude;

        // Rate limit movement updates
        long now = System.currentTimeMillis();
        if (moving != lastMovementState && now - lastMovementUpdate > MOVEMENT_UPDATE_INTERVAL) {
            lastMovementState = moving;
            lastMovementUpdate = now;
            // Update movement state on main thread
            final boolean finalMoving = moving;
            mainHandler.post(() -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).onMovementChanged(finalMoving);
                }
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
