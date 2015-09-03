package com.clint.hillcaddy;

/**
 * Created by Clint on 8/31/2015.
 */
public class Club
{
    private String name;
    private Shot medianShot;
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


}
