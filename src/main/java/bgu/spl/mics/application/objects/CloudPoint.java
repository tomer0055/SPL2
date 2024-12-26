package bgu.spl.mics.application.objects;

/**
 * CloudPoint represents a specific point in a 3D space as detected by the LiDAR.
 * These points are used to generate a point cloud representing objects in the environment.
 */
public class CloudPoint {
    private double  x;
    private double y;

    public CloudPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public String toString() 
    {
        return "x: " + x + " y: " + y;
    }
    
}
