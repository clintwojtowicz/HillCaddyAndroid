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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

    public final static String EXTRA_YDIST = "com.clint.hillcaddy.Y_DISTANCE";
    public final static String EXTRA_ZDIST = "com.clint.hillcaddy.Z_DISTANCE";


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

        //see if an angle was captured, if it was show the captured angle
        Intent intent = getIntent();
        Integer angleCaptured = intent.getIntExtra(AngleCaptureActivity.EXTRA_THETA, 0);
        EditText angleText = (EditText) findViewById(R.id.angle_recommendClub_editText);
        angleText.setText(angleCaptured.toString());

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

    public void calculateBestClub(View view)
    {
        //need to add a getCurrentProfileAndAvgs() so that averages can be recalculated if they have gone out of scope
        //or... add the calculated averages to the clubs db for each profile to save them and improve speed
        Profile profile = globals.getCurrentProfile();
        DatabaseHelper db = globals.getDB();
        profile.calculateClubAverageShot(db);

        this.hideKeyboard();

        Double angleToTarg = 0.0;
        Integer straightLineDist = 0;

        boolean valid = true;
        try {
            EditText speedEditText = (EditText) findViewById(R.id.angle_recommendClub_editText);
            angleToTarg = Double.parseDouble(speedEditText.getText().toString());

            EditText spinEditText = (EditText) findViewById(R.id.distance_recommendClub_editText);
            straightLineDist = Integer.parseInt(spinEditText.getText().toString());


        }
        catch (Exception e)
        {
            //show invalid parameters message box
            showMessage("Invalid Angle or Distance Parameter");
            valid = false;
        }
        if(valid)
        {
            Integer Ydist = ShotCalculator.getDistance(angleToTarg, straightLineDist);
            Integer Zdist = ShotCalculator.getLandingHeight(angleToTarg, straightLineDist);

            Intent intent = new Intent(this, ClubResultActivity.class);
            intent.putExtra(EXTRA_YDIST, Ydist);
            intent.putExtra(EXTRA_ZDIST, Zdist);

            startActivity(intent);


        }

    }


    public void showAngleCaptureView(View view)
    {
        Intent intent = new Intent(this, AngleCaptureActivity.class);
        startActivity(intent);

        //send a variable to this activity that will return the angle captured by gyro and then use it to fill the angle field


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
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.courseMode_background);

        if(globals.getBackgroundSetting()) {
            layout.setBackgroundResource(R.drawable.fallbrook_cropped_opaque);
        }
        else{
            layout.setBackgroundColor(Color.WHITE);
        }

    }

    private void hideKeyboard()
    {
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public void showMessage(String m)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Error");
        builder.setMessage(m);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){}
        });

        builder.show();
    }

    public void showInstructions(MenuItem item)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Instructions");
        builder.setMessage(getResources().getString(R.string.instructions_recommendClub_text));

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){}
        });

        builder.show();

    }
}
