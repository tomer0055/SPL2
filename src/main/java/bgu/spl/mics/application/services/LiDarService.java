package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;

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
    public LiDarService(LiDarWorkerTracker liDarTracker) {
        super("Lidar"+liDarTracker.getId());
        this.liDarTracker = liDarTracker;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        this.register();
        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast t) -> {
            //TODO: implement


            
        });
        this.subscribeEvent(DetectObjectsEvent.class, (event)  -> {
            DetectObjectsEvent detectEvent = (DetectObjectsEvent) event;
            liDarTracker.process(detectEvent.getObjects());
         });
        //subscribe to DetectObjectsEvent
        //subscribe to TickBroadcast
    }
}
