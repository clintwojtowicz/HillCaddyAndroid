package com.clint.hillcaddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class RangeModeActivity extends AppCompatActivity {

    GlobalVars globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_range_mode);
        globals = ((GlobalVars)getApplicationContext());
        updateClubsSpinner();
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

    public void addShot(View view)
    {
        Double ballSpeed = 0.0;
        Integer backSpin = 0;
        Double launchAngle = 0.0;
        try {
            EditText speedEditText = (EditText) findViewById(R.id.ballSpeed_range_editText);
            ballSpeed = Double.parseDouble(speedEditText.getText().toString());

            EditText spinEditText = (EditText) findViewById(R.id.backSpin_range_editText);
            backSpin = Integer.parseInt(spinEditText.getText().toString());

            EditText angleEditText = (EditText) findViewById(R.id.launchAngle_range_editText);
            launchAngle = Double.parseDouble(angleEditText.getText().toString());
        }
        catch (Exception e)
        {
            //show invalid shot message box
            showMessage("Invalid Shot Parameter");
        }

        Shot shot = new Shot(ballSpeed, backSpin, launchAngle);

        //send shot data to db
        //addShotToDB(ballSpeed, backSpin, launchAngle);



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

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){}
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


}
