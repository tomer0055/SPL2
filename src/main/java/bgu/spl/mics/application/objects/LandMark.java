package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private String id;
    private String Description;
    private List<CloudPoint> points;
    public LandMark(String id, String Description, List<CloudPoint> points){
        this.id = id;
        this.Description = Description;
        this.points = points;
    }

}
