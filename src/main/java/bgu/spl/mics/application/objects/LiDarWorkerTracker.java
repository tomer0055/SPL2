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



    public LiDarWorkerTracker(int Id, int frequencys,String filePath) {
        this.Id = Id;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.lastTrackedObjects = new ArrayList<>();
        this.dataBase = LiDarDataBase.getInstance(filePath);
     
    }
    public List<TrackedObject> getLastTrackedObjects() {
        return lastTrackedObjects;
    }
    public int getId() {
        return Id;
    }

  
   public List<TrackedObject> process(StampedDetectedObjects objs)
   {
    if(objs==null)
    {
        return new ArrayList<>();
    }
    int currnetObjTime = objs.getTime();
    DetectedObject[] detectedObjects = objs.getDetectedObjects();
    List<TrackedObject> trackedObjects = new ArrayList<>();
    for (DetectedObject detectedObject : detectedObjects) 
    {
        
        TrackedObject resObj = dataBase.getTrackedObjectByTimeAndId(objs.getTime(), detectedObject.getId());
        if(resObj!=null)
        {
            trackedObjects.add(resObj);
        }
       
    }
    detectError(trackedObjects);
    lastTrackedObjects.addAll(trackedObjects);
 

    return trackedObjects;
   }




   public int getFrequency()
   {
       return frequency;
   }
   private void detectError(List<TrackedObject> obj)
   {
       for(TrackedObject trackedObject: obj)
       {
           if(trackedObject.getId().equals("ERROR"))
           {
               this.status = STATUS.ERROR;
           }
       }
       
   }
    public STATUS getStatus()
    {
         return status;
    }
   
  

}
