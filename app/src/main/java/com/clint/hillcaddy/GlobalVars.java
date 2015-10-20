package com.clint.hillcaddy;

import android.app.Application;

import java.util.List;

/**
 * Created by Clint on 9/1/2015.
 */
public class GlobalVars extends Application
{
    private Profile currentProfile = new Profile();
    private Double ro = 1.2;        //default setting for air density is 1.2

    private DatabaseHelper db = new DatabaseHelper(this);

    public void setCurrentProfile(Profile profile)
    {
        this.currentProfile = profile;
    }

    public void setCurrentProfileWithName(String name)
    {
        currentProfile.setName(name);

        List<Club> clubs = db.getClubList(name);
        this.currentProfile.setBag(clubs);

        db.setLastUsed(name);

    }

    public Profile getCurrentProfile()
    {
        if ((currentProfile == null)||(currentProfile.getName().equals("")))
        {
            this.currentProfile = db.getLastUsedProfile();
            this.currentProfile.calculateClubAverages(db, ro);
        }

        return this.currentProfile;

    }

    public DatabaseHelper getDB()
    {
        return db;
    }

    public Double getRo()
    {
        if (ro == null)
        {
            this.ro = Constants.roAirSeaLvl;
        }

        return this.ro;
    }

    public void setRo(Double newRo)
    {
        this.ro = newRo;
    }



}
