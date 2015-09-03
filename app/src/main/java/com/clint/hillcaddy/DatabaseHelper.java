package com.clint.hillcaddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Clint on 9/3/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "hillcaddy";
    private static final String TABLE_PROFILES = "profiles";

    //profiles table
    private static final String KEY_NAME = "name";
    private static final String KEY_LASTUSED = "lastUsed";

    //shots table
    private static final String KEY_CLUBNAME = "clubName";
    private static final String KEY_BALLSPEED = "ballspeed";
    private static final String KEY_BACKSPIN = "backspin";
    private static final String KEY_ANGLE = "launchAngle";


    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //create profile table if it doesn't already exist
        String createProfileTable = "CREATE TABLE IF NOT EXISTS "+TABLE_PROFILES+"("+KEY_NAME+" VARCHAR, "+KEY_LASTUSED+" INTEGER);";
        db.execSQL(createProfileTable);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //put action for upgrading here

    }

    public void addProfile(String user)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user);
        values.put(KEY_LASTUSED, 0);

        db.insert(TABLE_PROFILES, null, values);
        createShotTable(user, db);

        db.close();

    }

    public void createShotTable(String user, SQLiteDatabase db)
    {
        String cmd = "CREATE TABLE IF NOT EXISTS "+user+"("+KEY_CLUBNAME+" VARCHAR, "+KEY_BALLSPEED+" DOUBLE, "+KEY_BACKSPIN+" INTEGER, "+KEY_ANGLE+" DOUBLE);";
        db.execSQL(cmd);

    }

    public void removeProfile(String user)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROFILES, KEY_NAME + " = ?", new String[] {user});
        removeShotTable(user, db);

        db.close();

    }

    public void removeShotTable(String user, SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS " + user);

    }

    public void addShot(String user, String club, Shot newShot)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CLUBNAME, club);
        values.put(KEY_BALLSPEED, newShot.getBallSpeed());
        values.put(KEY_BACKSPIN, newShot.getBackSpin());
        values.put(KEY_ANGLE, newShot.getLaunchAngle());

        db.insert(user, null, values);

        db.close();
    }

    public Profile getLastUsedProfile()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String command = "SELECT "+KEY_NAME+ " FROM "+TABLE_PROFILES+" WHERE "+KEY_LASTUSED+" = '1';";
        Cursor cursor = db.rawQuery(command, null);

        Profile profile = new Profile();
        if(cursor.moveToFirst()) {
            String name = cursor.getString(0);
            profile.setName(name);

            //get all of the clubs for that profile

        }
        else
        {
            //no last used profiles, select first one

        }

        return profile;


    }





}
