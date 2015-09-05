package com.clint.hillcaddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

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
        createClubTable(user, db);

        setLastUsed(user, db);

        db.close();

    }

    public void createShotTable(String user, SQLiteDatabase db)
    {
        String cmd = "CREATE TABLE IF NOT EXISTS "+user+"_shots("+KEY_CLUBNAME+" VARCHAR, "+KEY_BALLSPEED+" DOUBLE, "+KEY_BACKSPIN+" INTEGER, "+KEY_ANGLE+" DOUBLE);";
        db.execSQL(cmd);

    }

    public void createClubTable(String user, SQLiteDatabase db)
    {
        String cmd = "CREATE TABLE IF NOT EXISTS "+user+"_clubs("+KEY_CLUBNAME+" VARCHAR);";
        db.execSQL(cmd);

    }

    public List<Club> loadClubList(String user, SQLiteDatabase db)
    {
        String command = "SELECT "+KEY_CLUBNAME+ " FROM "+user+"_clubs ;";
        Cursor cursor = db.rawQuery(command, null);

        List<Club> list = new ArrayList<Club>();
        if(cursor.moveToFirst()) {
            do {
                Club club = new Club();
                club.setName(cursor.getString(0));
                list.add(club);

            }while(cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return list;

    }

    public boolean checkForOriginalProfile(String nameToCheck)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String command = "SELECT "+KEY_NAME+ " FROM "+TABLE_PROFILES+" WHERE "+KEY_NAME+" = '"+nameToCheck+"';";
        Cursor cursor = db.rawQuery(command, null);

        boolean original = true;

        if(cursor.moveToFirst())
        {
            //the name is already there. return false
            original = false;
        }

        cursor.close();
        db.close();

        return original;
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
        String command = "SELECT "+KEY_NAME+ " FROM "+TABLE_PROFILES+" WHERE "+KEY_LASTUSED+" = 1;";
        Cursor cursor = db.rawQuery(command, null);

        Profile profile = new Profile();
        if(cursor.moveToFirst()) {
            String name = cursor.getString(0);
            profile.setName(name);

            //get all of the clubs for that profile
            List<Club> clubsToLoad = loadClubList(name, db);
            profile.setBag(clubsToLoad);


        }
        else
        {
            //no last used profiles, return profile with name none
            profile.setName("none");

        }

        cursor.close();
        db.close();

        return profile;

    }

    public void setLastUsed(String lastProf, SQLiteDatabase db)
    {
        //set all last used to 0
        String command = "UPDATE "+TABLE_PROFILES+" SET "+KEY_LASTUSED+" = 0;";
        db.execSQL(command);

        //set lastProf to 1
        command = "UPDATE "+TABLE_PROFILES+" SET "+KEY_LASTUSED+" = 1 WHERE "+KEY_NAME+" = '"+lastProf+"';";
        db.execSQL(command);


    }

    public List<String> getAllProfileNames()
    {
        SQLiteDatabase db = getReadableDatabase();
        String command = "SELECT "+KEY_NAME+ " FROM "+TABLE_PROFILES+" ;";
        Cursor cursor = db.rawQuery(command, null);

        List<String> list = new ArrayList<String>();
        if(cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            }while(cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return list;

    }

    public List<Club> getClubList(String user)
    {
        SQLiteDatabase db = getReadableDatabase();
        String command = "SELECT "+KEY_CLUBNAME+ " FROM "+user+"_clubs ;";
        Cursor cursor = db.rawQuery(command, null);

        List<Club> list = new ArrayList<Club>();
        if(cursor.moveToFirst()) {
            do {
                Club club = new Club();
                club.setName(cursor.getString(0));
                list.add(club);

            }while(cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return list;

    }

    public void setLastUsed(String lastProf)
    {
        SQLiteDatabase db = getWritableDatabase();

        //set all last used to 0
        String command = "UPDATE "+TABLE_PROFILES+" SET "+KEY_LASTUSED+" = 0;";
        db.execSQL(command);

        //set lastProf to 1
        command = "UPDATE "+TABLE_PROFILES+" SET "+KEY_LASTUSED+" = 1 WHERE "+KEY_NAME+" = '"+lastProf+"';";
        db.execSQL(command);

        db.close();

    }

    public void addClub(String user, Club club)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CLUBNAME, club.getName());

        db.insert(user+"_clubs", null, values);

        db.close();

    }

}
