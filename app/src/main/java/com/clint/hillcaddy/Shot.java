package com.clint.hillcaddy;

/**
 * Created by Clint on 8/31/2015.
 */
public class Shot
{
    private Double ballSpeed;
    private Integer backSpin;
    private Double launchAngle;

    private Integer distance;

    public Shot(Double _ballSpeed, Integer _backSpin, Double _launchAngle)
    {
        ballSpeed = _ballSpeed;
        backSpin = _backSpin;
        launchAngle = _launchAngle;

    }

    public void setBallSpeed(Double bs)
    {
        ballSpeed = bs;
    }

    public void setBackSpin(Integer bs)
    {
        backSpin = bs;
    }

    public void setLaunchAngle(Double la)
    {
        launchAngle = la;
    }

    public Double getBallSpeed()
    {
        return ballSpeed;
    }

    public Integer getBackSpin()
    {
        return backSpin;
    }

    public Double getLaunchAngle()
    {
        return launchAngle;
    }

    public void calculateDistance()
    {



    }

}
