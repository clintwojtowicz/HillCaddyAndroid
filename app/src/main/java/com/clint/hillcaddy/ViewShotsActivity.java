package com.clint.hillcaddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ViewShotsActivity extends AppCompatActivity {

    GlobalVars globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shots);
        globals = ((GlobalVars)getApplicationContext());
        updateClubsSpinner();
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

    public void updateClubsSpinner()
    {
        Profile profile = globals.getCurrentProfile();
        ArrayList<String> clubNames = profile.getClubNameList();

        Spinner spinner = (Spinner)findViewById(R.id.selClub_viewShots_Spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, clubNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

    }

    public void showShots(View view)
    {
        Spinner clubSpinner = (Spinner)findViewById(R.id.selClub_viewShots_Spinner);
        String selectedClub = clubSpinner.getSelectedItem().toString();

        DatabaseHelper db = globals.getDB();
        Profile profile = globals.getCurrentProfile();
        String currentUser = profile.getName();

        List<String> shots = db.getShotsAsStringsWithLabels(currentUser, selectedClub);

        ListView shotsView = (ListView)findViewById(R.id.shots_listView);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, shots);
        shotsView.setAdapter(listAdapter);
        shotsView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    }

    public void removeSelectedShot(View view)
    {
        ListView shotsView = (ListView)findViewById(R.id.shots_listView);
        int position = shotsView.getCheckedItemPosition();

        boolean valid = true;
        String selectedItem = "";
        try
        {
            selectedItem = shotsView.getItemAtPosition(position).toString();
        }
        catch (Exception e)
        {
            valid = false;
        }
        if (valid)
        {
            Shot selectedShot = parseShotLabels(selectedItem);

            DatabaseHelper db = globals.getDB();
            Profile profile = globals.getCurrentProfile();
            String currentUser = profile.getName();

            Spinner clubSpinner = (Spinner) findViewById(R.id.selClub_viewShots_Spinner);
            String currentClubName = clubSpinner.getSelectedItem().toString();

            db.removeShot(currentUser, currentClubName, selectedShot);

            showShots(getCurrentFocus());
        }

    }

    public Shot parseShotLabels(String shotWithLabels)
    {
        //"Ball Speed: "+ cursor.getString(0) + " Back Spin: "+ cursor.getString(1) + " Launch Angle: "+ cursor.getString(2);

        shotWithLabels = shotWithLabels.replaceAll("Ball Speed: ", "");
        shotWithLabels = shotWithLabels.replaceAll("Back Spin: ", "");
        shotWithLabels = shotWithLabels.replaceAll("Side Spin: ", "");
        shotWithLabels = shotWithLabels.replaceAll("Launch Angle: ", "");
        shotWithLabels = shotWithLabels.replaceAll("  ", " ");

        String[] values = shotWithLabels.split(" ");

        Double speed = Double.parseDouble(values[0].trim());
        Integer spin = Integer.parseInt(values[1].trim());
        Integer sideSpin = Integer.parseInt(values[2].trim());
        Double angle = Double.parseDouble(values[3].trim());

        Shot shot = new Shot(speed, spin, sideSpin, angle);
        return shot;

    }



}
