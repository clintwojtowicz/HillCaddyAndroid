package com.clint.hillcaddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

public class AngleCaptureActivity extends Activity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private float xGrav = 0;
    private float yGrav = 0;
    private double theta = 0;

    public final static String EXTRA_THETA = "com.clint.hillcaddy.THETA_CAPTURED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_angle_capture);

        //force the screen to be in landscape orientation only
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        this.checkForGravitySensor();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_angle_capture, menu);
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
        sensorManager.unregisterListener(this, gravitySensor);
        super.onPause();
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    public void onSensorChanged(SensorEvent event)
    {
        xGrav = event.values[0];
        yGrav = event.values[1];

        //use -1 because the y axis is being used to point towards target
        theta = -1 * Math.atan(yGrav/xGrav);
        int thetaRoundedDegrees = (int) Math.round(Conversion.radiansToDegrees(theta));

        Intent intent = new Intent(this, RecommendedClubActivity.class);
        intent.putExtra(EXTRA_THETA, thetaRoundedDegrees);
        startActivity(intent);


    }



    public void captureAngle(View view)
    {
        //when the button is pressed this captures the current angle
        //register the listener then get the data from the onSensorChanged function
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_FASTEST);



    }

    private void checkForGravitySensor()
    {
        if(gravitySensor == null)
        {
            //no sensor available, show them a message and make the button unclickable
            this.showMessage("No Gravity Sensor Available.");
            Button captureButton = (Button)findViewById(R.id.captureAngleButton);
            captureButton.setClickable(false);
        }


    }

    public void showMessage(String m)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Error");
        builder.setMessage(m);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                //return to the previous screen because there is no gravity sensor available
                onBackPressed();
            }
        });

        builder.show();
    }
}
