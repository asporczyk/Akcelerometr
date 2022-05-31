package com.example.akcelerometr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    Sensor acceleometer;
    Sensor temperature;
    Sensor light;

    ImageView imageView;

    float velocityX, velocityY;

    Spinner spinner;
    ArrayAdapter<CharSequence> ad;

    void turnOn(int mode){
        if (acceleometer != null) {
            sensorManager.registerListener(this, acceleometer, mode);
        } else {
//            TVx.setText("X: null");
//            TVy.setText("Y: null");
//            TVz.setText("Z: null");


        }
        if (temperature != null) {
            sensorManager.registerListener(this, temperature, mode);
        } else {
            //TVtemp.setText("Temperature: null");
        }
        if (light != null) {
            sensorManager.registerListener(this, light, mode);
        } else {
            //TVlight.setText("Light: null");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView2);

        velocityX = 0;
        velocityY = 0;

        spinner = findViewById(R.id.spinner);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        acceleometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        temperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        turnOn(SensorManager.SENSOR_DELAY_NORMAL);

        ad = ArrayAdapter.createFromResource(this, R.array.modes, android.R.layout.simple_spinner_item);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(ad);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch ((String) spinner.getSelectedItem()) {
                    case "NORMAL":
                        sensorManager.unregisterListener(MainActivity.this);
                        turnOn(SensorManager.SENSOR_DELAY_NORMAL);
                        break;
                    case "UI":
                        sensorManager.unregisterListener(MainActivity.this);
                        turnOn(SensorManager.SENSOR_DELAY_UI);
                        break;
                    case "GAME":
                        sensorManager.unregisterListener(MainActivity.this);
                        turnOn(SensorManager.SENSOR_DELAY_GAME);
                        break;
                    case "FASTEST":
                        sensorManager.unregisterListener(MainActivity.this);
                        turnOn(SensorManager.SENSOR_DELAY_FASTEST);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor s = sensorEvent.sensor;
        if (s.getType() == Sensor.TYPE_ACCELEROMETER) {

//            TVx.setText("X: " + sensorEvent.values[0]);
//            TVy.setText("Y: " + sensorEvent.values[1]);
//            TVz.setText("Z: " + sensorEvent.values[2]);

            float dx = sensorEvent.values[0];
            float dy = sensorEvent.values[1];

            float newVelocityX = (float) (velocityX + 0.01*dx);
            float newVelocityY = (float) (velocityY + 0.01*dy);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            if(imageView.getX() - sensorEvent.values[0] >= 0 && imageView.getX() - sensorEvent.values[0] <= displayMetrics.widthPixels - imageView.getWidth()){
                imageView.setX(imageView.getX() - newVelocityX);
            } else {
                newVelocityX = -newVelocityX*0.75f;
                imageView.setX(imageView.getX() - newVelocityX);
            }

            Resources resources = getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");


            if(imageView.getY() + sensorEvent.values[1] >= 0 && imageView.getY() + sensorEvent.values[1] <= displayMetrics.heightPixels - imageView.getHeight()*1.5f - resources.getDimensionPixelSize(resourceId)){
                imageView.setY(imageView.getY() + newVelocityY);
            } else{
                newVelocityY = -newVelocityY*0.75f;
                imageView.setY(imageView.getY() + newVelocityY);
            }

            velocityY = newVelocityY;
            velocityX = newVelocityX;

        }
        if (s.getType() == Sensor.TYPE_LIGHT) {
            //TVlight.setText("Light: " + sensorEvent.values[0]);
        }
        if (s.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            //TVtemp.setText("Temperature: " + sensorEvent.values[0]);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        spinner.setSelection(1);
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        spinner.setSelection(0);
    }
}

