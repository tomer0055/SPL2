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
    int tick=0;
    
    List <DetectedObject> detectedObjects;
    public Camera( int id, int ferequency) {
        this.id = id;
        this.ferequency = ferequency;
        this.status = STATUS.UP;
        this.detectedObjects = new ArrayList<>();
    }
    public DetectedObject detect(){
        tick++;
        DetectedObject detectedObject ;
        if(tick%ferequency==0){
            //find if there is an object with the same time stamp then send it!
            detectedObject = new DetectedObject(id,"object detected");
            detectedObjects.add(detectedObject);
            return detectedObject;
        }
        return null;
        //logger Camera id: " + id + " detected object id: " + detectedObject.getId() + " description: " + detectedObject.getDescription()
    }
    public String getId() {
        return String.valueOf(id);
    }
    


}