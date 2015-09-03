package com.clint.hillcaddy;

/**
 * Created by Clint on 9/1/2015.
 */
public class Conversion
{
    public Double mphToMs(Float mph)
    {
        return mph * .44704;
    }

    public Double msToMph(Double ms)
    {
        return ms / .44704;
    }

    public Double yardToMeter(Double yd)
    {
        return yd * .9144;
    }

    public Double meterToYard(Double m)
    {
        return m / .9144;
    }

    public Double degreesToRadians(Double deg)
    {
        return deg * Constants.PI / 180.0;
    }

    public Double radiansToDegrees(Double rad)
    {
        return rad * 180.0 / Constants.PI;
    }

    public Double rpmToRadps(Double rpm)
    {
        return rpm * Constants.PI / 30;
    }

    public Double radpsToRpm(Double radps)
    {
        return radps * 30 / Constants.PI;
    }


}
