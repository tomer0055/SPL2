package bgu.spl.mics.application.services;


import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.application.objects.StatisticalFolder;


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
    private final Queue<DetectObjectsEvent> pendingEvents = new ConcurrentLinkedQueue<>();
    private HashMap<Integer,Future<List<TrackedObject>>> futureHashMap = new HashMap<>();
    private Queue<List<TrackedObject>> trackedObjects = new ConcurrentLinkedQueue<>();
    StatisticalFolder statisticalFolder;
    public LiDarService(LiDarWorkerTracker liDarTracker,StatisticalFolder statisticalFolder) {
        super("Lidar"+liDarTracker.getId());
        this.liDarTracker = liDarTracker;
        this.currentTick = 0;
        this.statisticalFolder = statisticalFolder;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        this.register();
        this.subscribeEvent(DetectObjectsEvent.class, (event)->{
            pendingEvents.add(event);
          



            
        });
        this.subscribeBroadcast(TickBroadcast.class, (event)->
        {
            for(DetectObjectsEvent obj: pendingEvents)
            {
                if(obj.getFuture().isDone())
                {
                    pendingEvents.remove(obj);
                    List<TrackedObject> tr = liDarTracker.process(obj.getFuture().resultNow());
                    TrackedObjectsEvent e = new TrackedObjectsEvent(tr);
                    trackedObjects.add(tr);
                    Future<List<TrackedObject>> f = this.sendEvent(e);
                    e.setFuture(f);
                    this.addFuture(f);
                }
            }
           resolveFutures(currentTick);
           currentTick++;
        });
        this.subscribeBroadcast(CrashedBroadcast.class, (event)->
        {
            this.terminate();
        });

    }
    private void addFuture(Future<List<TrackedObject>> f) {
        futureHashMap.put(currentTick, f);
    }
    private void resolveFutures(int tick) {
        for(int i = 0; i < futureHashMap.size(); i++) {
            if(futureHashMap.containsKey(i) && i + liDarTracker.getFrequency() <= tick) {
                List<TrackedObject> tr = trackedObjects.remove();
                futureHashMap.get(i).resolve(tr);
                futureHashMap.remove(i);
            }
        }
    }
        //subscribe to DetectObjectsEvent
        //subscribe to TickBroadcast
    }


