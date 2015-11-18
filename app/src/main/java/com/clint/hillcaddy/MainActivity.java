package com.clint.hillcaddy;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    GlobalVars globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        globals = ((GlobalVars)getApplicationContext());


    }

    @Override
    protected void onResume()
    {
        setBackgroundImage();
        updateProfsSpinner();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void showCreateProfileView(MenuItem view)
    {
        //show Create New Profile view
        Intent intent = new Intent(this, CreateProfileActivity.class);
        startActivity(intent);

    }

    public void showCreateProfileView(View view)
    {
        //show Create New Profile view
        Intent intent = new Intent(this, CreateProfileActivity.class);
        startActivity(intent);

    }

    private void updateProfsSpinner()
    {
        DatabaseHelper db = globals.getDB();
        List<String> nameList = db.getAllProfileNames();

        Spinner spinner = (Spinner)findViewById(R.id.selProf_Spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nameList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

    }

    public void showCourseModeView(View view)
    {
        //load selected profile from local db
        Spinner profSpinner = (Spinner)findViewById(R.id.selProf_Spinner);
        Integer profileCount = profSpinner.getCount();

        if (profileCount > 0)
        {
            String selectedProf = profSpinner.getSelectedItem().toString();
            globals.setCurrentProfileWithName(selectedProf);
            Intent intent = new Intent(this, CourseModeActivity.class);
            startActivity(intent);

        }
        else
        {
            //there is no profile available so inform the user to create one before proceeding
            showCreateProfileView(view);
        }

    }

    public void showRangeModeView(View view)
    {
        //load selected profile from local db
        Spinner profSpinner = (Spinner)findViewById(R.id.selProf_Spinner);
        Integer profileCount = profSpinner.getCount();


        if (profileCount > 0)
        {
            String selectedProf = profSpinner.getSelectedItem().toString();
            globals.setCurrentProfileWithName(selectedProf);
            Intent intent = new Intent(this, RangeModeActivity.class);
            startActivity(intent);

        }
        else
        {
            //there is no profile available so inform the user to create one before proceeding
            showCreateProfileView(view);
        }

    }

    public void showSettingsView(MenuItem view)
    {
        //show Settings view
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }

    public void showAboutView(MenuItem view)
    {
        //show Settings view
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);

    }

    private void setBackgroundImage()
    {
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.main_background);

        if(globals.getBackgroundSetting()) {
            layout.setBackgroundResource(R.drawable.torrey_cropped);
        }
        else{
            layout.setBackgroundColor(Color.WHITE);
        }

    }


}
