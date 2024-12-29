package bgu.spl.mics.application.messages;


import java.util.concurrent.Future;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

public class DetectObjectsEvent implements Event<StampedDetectedObjects> {
    private final  StampedDetectedObjects detectedObjects;
    private final  int time;
    private  Future<StampedDetectedObjects> future = null;

    public DetectObjectsEvent(StampedDetectedObjects objects, int time) {
        this.detectedObjects = objects;
        this.time = time;
    }
    public StampedDetectedObjects getObjects(){
        return detectedObjects;
    }
    public int getTime(){
        return time;
    }
    public Future<StampedDetectedObjects> getFuture(){
        return future;
    }
    public void setFuture(Future<StampedDetectedObjects> future){
        this.future = future;
    }
}
