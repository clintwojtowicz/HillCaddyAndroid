package com.clint.hillcaddy;

/**
 * Created by Clint on 10/1/2015.
 */
public class ShotResult {

    private String clubName;
    private Integer distance;

    public ShotResult()
    {
        clubName = "";
        distance = 0;
    }

    public ShotResult(String _clubName, Integer dist)
    {
        clubName = _clubName;
        distance = dist;
    }

    public void setClubName(String _name)
    {
        clubName = _name;
    }

    public String getClubName()
    {
        return clubName;
    }

    public void setDistance(Integer dist)
    {
        distance = dist;
    }

    public Integer getDistance()
    {
        return distance;
    }

}
