package com.clint.hillcaddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

public class ClubResultActivity extends AppCompatActivity {

    GlobalVars globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_result);

        Intent intent = getIntent();
        Integer Zdist = intent.getIntExtra(CourseModeActivity.EXTRA_ZDIST, 0);
        Integer Ydist = intent.getIntExtra(CourseModeActivity.EXTRA_YDIST, 0);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        globals = ((GlobalVars)getApplicationContext());

        showShotResults(Ydist, Zdist);
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
        getMenuInflater().inflate(R.menu.menu_club_result, menu);
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

    private void showShotResults(Integer ydist, Integer zdist)
    {
        Profile profile = globals.getCurrentProfile();
        List<ShotResult> distanceList = profile.getAllDistancesFromTarget(ydist, zdist, globals.getRo());

        TableLayout distanceTable = (TableLayout) findViewById(R.id.resultCard_table);
        TableRow header = new TableRow(this);

        TextView tv0 = new TextView(this);
        tv0.setText("Club      ");
        tv0.setTextColor(Color.BLACK);
        tv0.setTypeface(null, Typeface.BOLD);
        tv0.setTextSize(25);
        header.addView(tv0);

        TextView tv1 = new TextView(this);
        tv1.setText("Carry Distance \nFrom Target (yds)");
        tv1.setTextColor(Color.BLACK);
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setTextSize(25);
        header.addView(tv1);

        distanceTable.addView(header);

        Iterator<ShotResult> iterator = distanceList.iterator();

        while (iterator.hasNext()) {
            ShotResult shot = iterator.next();

            TableRow tbrow = new TableRow(this);
            TextView t1v = new TextView(this);
            t1v.setText(shot.getClubName());
            t1v.setTextColor(Color.BLACK);
            t1v.setTypeface(null, Typeface.BOLD);
            t1v.setTextSize(20);
            tbrow.addView(t1v);

            TextView t2v = new TextView(this);
            t2v.setText(shot.getDistance().toString());
            t2v.setTextColor(Color.BLACK);
            t2v.setTypeface(null, Typeface.BOLD);
            t2v.setTextSize(20);
            tbrow.addView(t2v);

            distanceTable.addView(tbrow);
        }

        ShotResult closestShot = findClosestShot(distanceList);
        showClosestClubDialog(closestShot);

    }

    public void showClosestClubDialog(ShotResult shot)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("HillCaddy Says...");
        if (shot.getDistance() < 0) {
            Integer shotAbs = Math.abs(shot.getDistance());
            builder.setMessage("The closest club is your " + shot.getClubName() + ". It will land " + shotAbs.toString() + " yds short of the target");
        }
        else if (shot.getDistance() < 1000)
        {
            builder.setMessage("The closest club is your " + shot.getClubName() + ". It will land " + shot.getDistance().toString() + " yds past the target");
        }
        else
        {
            builder.setMessage("No valid shots available, go to range mode and check your clubs and shots.");
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){}
        });

        builder.show();
    }

    private ShotResult findClosestShot(List<ShotResult> shots)
    {
        ShotResult closest = new ShotResult();
        closest.setDistance(1000);

        Iterator<ShotResult> iterator = shots.iterator();

        while (iterator.hasNext())
        {
            ShotResult shotToCheck = iterator.next();
            if(Math.abs(shotToCheck.getDistance()) < Math.abs(closest.getDistance()))
            {
                closest = shotToCheck;
            }

        }

        return closest;

    }

    public void showSettingsView(MenuItem view)
    {
        //show Settings view
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }

    private void setBackgroundImage()
    {
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.clubResult_background);

        if(globals.getBackgroundSetting()) {
            layout.setBackgroundResource(R.drawable.fallbrook_cropped_opaque);
        }
        else{
            layout.setBackgroundColor(Color.WHITE);
        }

    }

}
