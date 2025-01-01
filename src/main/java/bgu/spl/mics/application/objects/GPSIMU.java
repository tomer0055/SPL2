package bgu.spl.mics.application.objects;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int CurrentTick;
    private STATUS status;
    private ConcurrentHashMap<Integer,Pose> PoseMap;
    public GPSIMU(String filePath ){
        this.CurrentTick = 0;
        this.status = STATUS.UP;
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            List<Pose> PoseList;
            Type dataType = new TypeToken<List<Pose>>() {
            }.getType();
            PoseList = gson.fromJson(reader, dataType);
            PoseMap = new ConcurrentHashMap<>();
            for(Pose pose : PoseList){
                PoseMap.put(pose.getTime(),pose);
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Pose onTick(){
        CurrentTick++;
        return PoseMap.get(CurrentTick);
    }
}
