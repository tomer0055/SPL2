package bgu.spl.mics.application.objects;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
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
    public Camera( int id, int ferequency,STATUS status, String cameraDATAPath) {
        this.id = id;
        this.ferequency = ferequency;

        this.status = status;        
        this.cameraDATAPath = cameraDATAPath;

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
            Type dataType = new TypeToken<List<StampedDetectedObjects>>(){}.getType();
            StampedDetectedObjects[] objs = gson.fromJson(reader, dataType);
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



}