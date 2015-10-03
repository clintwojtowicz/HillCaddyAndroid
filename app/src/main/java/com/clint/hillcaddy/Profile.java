package com.clint.hillcaddy;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

    public void calculateClubAverages(DatabaseHelper db)
    {
        ListIterator<Club> iterator = bag.listIterator();

        while(iterator.hasNext())
        {
            //get shot list by passing club and profile name
            Integer currentIndex = iterator.nextIndex();
            Club currentClub = iterator.next();
            List<Shot> shotList = db.getShotList(currentClub.getName(), this.name);
            Shot avgShot = calculateAverageShot(shotList);

            //set average shot and average distance for each club
            currentClub.setAverageShot(avgShot);
            currentClub.setAverageDistance(ShotCalculator.calculateDistance(avgShot, 0));

            bag.set(currentIndex, currentClub);

        }

        this.sortBagByAvgDist();

    }

    private Shot calculateAverageShot(List<Shot> shots)
    {
        if (shots.isEmpty())
        {
            return new Shot(0.0, 0, 0, 0.0);
        }
        Iterator<Shot> iterator = shots.iterator();
        Double speedSum = 0.0;
        Integer spinSum = 0;
        Integer sideSpinSum = 0;
        Double angleSum = 0.0;
        Integer count = 0;

        while(iterator.hasNext())
        {
            Shot tempShot = iterator.next();

            speedSum += tempShot.getBallSpeed();
            spinSum += tempShot.getBackSpin();
            sideSpinSum += tempShot.getSideSpin();
            angleSum += tempShot.getLaunchAngle();
            count++;
        }

        Double avgSpeed = speedSum / count;
        Integer avgSpin = spinSum / count;
        Integer avgSideSpin = sideSpinSum / count;
        Double avgAngle = angleSum / count;

        return new Shot(avgSpeed, avgSpin, avgSideSpin, avgAngle);



    }

    public List<ShotResult> getClubDistances()
    {
        Iterator<Club> iterator = bag.iterator();

        List<ShotResult> distanceList = new ArrayList<ShotResult>();


        while(iterator.hasNext())
        {
            Club tempClub = iterator.next();
            ShotResult shot = new ShotResult();
            //show distance in yards
            Long distanceYards =  Math.round(Conversion.meterToYard((double)tempClub.getAverageDistance()));
            shot.setClubName(tempClub.getName());
            shot.setDistance(distanceYards.intValue());
            distanceList.add(shot);

        }

        return distanceList;
    }

    public void sortBagByAvgDist()
    {
        Collections.sort(bag, new Comparator<Club>() {
            @Override
            public int compare(Club c1, Club c2) {
                return c2.getAverageDistance() - c1.getAverageDistance(); //descending order
            }

        });

    }

    public List<ShotResult> getAllDistancesFromTarget(Integer Ydist, Integer Zdist)
    {
        Iterator<Club> iterator = bag.iterator();

        List<ShotResult> distanceList = new ArrayList<ShotResult>();


        while(iterator.hasNext())
        {
            Club tempClub = iterator.next();
            ShotResult shot = new ShotResult();
            //show distance in yards
            Long distanceYards =  Math.round(Conversion.meterToYard((double)ShotCalculator.calculateDistance(tempClub.getAverageShot(), Zdist)));
            shot.setClubName(tempClub.getName());
            shot.setDistance(distanceYards.intValue() - Ydist);
            distanceList.add(shot);

        }

        return distanceList;
    }









}
