package bgu.spl.mics.application.messages;


import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;

public class CameraTerminate implements  Event<List<DetectedObject>> {
    
    private  List<DetectedObject> detectedObjects;
    
    public CameraTerminate( List<DetectedObject> detectedObjects) {

        this.detectedObjects = detectedObjects;

    }
    public List<DetectedObject> getDetectedObjects(){
        return detectedObjects;
    }
    
}
