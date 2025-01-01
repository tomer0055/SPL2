package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private String id;
    private String Description;
    private List<CloudPoint> points;
    public LandMark(String id, String Description, CloudPoint[] points){
        this.id = id;
        this.Description = Description;
        this.points = new LinkedList<CloudPoint>();
        for (CloudPoint point : points){//TODO: check if this is the right way to add points
            this.points.add(point);
        }
    }
    public String getId(){
        return id;
    }
    public String getDescription(){
        return Description;
    }
    public List<CloudPoint> getPoints(){
        return points;
    }
    public void updateLocation(CloudPoint[] newPoint){
        points.clear();
        for (CloudPoint point : newPoint){
            points.add(point);
        }
    }


}
