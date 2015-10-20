package com.clint.hillcaddy;

/**
 * Created by Clint on 8/31/2015.
 */
public class Shot
{
    private Double ballSpeed;
    private Integer backSpin;
    private Integer sideSpin;
    private Double launchAngle;
    private Integer id;

    public Shot(Double _ballSpeed, Integer _backSpin, Integer _sideSpin, Double _launchAngle, Integer _id)
    {
        ballSpeed = _ballSpeed;
        backSpin = _backSpin;
        sideSpin = _sideSpin;
        launchAngle = _launchAngle;
        id = _id;

    }

    public Shot(String _ballSpeed, String _backSpin, String _sideSpin, String _launchAngle, String _id)
    {
        ballSpeed = Double.parseDouble(_ballSpeed);
        backSpin = Integer.parseInt(_backSpin);
        sideSpin = Integer.parseInt(_sideSpin);
        launchAngle = Double.parseDouble(_launchAngle);
        id = Integer.parseInt(_id);

    }

    public void setBallSpeed(Double bs)
    {
        ballSpeed = bs;
    }

    public void setBackSpin(Integer bs)
    {
        backSpin = bs;
    }

    public void setSideSpin(Integer ss) { sideSpin = ss; }

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

    public Integer getSideSpin() { return sideSpin; }

    public Double getLaunchAngle()
    {
        return launchAngle;
    }

    public Integer getID() { return id; }

}
