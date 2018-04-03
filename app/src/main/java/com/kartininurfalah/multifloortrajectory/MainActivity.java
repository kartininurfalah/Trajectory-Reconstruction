package com.kartininurfalah.multifloortrajectory;

import android.app.Activity;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends Activity implements SensorEventListener {

    public static  final String NA = "NA";
    public static final String FIXED = "FIXED";
    //location min time
    private static final int LOCATION_MIN_TIME = 30 * 1000;
    //location min disance
    private static final int LOCATION_MIN_DISTANCE = 10;
    // Gravity for accelerometer data
    private float[] gravity = new float[3];
    // magnetic data
    private float[] geomagnetic = new float[3];
    // Rotation data
    private float[] rotation = new float[9];
    // orientation (azimuth, pitch, roll)
    private float[] orientation = new float[3];
    // smoothed values
    private float[] smoothed = new float[3];
    //sensor manager
    private SensorManager SM;
    private TextView xAccel, yAccel, zAccel, xMagno, yMagno, zMagno, pressure;
    //sensor gravity and pressure
    private Sensor sensorAccel, sensorMagno, sensorPressure;
    private LocationManager locationManager;
    private Location currentLocation;
    private GeomagneticField geomagneticField;
    private double bearing = 0;
    private TextView textDirection, textLat, textLong;
    private CompasView compasView;

    private float[] mGravity = new float[3];
    private float[] mGeometric = new float[3];
    private float azimuth = 0f;
    private float currectAzimuth = 0f;
    private ImageView imageView;
    public void btnStartKlik(View view){


    }

    public void btnResetKlik(View view){


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textLat = (TextView) findViewById(R.id.latitude);
        textLong = (TextView) findViewById(R.id.longitude);
        textDirection = (TextView) findViewById(R.id.text);
        compasView = (CompasView) findViewById(R.id.compass);

        //keep screen light on (wake lock light)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //imageView
        imageView = (ImageView) findViewById(R.id.kompasImage);

        //Assign textview
        xAccel = (TextView) findViewById(R.id.xAccel);
        yAccel = (TextView) findViewById(R.id.yAccel);
        zAccel = (TextView) findViewById(R.id.zAccel);

        xMagno = (TextView) findViewById(R.id.xMagno);
        yMagno = (TextView) findViewById(R.id.yMagno);
        zMagno = (TextView) findViewById(R.id.zMagno);

        pressure = (TextView) findViewById(R.id.pressure);


    }

    @Override
    protected void onStart() {
        super.onStart();
        //create our sensor manager
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        //Accelerometer Sensor
        sensorAccel = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //MagnetometerSensor
        sensorMagno = SM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //Barometer(Pressure) Sensor
        sensorPressure = SM.getDefaultSensor(Sensor.TYPE_PRESSURE);
        //Register Sensor Listener Accel
        SM.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
        //Register Sensor Listener Magno
        SM.registerListener(this, sensorMagno, SensorManager.SENSOR_DELAY_NORMAL);
        //Register Sensor Listener Presure
        SM.registerListener(this, sensorPressure, SensorManager.SENSOR_DELAY_NORMAL);
        //request location manager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE,this);
        //get last known position

    }

    @Override
    protected void onResume() {
        super.onResume();
        SM.registerListener(this, SM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
        SM.registerListener(this, SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        SM.registerListener(this, SM.getDefaultSensor(Sensor.TYPE_PRESSURE),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SM.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        final float alpha = 0.97f;
        synchronized (this){
            if(sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                mGravity[0] = alpha*mGravity[0] + (1-alpha)*sensorEvent.values[0];
                mGravity[1] = alpha*mGravity[1] + (1-alpha)*sensorEvent.values[1];
                mGravity[2] = alpha*mGravity[2] + (1-alpha)*sensorEvent.values[2];
            }
            if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                mGeometric[0] = alpha*mGeometric[0] + (1-alpha)*sensorEvent.values[0];
                mGeometric[1] = alpha*mGeometric[1] + (1-alpha)*sensorEvent.values[1];
                mGeometric[2] = alpha*mGeometric[2] + (1-alpha)*sensorEvent.values[2];
            }
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R,I,mGravity, mGeometric);

            if (success){
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = (float)Math.toDegrees(orientation[0]);

                azimuth = (azimuth + 360) % 360;

                //

                Animation anim =  new RotateAnimation(-currectAzimuth, -azimuth, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                currectAzimuth = azimuth;

                anim.setDuration(500);
                anim.setRepeatCount(0);
                anim.setFillAfter(true);

                imageView.startAnimation(anim);
            }
        }

        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            xAccel.setText("X: " + sensorEvent.values[0]);
            yAccel.setText("Y: " + sensorEvent.values[1]);
            zAccel.setText("Z: " + sensorEvent.values[2]);
        } else if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            xMagno.setText("X: " + sensorEvent.values[0]);
            yMagno.setText("Y: " + sensorEvent.values[1]);
            zMagno.setText("Z: " + sensorEvent.values[2]);
        } else if(sensor.getType() == Sensor.TYPE_PRESSURE){
            pressure.setText("Pressure : " +sensorEvent.values[0]);
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {


    }
}
