package com.clint.hillcaddy;

/**
 * Created by Clint on 9/1/2015.
 */
public class Conversion
{
    public static Double mphToMs(Double mph)
    {
        return mph * .44704;
    }

    public static Double msToMph(Double ms)
    {
        return ms / .44704;
    }

    public static Double yardToMeter(Double yd)
    {
        return yd * .9144;
    }

    public static Double meterToYard(Double m)
    {
        return m / .9144;
    }

    public static Double degreesToRadians(Double deg)
    {
        return deg * Constants.PI / 180.0;
    }

    public static Double radiansToDegrees(Double rad)
    {
        return rad * 180.0 / Constants.PI;
    }

    public static Double rpmToRadps(Double rpm)
    {
        return rpm * Constants.PI / 30.0;
    }

    public static Double radpsToRpm(Double radps)
    {
        return radps * 30.0 / Constants.PI;
    }

    public static Integer celsiusToFahrenheit(Float tempC)
    {
        return (int)Math.round(tempC * 1.8 + 32);

    }

    public static Float fahrenheitToCelsius(Integer tempF)
    {
        return (float)((tempF - 32) / 1.8);
    }


}
