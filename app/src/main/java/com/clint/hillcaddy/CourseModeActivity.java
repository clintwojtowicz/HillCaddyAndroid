package com.clint.hillcaddy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CourseModeActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private Sensor temperatureSensor;
    private Sensor relativeHumiditySensor;
    private Float pressure_hPa;
    private Float temp_Celsius;
    private Float relativeHumidity;

    GlobalVars globals;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_mode);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        globals = (GlobalVars) getApplicationContext();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        relativeHumiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_course_mode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        initializeDensitySensors();
        setBackgroundImage();
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this, pressureSensor);
        sensorManager.unregisterListener(this, temperatureSensor);
        sensorManager.unregisterListener(this, relativeHumiditySensor);
        super.onPause();
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_PRESSURE:
                pressure_hPa = event.values[0];
                sensorManager.unregisterListener(this, pressureSensor);
                break;

            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                temp_Celsius = event.values[0];
                sensorManager.unregisterListener(this, temperatureSensor);
                break;

            case Sensor.TYPE_RELATIVE_HUMIDITY:
                relativeHumidity = event.values[0];
                sensorManager.unregisterListener(this, relativeHumiditySensor);
                break;

        }


    }


    public void showDistanceCard(View view) {
        //calculate the average shot for each club before showing distance card
        Profile profile = globals.getCurrentProfile();
        DatabaseHelper db = globals.getDB();
        profile.calculateClubAverages(db, globals.getRo());
        globals.setCurrentProfile(profile);

        Intent intent = new Intent(this, DistanceCardActivity.class);
        startActivity(intent);


    }


    public void showRecommendedClubs(View view) {
        Profile profile = globals.getCurrentProfile();
        DatabaseHelper db = globals.getDB();
        profile.calculateClubAverageShot(db);
        Intent intent = new Intent(this, RecommendedClubActivity.class);
        startActivity(intent);

    }

    public void calculateAirDensity(View view) {
        Boolean sensorsValid = this.checkForSensors();

        if (sensorsValid) {

            Double ro = ShotCalculator.calculateAirDensity(pressure_hPa * 100, temp_Celsius + 273, relativeHumidity);

            this.showResultsMessage("Results", "Measured Air Density is: \n\n" + String.format("%.2f", ro) + " kg/m^3");
        }

    }

    private void initializeDensitySensors()
    {
        Boolean sensorsValid = this.checkForSensorsNoMessage();

        if (sensorsValid) {

            //get sensor measurements
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(this, relativeHumiditySensor, SensorManager.SENSOR_DELAY_FASTEST);
        }



    }

    private Boolean checkForSensorsNoMessage()
    {
        Boolean valid = true;
        if(pressureSensor == null)
        {
            valid = false;
        }
        if (temperatureSensor == null)
        {
            valid = false;
        }
        if (relativeHumiditySensor == null)
        {
            valid = false;
        }

        return valid;

    }

    private Boolean checkForSensors()
    {
        Boolean valid = true;
        String badSensors = "";
        if(pressureSensor == null)
        {
            badSensors += "Pressure\n";
            valid = false;
        }
        if (temperatureSensor == null)
        {
            badSensors += "Ambient Temperature\n";
            valid = false;
        }
        if (relativeHumiditySensor == null)
        {
            badSensors += "Relative Humidity\n";
            valid = false;
        }
        if (!valid)
        {
            //no sensor available, show them a message and make the button unclickable
            this.showErrorMessage("Error", "The following sensors are not available on this device: \n\n" + badSensors + "\nWould you like to enter the missing data?");
        }

        return valid;


    }

    public void showResultsMessage(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.show();
    }

    public void showErrorMessage(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getBaseContext(), AirDensityActivity.class);
                startActivity(intent);


            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.show();
    }

    public void showSettingsView(MenuItem view)
    {
        //show Settings view
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }

    private void setBackgroundImage()
    {
        LinearLayout layout = (LinearLayout)findViewById(R.id.courseMode_background);

        if(globals.getBackgroundSetting()) {
            layout.setBackgroundResource(R.drawable.fallbrook_cropped);
        }
        else{
            layout.setBackgroundColor(Color.WHITE);
        }

    }
}
