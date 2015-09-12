package com.clint.hillcaddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class DistanceCardActivity extends AppCompatActivity {

    GlobalVars globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_card);
        globals = ((GlobalVars)getApplicationContext());
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

    public void showDistanceCard()
    {
        Profile profile = globals.getCurrentProfile();
        List<String> distanceList = profile.getClubDistancesAsStrings();

        ListView distancesView = (ListView)findViewById(R.id.distances_listView);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, distanceList);
        distancesView.setAdapter(listAdapter);
        distancesView.setChoiceMode(ListView.CHOICE_MODE_NONE);

    }
}
