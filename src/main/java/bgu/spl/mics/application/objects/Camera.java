package bgu.spl.mics.application.objects;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    String cameraDATAPath;
    int id;
    int ferequency;
    STATUS status;
    String camera_key;
    public Camera( int id, int ferequency,String camera_key, String cameraDATAPath) {
        this.id = id;
        this.ferequency = ferequency;
        this.status = STATUS.UP;        
        this.cameraDATAPath = cameraDATAPath;
        this.camera_key = camera_key;

    }
    /**
     * Returns a list of DetectedObjects that were detected at the current time.
     * by reading the cameraDATA.json file
     * 
     * @return A list of DetectedObjects that were detected at the current time
     * can be Empty
     */
public StampedDetectedObjects getDetectedObjectsByTime(int currentTime) {
        
        Gson gson = new Gson();
        List<StampedDetectedObjects> resTime= new ArrayList<>();
        try(FileReader reader = new FileReader(cameraDATAPath)){
              JsonObject root = gson.fromJson(reader, JsonObject.class);

            // Extract the specific camera array
            JsonArray cameraArray = root.getAsJsonArray(camera_key);
            if (cameraArray == null) {
                System.out.println(camera_key +" -> was No camera data found in the JSON file.");
            }
            Type dataType = new TypeToken<List<StampedDetectedObjects>>() {}.getType();
            List<StampedDetectedObjects> objs = gson.fromJson(cameraArray, dataType);

            
            for (StampedDetectedObjects obj : objs ) {
                if(obj.getTime()== currentTime){
                    resTime.add(obj);
                }
            }
            if(resTime.size()>1){
                System.err.println("more than one StampedDetectedObjects with the same time");
            }
            if(resTime.size()==1){
                return resTime.get(0);
            }
        }
        catch (IOException e) {
            e.printStackTrace();  
        } 
        return null;
        
    }

    public String getId() {
        return String.valueOf(id);
    }
    public int getFrequency() {
        return ferequency;
    }
    public STATUS getStatus() {
        return status;
    }
    public void setStatus(STATUS status) {
        this.status = status;
    }
    public String getCamera_key() {
        return camera_key;
    }



}