package bgu.spl.mics.application.objects;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    private String id;
    private int time;
    @SerializedName("cloudPoints") // This annotation is used to map the JSON key "cloudPoints" to the field "points".
    private List<List<Double>> points;

    public StampedCloudPoints(int time ,String id , List<List<Double>> points) {
        this.id = id;
        this.time = time;
        this.points = points;
    }
    @Override
    public String toString() {
        return  "time: " + time +" id: " + id + " points: " + points.toString();
    }
    public String getId() {
        return id;
    }
    public int getTime() {
        return time;
    }
    public List<List<Double>> getPoints() {
        return points;
    }
    
}
