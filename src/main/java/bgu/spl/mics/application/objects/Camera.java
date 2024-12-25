package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    int id;
    int ferequency;
    STATUS status;
    List <DetectedObject> detectedObjects;
    public Camera( int id, int ferequency,STATUS status) {
        this.id = id;
        this.ferequency = ferequency;
        this.status = status;
        this.detectedObjects = new ArrayList<>();
    }
    public void detect(){
        //TODO Implement this
    }
    


}
