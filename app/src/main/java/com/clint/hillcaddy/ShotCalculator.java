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
    public static final Double PI = 3.141592;
    public static final Double C_DRAG_INITIAL = .18;
    public static final Double SPIN_WEIGHT = .00005;
    public static final Double C_LIFT = .000027;
    public static final Double dS_SCALE = 1.0;
    public static final Double dS_COEF = .0005;


    public static final Double roAirSeaLvl = 1.2;
     */

    /*
        x refers to left to right
        y refers to distance down range
        z refers to height

     */

    public static Integer calculateDistance(Shot shot, Integer elevationDiff, Double airDensity)
    {
        Double dt = .001;
        Double area = (Constants.DIAMETER_GB/2) * (Constants.DIAMETER_GB/2) * Constants.PI;     //A = pi* r^2
        Double ro = airDensity;

        //get all of the shot characteristics in SI units for calculations
        Double speed = Conversion.mphToMs(shot.getBallSpeed());
        Double launchAngle = Conversion.degreesToRadians(shot.getLaunchAngle());
        Double backSpin = Conversion.rpmToRadps(shot.getBackSpin() * 1.0);
        Double sideSpin = Conversion.rpmToRadps(shot.getSideSpin() * 1.0);

        Double Cd;
        Double Cl = Constants.C_LIFT;

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

            Cd = getDragCoef(spin.magnitude());     //drag increases with spin due to turbulence

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

            spin = decreaseSpin(spin, dt);
        }

        //return the distance from the origin, not just the y distance
        return (int)Math.round(Math.sqrt(pos.y * pos.y + pos.x * pos.x));
    }

    private static Double getDragCoef(Double spin)
    {
        return Constants.C_DRAG_INITIAL + (Conversion.radpsToRpm(spin) * Constants.SPIN_WEIGHT);
    }

    private static Vector3D getLaunchVector(Double speed, Double launchAngle, Double azimuthAngle)
    {
        return new Vector3D(speed * Math.cos(launchAngle) * Math.tan(azimuthAngle), speed * Math.cos(launchAngle), speed * Math.sin(launchAngle));

    }

    private static Vector3D decreaseSpin(Vector3D spin, Double dt)
    {
        //use exponential expression to decrease spin based on how much the ball is already spinning instead of linear expression
        spin.x = spin.x - ((Constants.dS_SCALE * Math.exp(Constants.dS_COEF * spin.x) - 1) * spin.x) * dt;
        spin.y = spin.y - ((Constants.dS_SCALE * Math.exp(Constants.dS_COEF * spin.y) - 1) * spin.y) * dt;
        spin.z = spin.z - ((Constants.dS_SCALE * Math.exp(Constants.dS_COEF * spin.z) - 1) * spin.z) * dt;

        return spin;
    }

    public static Integer getLandingHeight(Double angleToTargDeg, Integer distanceToTarg)
    {
        //note: distanceToTarg is the hypotenuse of the triangle, it should be the straight line distance given by laser range finder
        Long result = Math.round(distanceToTarg * Math.sin(Conversion.degreesToRadians(angleToTargDeg)));
        return result.intValue();

    }

    public static Integer getDistance(Double angleToTargDeg, Integer distanceToTarg)
    {
        //note: distanceToTarg is the hypotenuse of the triangle, it should be the straight line distance given by laser range finder
        Long result = Math.round(distanceToTarg * Math.cos(Conversion.degreesToRadians(angleToTargDeg)));
        return result.intValue();

    }

    //note that pressure should be sent in Pa, temperature in K, and humidity as a percentage
    public static Double calculateAirDensity(Float pressure, Float temperature, Float relativeHumidity)
    {
        Double temp_cels = temperature - 273.0;

        //vapor pressure = relative humidity * saturation pressure
        Double pressure_vapor = relativeHumidity * 6.1078 * Math.pow(10,((7.5 * temp_cels) / (temp_cels + 237.3)));
        Double pressure_dry = pressure - pressure_vapor;

        return (pressure_dry / (Constants.R_DRYAIR * temperature)) + (pressure_vapor / (Constants.R_WATERVAPOR * temperature));
    }






}
