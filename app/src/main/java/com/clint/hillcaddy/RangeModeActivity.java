package com.clint.hillcaddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class RangeModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_range_mode);
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
        EditText speedEditText = (EditText)findViewById(R.id.ballSpeed_range_editText);
        Float ballSpeed = Float.parseFloat(speedEditText.getText().toString());

        EditText spinEditText = (EditText)findViewById(R.id.backSpin_range_editText);
        Integer backSpin = Integer.parseInt(spinEditText.getText().toString());

        EditText angleEditText = (EditText)findViewById(R.id.launchAngle_range_editText);
        Float launchAngle = Float.parseFloat(angleEditText.getText().toString());

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


}
