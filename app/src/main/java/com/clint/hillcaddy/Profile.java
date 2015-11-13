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

    public void removeClubFromBag(String targetClub)
    {
        ListIterator<Club> iterator = bag.listIterator();

        while(iterator.hasNext())
        {
            Club currentClub = iterator.next();
            if(currentClub.getName() == targetClub)
            {
                iterator.remove();
                break;
            }
        }

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

    public void calculateClubAverages(DatabaseHelper db, Double airDensity)
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
            currentClub.setAverageDistance(ShotCalculator.calculateDistance(avgShot, 0, airDensity));

            bag.set(currentIndex, currentClub);

        }

        this.sortBagByAvgDist();

    }

    public void calculateClubAverageShot(DatabaseHelper db)
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
            bag.set(currentIndex, currentClub);

        }

    }

    private Shot calculateAverageShot(List<Shot> shots)
    {
        if (shots.isEmpty())
        {
            return new Shot(0.0, 0, 0, 0.0, 0);
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

        return new Shot(avgSpeed, avgSpin, avgSideSpin, avgAngle, 0);



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

    public List<ShotResult> sortShotResultList(List<ShotResult> shotResults)
    {
        Collections.sort(shotResults, new Comparator<ShotResult>() {
            @Override
            public int compare(ShotResult s1, ShotResult s2) {
                return s2.getDistance() - s1.getDistance(); //descending order
            }

        });

        return shotResults;
    }

    public List<ShotResult> getAllDistancesFromTarget(Integer Ydist, Integer Zdist, Double airDensity)
    {
        Iterator<Club> iterator = bag.iterator();

        List<ShotResult> distanceList = new ArrayList<ShotResult>();


        while(iterator.hasNext())
        {
            Club tempClub = iterator.next();
            ShotResult shot = new ShotResult();
            //show distance in yards
            Long distanceYards =  Math.round(Conversion.meterToYard((double)ShotCalculator.calculateDistance(tempClub.getAverageShot(), Zdist, airDensity)));
            shot.setClubName(tempClub.getName());
            shot.setDistance(distanceYards.intValue() - Ydist);
            distanceList.add(shot);

        }

        distanceList = sortShotResultList(distanceList);

        return distanceList;
    }









}
