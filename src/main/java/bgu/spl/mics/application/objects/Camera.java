package bgu.spl.mics.application.objects;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    String cameraDATAPath = "../resources/cameraDATA.json";//TODO: change to the correct path
    int id;
    int ferequency;
    STATUS status;
    int tick=0;
    Map <DetectedObject, Integer> detectedObjects;
    public Camera( int id, int ferequency,STATUS status) {
        this.id = id;
        this.ferequency = ferequency;
        this.status = status;
        this.detectedObjects = new HashMap<>();
        
    }


    /**
     * Detects objects in the environment.
     * 
     * @return A list of DetectedObjects that were detected in the environment
     * List can be Empty .
     */
    public List< DetectedObject> detect(){
        List<DetectedObject> toRemove = new ArrayList<>();
        List<DetectedObject> currentDetectedObjects = getDetectedObjectsByTime();
        if(currentDetectedObjects!=null){

        //add the detected objects to the list of detected objects
        for (DetectedObject detectedObject : currentDetectedObjects) {
            detectedObjects.put(detectedObject, this.tick);
        }
        //compare the current time  only return the objects that were detected if current_time - ferequency = time of detection
        //remove the objects that were detected ferequency time ago not sure if we need to remove them from the list of detected objects
        
        for (Map.Entry<DetectedObject, Integer> entry : detectedObjects.entrySet()) {
            if(tick - entry.getValue() == ferequency){
                toRemove.add(entry.getKey());
            }
        }
        for (DetectedObject obj : toRemove) {
            detectedObjects.remove(obj);
        }
    }
        this.tick++;
        return toRemove;

}
    /**
     * Returns a list of DetectedObjects that were detected at the current time.
     * by reading the cameraDATA.json file
     * 
     * @return A list of DetectedObjects that were detected at the current time
     * can be Empty
     */
public List<DetectedObject> getDetectedObjectsByTime(){
        
        Gson gson = new Gson();
        List<StampedDetectedObjects> resTime= new ArrayList<>();
        try(FileReader reader = new FileReader(cameraDATAPath)){
            Type dataType = new TypeToken<List<StampedDetectedObjects>>(){}.getType();
            StampedDetectedObjects[] objs = gson.fromJson(reader, dataType);
            for (StampedDetectedObjects obj : objs ) {
                if(obj.getTime()==tick){
                    resTime.add(obj);
                }
            }
            List<DetectedObject> detectedObjects = resTime.getFirst().getDetectedObjects();
            return detectedObjects;
           
        }
        catch (IOException e) {
            e.printStackTrace();  
        } 
        return null;
        
    }

    public String getId() {
        return String.valueOf(id);
    }

    public int getTime() {
        return tick;
    }
    //for testing
    public void setTick(int tick){
        this.tick = tick;
    }



}