package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    private String id;
    private int time;
    private List<List<CloudPoint>> points;

    public StampedCloudPoints(String id, int time, List<List<CloudPoint>> points) {
        this.id = id;
        this.time = time;
        this.points = points;
    }
    @Override
    public String toString() {
        return "id: " + id + " time: " + time + " points: " + points.toString();
    }
    
}
