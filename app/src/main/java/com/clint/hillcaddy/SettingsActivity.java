package com.clint.hillcaddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.EventListener;

public class SettingsActivity extends AppCompatActivity implements EventListener{

    GlobalVars globals;
    CheckBox backgroundCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        globals = ((GlobalVars)getApplicationContext());
        backgroundCheckBox = (CheckBox)findViewById(R.id.background_settings_checkBox);

        //make sure the settings table has been created
        //DatabaseHelper db = globals.getDB();
        //db.createSettingsTable();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){

            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        updateBackgroundCheckBox();
        setBackgroundCheckBoxListener();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void updateBackgroundCheckBox()
    {
       backgroundCheckBox.setChecked(globals.getBackgroundSetting());

    }

    private void setBackgroundCheckBoxListener()
    {
        backgroundCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                globals.setBackgroundSetting(isChecked);
            }
        });

    }
}
