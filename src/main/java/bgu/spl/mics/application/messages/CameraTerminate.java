package bgu.spl.mics.application.messages;


import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

public class CameraTerminate implements  Event<StampedDetectedObjects> {
    
    private  StampedDetectedObjects StampedDetectedObj;
    
    public CameraTerminate( StampedDetectedObjects StampedDetectedObj) {

        this.StampedDetectedObj = StampedDetectedObj;

    }
    public StampedDetectedObjects getStampedObjects(){
        return StampedDetectedObj;
    }
    
}
