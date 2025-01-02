package bgu.spl.mics.application.messages;


import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;
public class LidarTerminated implements Event<List<TrackedObject>> {
        
        private  List<TrackedObject> trackedObjects;
        
        public LidarTerminated( List<TrackedObject> trackedObjects) {
    
            this.trackedObjects = trackedObjects;
    
        }
        public List<TrackedObject> getTrackedObjects(){
            return trackedObjects;
        }
    
}


