package com.clint.hillcaddy;

import android.app.Application;

/**
 * Created by Clint on 9/1/2015.
 */
public class GlobalVars extends Application
{
    private Profile currentProfile = new Profile();
    private Integer elevation = 0;      //TODO: implement ro based on elevation in settings

    public void setCurrentProfile(Profile profile)
    {
        this.currentProfile = profile;
    }

    public Profile getCurrentProfile()
    {
        if (currentProfile == null)
        {
            //TODO: get the last used profile from the database
            this.currentProfile = new Profile();
        }

        return this.currentProfile;

    }



}
