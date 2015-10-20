package com.clint.hillcaddy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class AirDensityActivity extends AppCompatActivity implements SensorEventListener{

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
        setContentView(R.layout.activity_air_density);

        globals = ((GlobalVars)getApplicationContext());

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_air_density, menu);
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
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        relativeHumiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

        showDataForWorkingSensors();

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
        switch(event.sensor.getType())
        {
            case Sensor.TYPE_PRESSURE:
                pressure_hPa = event.values[0];
                sensorManager.unregisterListener(this, pressureSensor);
                EditText pressureText = (EditText) findViewById(R.id.pressure_airDens_editText);
                Integer press = Math.round(pressure_hPa);
                pressureText.setText(press.toString());
                break;

            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                temp_Celsius = event.values[0];
                sensorManager.unregisterListener(this, temperatureSensor);
                EditText temperatureText = (EditText) findViewById(R.id.temp_airDens_editText);
                temperatureText.setText(Conversion.celsiusToFahrenheit(temp_Celsius).toString());
                break;

            case Sensor.TYPE_RELATIVE_HUMIDITY:
                relativeHumidity = event.values[0];
                sensorManager.unregisterListener(this, relativeHumiditySensor);
                EditText humidityText = (EditText) findViewById(R.id.humidity_airDens_editText);
                Integer humid = Math.round(relativeHumidity);
                humidityText.setText(humid.toString());
                break;


        }


    }

    private void showDataForWorkingSensors()
    {
        temp_Celsius = (float)0.0;
        pressure_hPa = (float)0.0;
        relativeHumidity = (float)0.0;

        //show 0's in all three field to start and wait for sensor input if it occurs
        EditText pressureText = (EditText) findViewById(R.id.pressure_airDens_editText);
        Integer press = Math.round(pressure_hPa);
        pressureText.setText(press.toString());

        EditText temperatureText = (EditText) findViewById(R.id.temp_airDens_editText);
        temperatureText.setText(Conversion.celsiusToFahrenheit(temp_Celsius).toString());

        EditText humidityText = (EditText) findViewById(R.id.humidity_airDens_editText);
        Integer humid = Math.round(relativeHumidity);
        humidityText.setText(humid.toString());

        if(pressureSensor != null)
        {
            //get pressure measurements
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_FASTEST);


        }
        if (temperatureSensor != null)
        {
            //get temp measurements
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_FASTEST);

        }
        if (relativeHumiditySensor != null)
        {
            //get relative humidity measurements
            sensorManager.registerListener(this, relativeHumiditySensor, SensorManager.SENSOR_DELAY_FASTEST);


        }



    }


    public void performManualAirDensityCalc(View view)
    {
        EditText pressureText = (EditText) findViewById(R.id.pressure_airDens_editText);
        pressure_hPa = Float.parseFloat(pressureText.getText().toString());

        EditText temperatureText = (EditText) findViewById(R.id.temp_airDens_editText);
        Integer tempF = Integer.parseInt(temperatureText.getText().toString());

        EditText humidityText = (EditText) findViewById(R.id.humidity_airDens_editText);
        relativeHumidity = Float.parseFloat(humidityText.getText().toString());

        temp_Celsius = Conversion.fahrenheitToCelsius(tempF);

        Double ro = ShotCalculator.calculateAirDensity(pressure_hPa * 100, temp_Celsius + 273, relativeHumidity);

        globals.setRo(ro);

        this.showResultsMessage("Results", "Measured Air Density is: " + ro.toString() + " kg/m^3");

    }


    public void setAirDensityToDefault(View view)
    {
        //set Ro back to 1.2
        globals.setRo(Constants.roAirSeaLvl);

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
}
