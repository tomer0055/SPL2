package bgu.spl.mics.application.messages;


import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;

public class DetectObjectsEvent implements Event {
    private List<DetectedObject> detectedObjects;
    private int time;

    public DetectObjectsEvent(List<DetectedObject> objects, int time) {
        this.detectedObjects = objects;
        this.time = time;
    }
    public List<DetectedObject> getObjects(){
        return detectedObjects;
    }
    public int getTime(){
        return time;
    }
}
