package com.example.ep_nombre_04112025_unidad_3;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.SupportMapFragment;

public class fragment_sensor extends Fragment implements SensorEventListener {
    TextView xCoor, yCoor, zCoor, estado;
    TextView xGiro, yGiro, zGiro, estadoGiro;
    SensorManager gestorSensores;
    Sensor acelerometro;
    Sensor giroscopio;
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xCoor = findViewById(R.id.xCoor);
        yCoor = findViewById(R.id.yCoor);
        zCoor = findViewById(R.id.zCoor);
        estado = findViewById(R.id.estado);

        xGiro = findViewById(R.id.xGiro);
        yGiro = findViewById(R.id.yGiro);
        zGiro = findViewById(R.id.zGiro);
        estadoGiro = findViewById(R.id.estadoGiro);

        gestorSensores = (SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometro = gestorSensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        giroscopio = gestorSensores.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            xCoor.setText(String.format("X: %.2f", x));
            yCoor.setText(String.format("Y: %.2f", y));
            zCoor.setText(String.format("Z: %.2f", z));

            double magnitud = Math.sqrt(x * x + y * y + z * z);
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
    protected void onResume() {
        super.onResume();
        if (acelerometro != null) {
            gestorSensores.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (giroscopio != null) {
            gestorSensores.registerListener(this, giroscopio, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    protected void onPause() {
        super.onPause();
        gestorSensores.unregisterListener(this);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}