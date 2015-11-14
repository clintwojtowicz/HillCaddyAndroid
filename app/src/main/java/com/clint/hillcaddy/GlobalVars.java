package com.clint.hillcaddy;

import android.app.Application;

import java.util.List;

/**
 * Created by Clint on 9/1/2015.
 */
public class GlobalVars extends Application
{
    private Profile currentProfile = new Profile();
    private Double ro = Constants.roAirSeaLvl;
    private Boolean backgroundImage = true;
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
            this.ro = db.getRoFromSettings();
        }

        return this.ro;
    }

    public void setRo(Double newRo)
    {
        this.ro = newRo;
        db.saveRoSetting(newRo);
    }

    public void setBackgroundSetting(Boolean setting)
    {
        backgroundImage = setting;
        db.saveBackgroundSetting(Conversion.boolToInt(setting));
    }

    public Boolean getBackgroundSetting()
    {
        if(backgroundImage == null)
        {
            this.backgroundImage = Conversion.intToBool(db.getBackgroundFromSettings());
        }

        return this.backgroundImage;
    }


}
