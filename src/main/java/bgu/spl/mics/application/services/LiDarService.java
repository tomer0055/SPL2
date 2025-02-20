package bgu.spl.mics.application.services;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.LidarTerminated;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
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
    private final Queue<DetectObjectsEvent> pendingEvents = new ConcurrentLinkedQueue<>();
    private HashMap<Integer,Future<List<TrackedObject>>> futureHashMap = new HashMap<>();
    private Queue<List<TrackedObject>> trackedObjects = new ConcurrentLinkedQueue<>();
    private StatisticalFolder statisticalFolder;
    private List<TrackedObject> lastTrackedObjects;
    public LiDarService(LiDarWorkerTracker liDarTracker,StatisticalFolder statisticalFolder) {
        super("Lidar"+liDarTracker.getId());
        this.liDarTracker = liDarTracker;
        this.statisticalFolder = statisticalFolder;
        this.initialize();
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected  void  initialize() {
        this.register();

        //System.out.println(getName() + " subscribed to DetectObjectsEvent");            
        this.subscribeEvent(DetectObjectsEvent.class, (event)->{
            pendingEvents.add(event);
           // System.out.println(getName() + " Recived Detected Object on time " +event.getTime());            


        });
        this.subscribeBroadcast(TickBroadcast.class, (event)->
        {
            time = event.getTick();

            Iterator<DetectObjectsEvent> iterator = pendingEvents.iterator();
            while(iterator.hasNext())
            {
                DetectObjectsEvent obj = iterator.next();
                if(obj.getFuture().isDone())
                {
                    StampedDetectedObjects givenObjs=obj.getFuture().get();
                    for(DetectedObject temp: givenObjs.getDetectedObjects())
                    {
                        statisticalFolder.incrementDetectedObjects();
                    }
                    iterator.remove();
                    List<TrackedObject> tr = liDarTracker.process(givenObjs);
                   
                    if(liDarTracker.getStatus() == STATUS.ERROR)
                    {
                        messageBus.terminateT();
                        this.sendEvent(new LidarTerminated(lastTrackedObjects));
                        CrashedBroadcast e = new CrashedBroadcast(this);
                        this.sendBroadcast(e);
                        //System.out.println("LiDarService: "+getName()+" detected error: "+" at time: "+time);
                        this.terminate();
                    }
                    TrackedObjectsEvent e = new TrackedObjectsEvent(tr);
                    trackedObjects.add(tr);
                    Future<List<TrackedObject>> f =  this.sendEvent(e);
                   // System.out.println("LiDarService: "+getName()+" detected "+tr.size()+" objects at time: "+time);
                    e.setFuture(f);
                    this.addFuture(f);
                    messageBus.complete(obj,givenObjs);

                }
                
            }
            
            checkIfSelfTermination();
            resolveFutures(time);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        //
        this.subscribeBroadcast(CrashedBroadcast.class, (event)->
        {
            this.sendEvent(new LidarTerminated(lastTrackedObjects));
            this.terminate();
        });
        

    }
    private void addFuture(Future<List<TrackedObject>> f) {
        futureHashMap.put(time, f);
    }
    private void resolveFutures(int tick) {
        Iterator<Integer> iterator = futureHashMap.keySet().iterator();
        while(iterator.hasNext()) {
            Integer key = iterator.next();
            if(key + liDarTracker.getFrequency() <= tick) {
                List<TrackedObject> t = trackedObjects.poll();
                lastTrackedObjects=t;
                futureHashMap.get(key).resolve(t);
                iterator.remove();
            }
        }
    }
    private void checkIfSelfTermination() {
        if(liDarTracker.getStatus() == STATUS.DOWN&&futureHashMap.isEmpty())
        {
            //System.out.println(futureHashMap.toString());
            //System.out.println("LiDarService: "+getName()+" is terminating in time: "+time);
            this.terminate();
        }
    }
    }


