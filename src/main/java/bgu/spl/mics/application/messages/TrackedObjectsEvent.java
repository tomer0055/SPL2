package bgu.spl.mics.application.messages;


import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.TrackedObject;

public class TrackedObjectsEvent implements Event<List<TrackedObject>> {
    private List<TrackedObject> trackedObjects;
    private Future<List<TrackedObject>> future;
    public TrackedObjectsEvent(List<TrackedObject> objects) {
        this.trackedObjects = objects;
    }
    public List<TrackedObject> getObjects(){
        return trackedObjects;
    }
    public void setFuture(Future<List<TrackedObject>> future) {
        this.future = future;
    }
    public Future<List<TrackedObject>> getFuture() {
        return future;
    }

    
}