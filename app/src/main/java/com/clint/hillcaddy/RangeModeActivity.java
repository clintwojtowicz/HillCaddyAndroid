package com.clint.hillcaddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.ArrayList;

public class RangeModeActivity extends AppCompatActivity {

    GlobalVars globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_range_mode);
        globals = ((GlobalVars)getApplicationContext());
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_range_mode, menu);
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
    protected void onResume()
    {
        setBackgroundImage();
        updateClubsSpinner();
        super.onResume();
    }

    public void addShot(View view)
    {
        //check to see if there is a club selected, if not direct user to addClub activity
        Spinner clubSpinner = (Spinner) findViewById(R.id.selClub_range_Spinner);
        if (clubSpinner.getCount() < 1)
        {
            addNewClub(view);
        }
        else {
            Double ballSpeed = 0.0;
            Integer backSpin = 0;
            Integer sideSpin = 0;
            Double launchAngle = 0.0;

            boolean validShot = true;
            try {
                EditText speedEditText = (EditText) findViewById(R.id.ballSpeed_range_editText);
                ballSpeed = Double.parseDouble(speedEditText.getText().toString());

                EditText spinEditText = (EditText) findViewById(R.id.backSpin_range_editText);
                backSpin = Integer.parseInt(spinEditText.getText().toString());

                EditText sideSpinEditText = (EditText) findViewById(R.id.sideSpin_range_editText);
                sideSpin = Integer.parseInt(sideSpinEditText.getText().toString());

                EditText angleEditText = (EditText) findViewById(R.id.launchAngle_range_editText);
                launchAngle = Double.parseDouble(angleEditText.getText().toString());
            } catch (Exception e) {
                //show invalid shot message box
                showMessage("Invalid Shot Parameter");
                validShot = false;
            }

            if (validShot) {
                Shot shot = new Shot(ballSpeed, backSpin, sideSpin, launchAngle, 0);

                //send shot data to db
                DatabaseHelper db = globals.getDB();
                String currentProf = globals.getCurrentProfile().getName();

                String clubUsed = clubSpinner.getSelectedItem().toString();

                db.addShot(currentProf, clubUsed, shot);

                this.clearShotTextEdits();
            }
        }

    }

    public void addNewClub(View view)
    {
        Intent intent = new Intent(this, AddClubActivity.class);
        startActivity(intent);

    }

    public void viewShots(View view)
    {
        Intent intent = new Intent(this, ViewShotsActivity.class);
        startActivity(intent);

    }

    public void showMessage(String m)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Error");
        builder.setMessage(m);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.show();
    }

    public void updateClubsSpinner()
    {
        Profile profile = globals.getCurrentProfile();
        ArrayList<String> clubNames = profile.getClubNameList();


        Spinner spinner = (Spinner)findViewById(R.id.selClub_range_Spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, clubNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

    }

    public void clearShotTextEdits()
    {
        EditText speedEditText = (EditText) findViewById(R.id.ballSpeed_range_editText);
        speedEditText.getText().clear();

        EditText spinEditText = (EditText) findViewById(R.id.backSpin_range_editText);
        spinEditText.getText().clear();

        EditText sideSpinEditText = (EditText) findViewById(R.id.sideSpin_range_editText);
        sideSpinEditText.getText().clear();

        EditText angleEditText = (EditText) findViewById(R.id.launchAngle_range_editText);
        angleEditText.getText().clear();


    }

    public void showSettingsView(MenuItem view)
    {
        //show Settings view
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }

    private void setBackgroundImage()
    {
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.rangeMode_background);

        if(globals.getBackgroundSetting()) {
            layout.setBackgroundResource(R.drawable.colorado_cropped_opaque);
        }
        else{
            layout.setBackgroundColor(Color.WHITE);
        }

    }



}
