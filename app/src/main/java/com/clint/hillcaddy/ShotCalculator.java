package com.clint.hillcaddy;

/**
 * Created by Clint on 9/12/2015.
 */

public class ShotCalculator
{
    /*  These are the names and values of the constants, for reference

    public static final Double MASS_GB = .04593;
    public static final Double DIAMETER_GB = .0427;
    public static final Double GRAVITY = 9.8;
    public static final Double PI = 3.1417;
    public static final Double C_DRAG = .38;
    public static final Double C_LIFT = .0000075;
    public static final Double dSpin_dt = .1;

    public static final Double roAirSeaLvl = 1.0;
     */

    /*
        x refers to left to right
        y refers to distance down range
        z refers to height

     */

    public static Integer calculateDistance(Shot shot, Integer elevationDiff)
    {
        Double dt = .001;
        Double area = (Constants.DIAMETER_GB/2) * (Constants.DIAMETER_GB/2) * Constants.PI;     //A = pi* r^2
        Double ro = getRoFromElevation(0);

        //get all of the shot characteristics in SI units for calculations
        Double speed = Conversion.mphToMs(shot.getBallSpeed());
        Double launchAngle = Conversion.degreesToRadians(shot.getLaunchAngle());
        Double backSpin = Conversion.rpmToRadps(shot.getBackSpin() * 1.0);
        Double sideSpin = Conversion.rpmToRadps(shot.getSideSpin() * 1.0);

        Double Cd = getDragCoef(speed);
        Double Cl = getLiftCoef(speed);

        //this is used to reduce the redundant amount of math done in the while loop to improve efficiency
        Double dragConstants = .5 * area * ro / Constants.MASS_GB;


        //vectors that will be used to calculate position
        Vector3D velocity = getLaunchVector(speed, launchAngle, 0.0);
        Vector3D accel = new Vector3D();
        Vector3D pos = new Vector3D();
        Vector3D spin = new Vector3D(backSpin, 0.0, sideSpin);
        Vector3D magnusAccel = new Vector3D();

        //check to see if ball has reached its desired elevation and is traveling downwards (i.e. velocity.z is negative)
        while(((pos.z > elevationDiff) && velocity.z < 0) || velocity.z >= 0)
        {
            pos.x = pos.x + velocity.x * dt;
            pos.y = pos.y + velocity.y * dt;
            pos.z = pos.z + velocity.z * dt;

            // magnus accel = spin coefficient * (spin X velocity) / Mass
            magnusAccel.x = Cl * (spin.y * velocity.z - spin.z * velocity.y) / Constants.MASS_GB;
            magnusAccel.y = Cl * -(spin.x * velocity.z - spin.z * velocity.x)/ Constants.MASS_GB;
            magnusAccel.z = Cl * (spin.x * velocity.y - spin.y * velocity.x)/ Constants.MASS_GB;

            accel.x = (-dragConstants * Cd * velocity.x * Math.abs(velocity.x)) + magnusAccel.x;
            accel.y = (-dragConstants * Cd * velocity.y * Math.abs(velocity.y)) + magnusAccel.y;
            accel.z = (-dragConstants * Cd * velocity.z * Math.abs(velocity.z)) + magnusAccel.z - Constants.GRAVITY;

            velocity.x = velocity.x + accel.x * dt;
            velocity.y = velocity.y + accel.y * dt;
            velocity.z = velocity.z + accel.z * dt;

            //this needs to be implemented. For now, just let it stay constant
            //spin = decreaseSpin(spin, dt);

            Cd = getDragCoef(velocity.magnitude());
            Cl = getLiftCoef(velocity.magnitude());

        }

        return (int)Math.round(pos.y);
    }

    private static Double getDragCoef(Double speed)
    {
        //TODO: this needs to be a function of speed

        return Constants.C_DRAG;
    }

    private static Double getLiftCoef(Double speed)
    {
        //TODO: make this a function of speed

        return Constants.C_LIFT;
    }

    private static Double getRoFromElevation(Integer elevation)
    {
        //TODO: implement ro based on elevation

        return Constants.roAirSeaLvl;
    }

    private static Vector3D getLaunchVector(Double speed, Double launchAngle, Double azimuthAngle)
    {
        return new Vector3D(speed * Math.cos(launchAngle) * Math.tan(azimuthAngle), speed * Math.cos(launchAngle), speed * Math.sin(launchAngle));

    }

    private static Vector3D decreaseSpin(Vector3D spin, Double dt)
    {
        Double deltaS = Constants.dSpin_dt * dt;

        if (spin.x != 0.0) spin.x = spin.x - deltaS;
        if (spin.y != 0.0) spin.y = spin.y - deltaS;
        if (spin.z != 0.0) spin.z = spin.z - deltaS;

        return spin;
    }






}
