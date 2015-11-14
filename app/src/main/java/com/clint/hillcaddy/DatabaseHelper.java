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
    private static final String TABLE_SETTINGS = "settings";

    //profiles table
    private static final String KEY_NAME = "name";
    private static final String KEY_LASTUSED = "lastUsed";

    //shots table
    private static final String KEY_SHOTID = "shotid";
    private static final String KEY_CLUBNAME = "clubName";
    private static final String KEY_BALLSPEED = "ballspeed";
    private static final String KEY_BACKSPIN = "backspin";
    private static final String KEY_SIDESPIN = "sidespin";
    private static final String KEY_ANGLE = "launchAngle";

    //settings table
    private static final String KEY_RO = "ro";
    private static final String KEY_BACKGROUND = "background";


    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //create profile table if it doesn't already exist
        String command = "CREATE TABLE IF NOT EXISTS "+TABLE_PROFILES+"("+KEY_NAME+" VARCHAR, "+KEY_LASTUSED+" INTEGER);";
        db.execSQL(command);

        //create settings table if it doesn't exist
        createSettingsTable();


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //put action for upgrading here

    }

    public boolean addProfile(String user)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean valid = false;

        if(checkForOriginalProfile(user, db))
        {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, user);
            values.put(KEY_LASTUSED, 0);

            db.insert(TABLE_PROFILES, null, values);
            createShotTable(user, db);
            createClubTable(user, db);

            setLastUsed(user, db);

            valid = true;

        }

        db.close();

        return valid;

    }

    public void createShotTable(String user, SQLiteDatabase db)
    {
        String cmd = "CREATE TABLE IF NOT EXISTS "+user+"_shots("+KEY_SHOTID+" INTEGER PRIMARY KEY, "+KEY_CLUBNAME+" VARCHAR, "+KEY_BALLSPEED+" DOUBLE, "+KEY_BACKSPIN+" INTEGER, "+KEY_SIDESPIN+" INTEGER, "+KEY_ANGLE+" DOUBLE);";
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

    public boolean checkForOriginalProfile(String nameToCheck,  SQLiteDatabase db)
    {
        String command = "SELECT "+KEY_NAME+ " FROM "+TABLE_PROFILES+" WHERE "+KEY_NAME+" = '"+nameToCheck+"';";
        Cursor cursor = db.rawQuery(command, null);

        boolean original = true;

        if(cursor.moveToFirst())
        {
            //the name is already there. return false
            original = false;
        }

        cursor.close();

        return original;
    }

    public void removeProfile(String user)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROFILES, KEY_NAME + " = ?", new String[] {user});
        removeShotTable(user, db);
        removeClubTable(user, db);

        db.close();

    }

    public void removeShotTable(String user, SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS " + user + "_shots");

    }

    public void removeClubTable(String user, SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS " + user + "_clubs");

    }

    public void addShot(String user, String club, Shot newShot)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CLUBNAME, club);
        values.put(KEY_BALLSPEED, newShot.getBallSpeed());
        values.put(KEY_BACKSPIN, newShot.getBackSpin());
        values.put(KEY_SIDESPIN, newShot.getSideSpin());
        values.put(KEY_ANGLE, newShot.getLaunchAngle());

        db.insert(user+"_shots", null, values);

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

        db.insert(user + "_clubs", null, values);

        db.close();

    }

    public List<String> getShotsAsStringsWithLabels(String user, String club)
    {
        SQLiteDatabase db = getReadableDatabase();
        String command = "SELECT "+KEY_BALLSPEED+", "+KEY_BACKSPIN+", "+KEY_SIDESPIN+", "+KEY_ANGLE+" FROM "+user+"_shots WHERE "+KEY_CLUBNAME+"= '"+club+"' ;";
        Cursor cursor = db.rawQuery(command, null);

        List<String> list = new ArrayList<String>();
        String temp;
        if(cursor.moveToFirst()) {
            do {
                temp = "Ball Speed: "+ cursor.getString(0) + " Launch Angle: "+ cursor.getString(3) + "\nSide Spin: "+ cursor.getString(2) + " Back Spin: "+ cursor.getString(1);
                list.add(temp);
            }while(cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return list;

    }

    public List<Shot> getShots(String user, String club)
    {
        SQLiteDatabase db = getReadableDatabase();
        String command = "SELECT "+KEY_SHOTID+", "+KEY_BALLSPEED+", "+KEY_BACKSPIN+", "+KEY_SIDESPIN+", "+KEY_ANGLE+" FROM "+user+"_shots WHERE "+KEY_CLUBNAME+"= '"+club+"' ;";
        Cursor cursor = db.rawQuery(command, null);

        List<Shot> list = new ArrayList<Shot>();
        if(cursor.moveToFirst()) {
            do {
                list.add(new Shot(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(0)));
            }while(cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return list;

    }

    public void removeShot(String user, String club, Integer shotID)
    {
        SQLiteDatabase db = getWritableDatabase();
        String command = "DELETE FROM "+user+"_shots WHERE "+KEY_CLUBNAME+"= '"+club+"' AND "+KEY_SHOTID+"= "+shotID+";";
        db.execSQL(command);
        db.close();
    }

    public List<Shot> getShotList(String club, String user)
    {
        SQLiteDatabase db = getReadableDatabase();
        String command = "SELECT "+KEY_SHOTID+", "+KEY_BALLSPEED+", "+KEY_BACKSPIN+", "+KEY_SIDESPIN+", "+KEY_ANGLE+" FROM "+user+"_shots WHERE "+KEY_CLUBNAME+"= '"+club+"' ;";
        Cursor cursor = db.rawQuery(command, null);

        List<Shot> list = new ArrayList<Shot>();
        if(cursor.moveToFirst()) {
            do {
                list.add(new Shot(cursor.getDouble(1), cursor.getInt(2), cursor.getInt(3), cursor.getDouble(4), cursor.getInt(0)));
            }while(cursor.moveToNext());

        }
        else
        {

        }
        cursor.close();
        db.close();
        return list;

    }

    public void removeClub(String user, String club)
    {
        //remove all shots for that club
        SQLiteDatabase db = getWritableDatabase();
        String command = "DELETE FROM "+user+"_shots WHERE "+KEY_CLUBNAME+"= '"+club+"';";
        db.execSQL(command);

        //delete the club from the club list
        command = "DELETE FROM "+user+"_clubs WHERE "+KEY_CLUBNAME+"= '"+club+"';";
        db.execSQL(command);

        db.close();

    }

    public void checkForNullSettings(SQLiteDatabase db)
    {
        String command = "SELECT * FROM "+TABLE_SETTINGS+" ;";
        Cursor cursor = db.rawQuery(command, null);

        if(!cursor.moveToFirst())
        {
            //set default settings
            ContentValues values = new ContentValues();
            values.put(KEY_RO, Constants.roAirSeaLvl);
            values.put(KEY_BACKGROUND, 1);
            db.insert(TABLE_SETTINGS, null, values);

        }

        cursor.close();

    }

    public void saveRoSetting(Double ro)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //save last used ro to settings
        String command = "UPDATE "+TABLE_SETTINGS+" SET "+KEY_RO+"= "+ro.toString()+";";
        db.execSQL(command);
        db.close();

    }

    public Double getRoFromSettings()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String command = "SELECT * FROM "+TABLE_SETTINGS+" ;";
        Cursor cursor = db.rawQuery(command, null);

        Double rv;

        if(cursor.moveToFirst())
        {
            rv = cursor.getDouble(0);
        }
        else
        {
            rv = Constants.roAirSeaLvl;
        }

        cursor.close();
        db.close();
        return rv;

    }

    public void saveBackgroundSetting(Integer background)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //this is a check to make sure the settings table has been created
        //createSettingsTable();

        //save last background image to settings
        String command = "UPDATE "+TABLE_SETTINGS+" SET "+KEY_BACKGROUND+" = "+background.toString()+";";
        db.execSQL(command);
        db.close();

    }

    public Integer getBackgroundFromSettings()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String command = "SELECT * FROM "+TABLE_SETTINGS+" ;";
        Cursor cursor = db.rawQuery(command, null);

        Integer rv;

        if(cursor.moveToFirst())
        {
            rv = cursor.getInt(1);
        }
        else
        {
            rv = 1;
        }

        cursor.close();
        db.close();
        return rv;

    }

    public void createSettingsTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //create settings table if it doesn't exist
        String command = "CREATE TABLE IF NOT EXISTS "+TABLE_SETTINGS+"("+KEY_RO+" DOUBLE, "+KEY_BACKGROUND+" INTEGER);";
        db.execSQL(command);
        checkForNullSettings(db);

    }

}
