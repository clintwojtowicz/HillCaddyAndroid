package com.clint.hillcaddy;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Clint on 8/31/2015.
 */
public class Profile
{
    private String name;
    private List<Club> bag = new ArrayList<Club>();

    public Profile()
    {
        name = "";
    }

    public Profile(String _name)
    {
        name = _name;
    }

    public void setName(String _name)
    {
        name = _name;
    }

    public String getName()
    {
        return name;
    }

    public void addClubToBag(Club newClub)
    {
        bag.add(newClub);
        //TODO: also add the club to the DB
    }

    public ArrayList<String> getClubNameList()
    {
        ArrayList<String> clubNames = new ArrayList<String>();
        Iterator<Club> iterator = bag.iterator();

        while (iterator.hasNext())
        {
            clubNames.add(iterator.next().getName());
        }

        return clubNames;

    }

    public void setBag(List<Club> clubs)
    {
        bag = clubs;

    }









}
