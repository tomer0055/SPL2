package bgu.spl.mics.application.messages;


import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
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
    public void setFuture(bgu.spl.mics.Future<StampedDetectedObjects> future2){
        this.future = future2;
    }
}
