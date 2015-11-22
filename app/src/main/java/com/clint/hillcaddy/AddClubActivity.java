package com.clint.hillcaddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.ArrayList;

public class AddClubActivity extends AppCompatActivity {

    GlobalVars globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_club);
        globals = ((GlobalVars)getApplicationContext());
        updateClubsSpinner();
    }

    @Override
    protected void onResume()
    {
        setBackgroundImage();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_club, menu);
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

    public void addClubToProfile(View view)
    {
        hideKeyboard();
        EditText nameEditText = (EditText)findViewById(R.id.name_addClub_editText);
        String clubName = nameEditText.getText().toString();
        nameEditText.getText().clear();

        clubName = clubName.trim();
        if(clubName.isEmpty())
        {
            //invalid name, show error window

        }
        else
        {
            Club newClub = new Club(clubName);

            Profile profile = globals.getCurrentProfile();
            profile.addClubToBag(newClub);
            globals.setCurrentProfile(profile);

            DatabaseHelper db = globals.getDB();
            db.addClub(profile.getName(), newClub);

            updateClubsSpinner();
        }


    }

    public void removeClubFromProfile(View view)
    {
        //get the club name from the spinner and remove it from the profile and db
        Spinner clubSpinner = (Spinner)findViewById(R.id.selClub_edit_Spinner);

        DatabaseHelper db = globals.getDB();
        Profile profile = globals.getCurrentProfile();

        if(clubSpinner.getCount() > 0)
        {
            String clubUsed = clubSpinner.getSelectedItem().toString();
            profile.removeClubFromBag(clubUsed);
            db.removeClub(profile.getName(), clubUsed);
            updateClubsSpinner();

        }



    }

    private void updateClubsSpinner()
    {
        Profile profile = globals.getCurrentProfile();
        ArrayList<String> clubNames = profile.getClubNameList();


        Spinner spinner = (Spinner)findViewById(R.id.selClub_edit_Spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, clubNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

    }

    private void hideKeyboard()
    {
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public void showSettingsView(MenuItem view)
    {
        //show Settings view
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }

    private void setBackgroundImage()
    {
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.addClub_background);

        if(globals.getBackgroundSetting()) {
            layout.setBackgroundResource(R.drawable.colorado_cropped_opaque);
        }
        else{
            layout.setBackgroundColor(Color.WHITE);
        }

    }

}
