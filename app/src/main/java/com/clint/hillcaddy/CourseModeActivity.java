package com.clint.hillcaddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class CourseModeActivity extends Activity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private Sensor temperatureSensor;
    private Sensor relativeHumiditySensor;
    private Integer currentSensor;
    private Float pressure_hPa;
    private Float temp_Celsius;
    private Float relativeHumidity;
    private Boolean tempFlag;
    private Boolean humidFlag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_mode);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        relativeHumiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        currentSensor = 0;
        this.checkForSensors();
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
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this, pressureSensor);
        sensorManager.unregisterListener(this, temperatureSensor);
        sensorManager.unregisterListener(this, relativeHumiditySensor);
        super.onPause();
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    public void onSensorChanged(SensorEvent event)
    {
        switch(currentSensor)
        {
            case Sensor.TYPE_PRESSURE:
                pressure_hPa = event.values[0];
                sensorManager.unregisterListener(this, pressureSensor);
                tempFlag = true;
                break;

            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                temp_Celsius = event.values[0];
                sensorManager.unregisterListener(this, temperatureSensor);
                humidFlag = true;
                break;

            case Sensor.TYPE_RELATIVE_HUMIDITY:
                relativeHumidity = event.values[0];
                sensorManager.unregisterListener(this, relativeHumiditySensor);
                break;


        }


    }


    public void showDistanceCard(View view)
    {
        Intent intent = new Intent(this, DistanceCardActivity.class);
        startActivity(intent);


    }


    public void showRecommendedClubs(View view)
    {
        Intent intent = new Intent(this, RecommendedClubActivity.class);
        startActivity(intent);


    }

    public void calculateAirDensity(View view)
    {
        tempFlag = false;
        humidFlag = true;

        //get pressure measurements
        currentSensor = Sensor.TYPE_PRESSURE;
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_UI);

        while(tempFlag == false && humidFlag == false) {

            if(tempFlag) {
                //get temp measurements
                currentSensor = Sensor.TYPE_AMBIENT_TEMPERATURE;
                sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_UI);
            }

            if(humidFlag) {
                //get relative humidity measurements
                currentSensor = Sensor.TYPE_RELATIVE_HUMIDITY;
                sensorManager.registerListener(this, relativeHumiditySensor, SensorManager.SENSOR_DELAY_UI);
            }

        }

        Double ro = ShotCalculator.calculateAirDensity(pressure_hPa * 100, temp_Celsius + 273, relativeHumidity);

        this.showDialogMessage("Results", "Measured Air Density is: " + ro.toString() + "kg/m^3");

    }

    private void checkForSensors()
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
            this.showDialogMessage("Error", "The following sensors are not available: \n" + badSensors + "\nDefault sea level air density of 1.2 will be used");
            Button captureButton = (Button)findViewById(R.id.calcDens_course_button);
            captureButton.setClickable(false);
        }



    }

    public void showDialogMessage(String title, String message)
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
}
