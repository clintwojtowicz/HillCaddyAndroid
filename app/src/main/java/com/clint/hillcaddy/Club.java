package com.clint.hillcaddy;

/**
 * Created by Clint on 8/31/2015.
 */
public class Club
{
    private String name;
    private Shot averageShot;
    private Integer averageDistance;

    public Club()
    {
        name = "";
    }

    public Club(String _name)
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

    public void setAverageShot(Shot avgShot)
    {
        averageShot = avgShot;
    }

    public Shot getAverageShot()
    {
        return averageShot;
    }

    public void setAverageDistance(Integer dist)
    {
        averageDistance = dist;
    }

    public Integer getAverageDistance()
    {
        return averageDistance;

    }





}
