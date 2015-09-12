package com.clint.hillcaddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class SelectModeActivity extends AppCompatActivity {

    GlobalVars globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);
        globals = ((GlobalVars)getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_mode, menu);
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

    public void showCourseModeView(View view)
    {
        Intent intent = new Intent(this, CourseModeActivity.class);
        startActivity(intent);

        //calculate the average shot for each club when entering course mode
        Profile profile = globals.getCurrentProfile();
        DatabaseHelper db = globals.getDB();
        profile.calculateClubAverages(db);
        globals.setCurrentProfile(profile);

    }

    public void showRangeModeView(View view)
    {
        Intent intent = new Intent(this, RangeModeActivity.class);
        startActivity(intent);

    }


}
