package bgu.spl.mics.application.services;


import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.TrackedObject;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {

    /**
     * Constructor for LiDarService.
     *
     * @param liDarTracker The LiDAR tracker object that this service will use to process data.
     */
    LiDarWorkerTracker liDarTracker;
    private int currentTick;
    public LiDarService(LiDarWorkerTracker liDarTracker) {
        super("Lidar"+liDarTracker.getId());
        this.liDarTracker = liDarTracker;
        this.currentTick = 0;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        this.register();
            // Handle TickBroadcast to synchronize processing with system time
            this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast t) -> {
                
                if (currentTick % liDarTracker.getFrequency() == 0) {
                    System.out.println(getName() + " is processing at tick " + currentTick);
                }
                currentTick++;
            });
           this.subscribeEvent(DetectObjectsEvent.class, ( e) -> {
            DetectObjectsEvent event = (DetectObjectsEvent) e;
            List<TrackedObject> trackedObjects = liDarTracker.process(event.getObjects());
            if (trackedObjects != null && !trackedObjects.isEmpty()) {
                TrackedObjectsEvent trackedObjectsEvent = new TrackedObjectsEvent(trackedObjects);
                sendEvent(trackedObjectsEvent);


                // Update the StatisticalFolder with tracking data
                
                // StatisticalFolder stats = StatisticalFolder.getInstance();
                
                // stats.incrementNumTrackedObjects(trackedObjects.size());
            }
            
        });
        //subscribe to DetectObjectsEvent
        //subscribe to TickBroadcast
    }
}
