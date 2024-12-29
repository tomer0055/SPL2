package bgu.spl.mics.application.messages;


import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

public class TrackedObjectsEvent implements Event<TrackedObject> {
    private List<TrackedObject> trackedObjects;
    public TrackedObjectsEvent(List<TrackedObject> objects) {
        this.trackedObjects = objects;
    }
    public List<TrackedObject> getObjects(){
        return trackedObjects;
    }

    
}