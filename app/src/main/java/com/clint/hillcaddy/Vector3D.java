package com.clint.hillcaddy;

/**
 * Created by Clint on 9/15/2015.
 */
public class Vector3D
{
    public Double x;
    public Double y;
    public Double z;

    public Vector3D()
    {
        x = 0.0;
        y = 0.0;
        z = 0.0;

    }

    public Vector3D(Double _x, Double _y, Double _z)
    {
        x = _x;
        y = _y;
        z = _z;
    }

    public Double magnitude()
    {
        return Math.sqrt( x*x + y*y + z*z);

    }



}
