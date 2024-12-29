package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private int Id;
    private int frequency;
    private STATUS status;
    private List<TrackedObject>lastTrackedObjects;
    private LiDarDataBase dataBase;
    private int time;

    public LiDarWorkerTracker(int Id, int frequency) {
        this.Id = Id;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.lastTrackedObjects = new ArrayList<>();
        this.dataBase = LiDarDataBase.getInstance("example input/lidar_data.json");
        this.time = 0;
    }
    public List<TrackedObject> getLastTrackedObjects() {
        return lastTrackedObjects;
    }
    public int getId() {
        return Id;
    }

  
   public List<TrackedObject> process(List<DetectedObject> objs)
   {
    lastTrackedObjects.addAll(dataBase.MatchTrackedObjectToDetobj(objs));
    // Search if there is an Object with current time= time+frequancy
    List<TrackedObject> toRet = new ArrayList<>();
    for (TrackedObject obj : lastTrackedObjects) {
        if (obj.getTime() == time + frequency) {
            toRet.add(obj);
        }
    }
    time++;
    return toRet;
   }
   public int getFrequency()
   {
       return frequency;
   }
   
  

}
