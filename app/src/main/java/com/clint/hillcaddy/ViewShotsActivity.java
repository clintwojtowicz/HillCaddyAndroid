package com.clint.hillcaddy;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

public class ViewShotsActivity extends AppCompatActivity implements EventListener{

    GlobalVars globals;
    Spinner clubsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shots);
        globals = ((GlobalVars)getApplicationContext());
        //force the screen to be in landscape orientation only
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        clubsSpinner = (Spinner)findViewById(R.id.selClub_viewShots_Spinner);
        updateClubsSpinner();
        setSpinnerListener();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_shots, menu);
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
        super.onPause();
    }

    public void updateClubsSpinner()
    {
        Profile profile = globals.getCurrentProfile();
        ArrayList<String> clubNames = profile.getClubNameList();

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, clubNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clubsSpinner.setAdapter(spinnerAdapter);

    }

    private void setSpinnerListener()
    {
        clubsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showShots();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void showShots()
    {
        String selectedClub = clubsSpinner.getSelectedItem().toString();

        DatabaseHelper db = globals.getDB();
        Profile profile = globals.getCurrentProfile();
        String currentUser = profile.getName();

        List<Shot> shots = db.getShots(currentUser, selectedClub);

        TableLayout shotTable = (TableLayout) findViewById(R.id.shots_table);
        shotTable.removeAllViews();

        TableRow header = new TableRow(this);

        TextView tv0 = new TextView(this);
        tv0.setText("Speed (mph)  ");
        tv0.setTextColor(Color.BLACK);
        tv0.setTextSize(15);
        header.addView(tv0);

        TextView tv1 = new TextView(this);
        tv1.setText("Launch Angle (deg)  ");
        tv1.setTextColor(Color.BLACK);
        tv1.setTextSize(15);
        header.addView(tv1);

        TextView tv2 = new TextView(this);
        tv2.setText("Backspin (rpm)  ");
        tv2.setTextColor(Color.BLACK);
        tv2.setTextSize(15);
        header.addView(tv2);

        TextView tv3 = new TextView(this);
        tv3.setText("Sidespin (rpm)");
        tv3.setTextColor(Color.BLACK);
        tv3.setTextSize(15);
        header.addView(tv3);

        shotTable.addView(header);

        Iterator<Shot> iterator = shots.iterator();

        while (iterator.hasNext()) {
            Shot shot = iterator.next();

            TableRow tbrow = new TableRow(this);

            TextView t1v = new TextView(this);
            t1v.setText(shot.getBallSpeed().toString());
            t1v.setTextColor(Color.BLACK);
            t1v.setGravity(Gravity.CENTER);
            t1v.setTextSize(12);
            tbrow.addView(t1v);

            TextView t2v = new TextView(this);
            t2v.setText(shot.getLaunchAngle().toString());
            t2v.setTextColor(Color.BLACK);
            t2v.setGravity(Gravity.CENTER);
            t2v.setTextSize(12);
            tbrow.addView(t2v);

            TextView t3v = new TextView(this);
            t3v.setText(shot.getBackSpin().toString());
            t3v.setTextColor(Color.BLACK);
            t3v.setGravity(Gravity.CENTER);
            t3v.setTextSize(12);
            tbrow.addView(t3v);

            TextView t4v = new TextView(this);
            t4v.setText(shot.getSideSpin().toString());
            t4v.setTextColor(Color.BLACK);
            t4v.setGravity(Gravity.CENTER);
            t4v.setTextSize(12);
            tbrow.addView(t4v);

            //set button id to shot id so we can use it to remove the shot
            //this is okay because android assigned ids start at 0x00FFFFFF, there will never be that many shots
            Button button = new Button(this);
            button.setText("Remove");
            button.setId(shot.getID());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeSelectedShot(v);


                }
            });
                tbrow.addView(button);

            shotTable.addView(tbrow);
        }

    }

    public void removeSelectedShot(View view)
    {

        DatabaseHelper db = globals.getDB();
        Profile profile = globals.getCurrentProfile();
        String currentUser = profile.getName();
        Spinner clubSpinner = (Spinner) findViewById(R.id.selClub_viewShots_Spinner);
        String currentClubName = clubSpinner.getSelectedItem().toString();

        db.removeShot(currentUser, currentClubName, view.getId());

        showShots();


    }






}
