package bgu.spl.mics.application.objects;

import java.util.Arrays;



/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    private String id;
    private int time;
    private CloudPoint[] coordinates; 
    private String description;
    public TrackedObject(int time,String id,CloudPoint[]coordinates,String description) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.coordinates = coordinates;
    }
    @Override
    public String toString() {
        return "id: " + id + " time: " + time + " coordinates: " + Arrays.toString(coordinates);
    }
    public String getId() {
        return id;
    }
    public int getTime() {
        return time;
    }

    public void setDescription(String description) {
        this.description = description;

    public CloudPoint[] getCoordinates() {
        return coordinates;
    }
    public String getDescription() {
        return description;

    }
}
