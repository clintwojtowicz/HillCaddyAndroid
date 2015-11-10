package com.clint.hillcaddy;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

public class DistanceCardActivity extends AppCompatActivity {

    GlobalVars globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_card);
        globals = ((GlobalVars) getApplicationContext());
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.showDistanceCard();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_distance_card, menu);
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

    public void showDistanceCard() {
        Profile profile = globals.getCurrentProfile();
        List<ShotResult> distanceList = profile.getClubDistances();

        TableLayout distanceTable = (TableLayout) findViewById(R.id.distanceCard_table);
        TableRow header = new TableRow(this);

        TextView tv0 = new TextView(this);
        tv0.setText("Club      ");
        tv0.setTextColor(Color.BLACK);
        tv0.setTypeface(null, Typeface.BOLD);
        tv0.setTextSize(25);
        header.addView(tv0);

        TextView tv1 = new TextView(this);
        tv1.setText("Carry Distance (yds) ");
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

    }


}
