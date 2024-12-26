package bgu.spl.mics.application.objects;

import java.util.List;

import com.google.gson.annotations.SerializedName;



/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    private String id;
    private int time;
   @SerializedName("cloudPoints")
    private List<List<Double>> coordinates; //list of lists of coordinates Changed to List<List<Double>> from CloudPoint[]
    private String description;
    public TrackedObject(int time,String id,List<List<Double>>coordinates) {
        this.id = id;
        this.time = time;
        this.coordinates = coordinates;
    }
    public TrackedObject(int time,String id,List<List<Double>>coordinates,String description) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.coordinates = coordinates;
    }
    @Override
    public String toString() {
        return "id: " + id + " time: " + time + " coordinates: " + coordinates.toString();
    }
    public String getId() {
        return id;
    }
    public int getTime() {
        return time;
    }
}
