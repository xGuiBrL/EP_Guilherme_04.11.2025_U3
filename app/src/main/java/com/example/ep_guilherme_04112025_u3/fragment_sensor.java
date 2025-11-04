package com.example.ep_guilherme_04112025_u3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

public class fragment_sensor extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor acelerometro;
    private Sensor giroscopio;
    TextView xCoor, yCoor, zCoor, estado;
    TextView xGiro, yGiro, zGiro, estadoGiro;
    private TextView precision, precisionGiro;

    private ArrayList<Float> historialX = new ArrayList<>();
    private ArrayList<Float> historialY = new ArrayList<>();
    private ArrayList<Float> historialZ = new ArrayList<>();

    private OnMovementListener movementListener;

    public interface OnMovementListener {
        void onMovementChanged(String movementType);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            movementListener = (OnMovementListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnMovementListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensor, container, false);

        xCoor = view.findViewById(R.id.xCoor);
        yCoor = view.findViewById(R.id.yCoor);
        zCoor = view.findViewById(R.id.zCoor);
        estado = view.findViewById(R.id.estado);

        xGiro = view.findViewById(R.id.xGiro);
        yGiro = view.findViewById(R.id.yGiro);
        zGiro = view.findViewById(R.id.zGiro);
        estadoGiro = view.findViewById(R.id.estadoGiro);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        giroscopio = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, giroscopio, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            xCoor.setText(String.format("%.2f", x));
            yCoor.setText(String.format("%.2f", y));
            zCoor.setText(String.format("%.2f", z));

            manageAccelHistory(x, y, z);
            classifyMovement(x, y, z);

        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            xGiro.setText(String.format("%.2f", x));
            yGiro.setText(String.format("%.2f", y));
            zGiro.setText(String.format("%.2f", z));
        }
    }

    private void manageAccelHistory(float x, float y, float z) {
        if (historialX.size() >= 10) {
            historialX.remove(0);
            historialY.remove(0);
            historialZ.remove(0);
        }
        historialX.add(x);
        historialY.add(y);
        historialZ.add(z);
    }

    private void classifyMovement(float x, float y, float z){
        SensorEvent event = null;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float xa = event.values[0];
            float ya = event.values[1];
            float za = event.values[2];

            xCoor.setText(String.format("X: %.2f", x));
            yCoor.setText(String.format("Y: %.2f", y));
            zCoor.setText(String.format("Z: %.2f", z));

            double magnitud = Math.sqrt(xa * xa + ya * ya + za * za);
            if (magnitud > 11) {
                estado.setText("¡Demasiado movimiento! Cuidado con el terremoto");
            } else if (magnitud < 9) {
                estado.setText("¡Estás inclinando mucho el teléfono!");
            } else {
                estado.setText("Todo estable. ¡Buen control de gravedad!");
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float gx = event.values[0];
            float gy = event.values[1];
            float gz = event.values[2];

            xGiro.setText(String.format("X: %.2f", gx));
            yGiro.setText(String.format("Y: %.2f", gy));
            zGiro.setText(String.format("Z: %.2f", gz));

            double giroMag = Math.sqrt(gx * gx + gy * gy + gz * gz);
            if (giroMag > 3.0) {
                estadoGiro.setText("¡Rotación rápida detectada!");
            } else if (giroMag < 0.2) {
                estadoGiro.setText("Sin rotación apreciable.");
            } else {
                estadoGiro.setText("Rotación suave.");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                break;
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                break;
        }
    }
}
