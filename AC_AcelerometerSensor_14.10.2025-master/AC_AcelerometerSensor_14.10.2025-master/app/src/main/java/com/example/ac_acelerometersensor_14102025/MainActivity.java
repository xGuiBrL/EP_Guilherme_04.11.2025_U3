package com.example.ac_acelerometersensor_14102025;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ac_acelerometersensor_14102025.R;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView xCoor, yCoor, zCoor, estado, precision;
    TextView xGiro, yGiro, zGiro, estadoGiro, precisionGiro;
    TextView valorMag, estadoMag, precisionMag;
    ProgressBar barraMag;
    TextView sensoresDisponibles;
    SensorManager gestorSensores;
    Sensor acelerometro;
    Sensor giroscopio;
    Sensor magnetometro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xCoor = findViewById(R.id.xcoor);
        yCoor = findViewById(R.id.ycoor);
        zCoor = findViewById(R.id.zcoor);
        estado = findViewById(R.id.estado);
        precision = findViewById(R.id.precision);

        xGiro = findViewById(R.id.xgiro);
        yGiro = findViewById(R.id.ygiro);
        zGiro = findViewById(R.id.zgiro);
        estadoGiro = findViewById(R.id.estadoGiro);
        precisionGiro = findViewById(R.id.precisionGiro);

        barraMag = findViewById(R.id.magBar);
        valorMag = findViewById(R.id.magValue);
        estadoMag = findViewById(R.id.estadoMag);
        precisionMag = findViewById(R.id.precisionMag);

        sensoresDisponibles = findViewById(R.id.sensoresDisponibles);

        gestorSensores = (SensorManager) getSystemService(SENSOR_SERVICE);

        acelerometro = gestorSensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        giroscopio = gestorSensores.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometro = gestorSensores.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        String lista = "Sensores: "
                + (acelerometro != null ? "Acelerómetro ✓" : "Acelerómetro ✗") + "  |  "
                + (giroscopio != null ? "Giroscopio ✓" : "Giroscopio ✗") + "  |  "
                + (magnetometro != null ? "Magnetómetro ✓" : "Magnetómetro ✗");
        sensoresDisponibles.setText(lista);

        if (acelerometro != null) {
            gestorSensores.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Tu dispositivo no tiene acelerómetro", Toast.LENGTH_LONG).show();
        }

        if (giroscopio != null) {
            gestorSensores.registerListener(this, giroscopio, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Tu dispositivo no tiene giroscopio", Toast.LENGTH_LONG).show();
        }

        if (magnetometro != null) {
            gestorSensores.registerListener(this, magnetometro, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Tu dispositivo no tiene magnetómetro", Toast.LENGTH_LONG).show();
        }
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

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float mx = event.values[0];
            float my = event.values[1];
            float mz = event.values[2];

            double campo = Math.sqrt(mx * mx + my * my + mz * mz);
            valorMag.setText(String.format("Campo: %.2f µT", campo));

            int progreso = (int) Math.min(campo, 200);
            barraMag.setProgress(progreso);

            if (campo > 120) {
                estadoMag.setText("Campo magnético muy alto (posible interferencia).");
            } else if (campo < 15) {
                estadoMag.setText("Campo bajo (ambiente magnético débil).");
            } else {
                estadoMag.setText("Campo magnético dentro de lo normal.");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        String mensaje;
        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                mensaje = "Precisión baja: el sensor está confundido.";
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                mensaje = "Precisión baja: intenta no moverlo tanto.";
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                mensaje = "Precisión media: ¡va mejorando!";
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                mensaje = "Precisión alta: ¡perfecto!";
                break;
            default:
                mensaje = "Estado desconocido.";
        }

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            precision.setText(mensaje);
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            precisionGiro.setText(mensaje);
        } else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            precisionMag.setText(mensaje);
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
        if (magnetometro != null) {
            gestorSensores.registerListener(this, magnetometro, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gestorSensores.unregisterListener(this);
    }
}
