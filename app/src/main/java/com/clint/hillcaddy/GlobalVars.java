package com.clint.hillcaddy;

import android.app.Application;

import java.util.List;

/**
 * Created by Clint on 9/1/2015.
 */
public class GlobalVars extends Application
{
    private Profile currentProfile = new Profile();
    private Integer elevation = 0;      //TODO: implement ro based on elevation in settings

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
        if ((currentProfile == null)||(currentProfile.getName() == ""))
        {
            this.currentProfile = db.getLastUsedProfile();
        }

        return this.currentProfile;

    }

    public DatabaseHelper getDB()
    {
        return db;
    }

    public String getCurrentProfileName()
    {
        return this.currentProfile.getName();
    }




}
