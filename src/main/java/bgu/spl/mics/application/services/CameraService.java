package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    private final Camera camera;
    private int currentTick;
    private HashMap<Integer,Future<StampedDetectedObjects>> futureHashMap = new HashMap<>();
    private List<StampedDetectedObjects> detectedObjects;
    private StatisticalFolder folder;
    public CameraService(Camera camera,StatisticalFolder folder) {
        super("Camera_"+camera.getId());
        this.camera = camera;
        this.currentTick = 0;
        detectedObjects = new ArrayList<>();
        this.folder = folder;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        this.register();
        this.subscribeBroadcast(TickBroadcast.class, (event)->{
            StampedDetectedObjects res = camera.getDetectedObjectsByTime(currentTick+camera.getFrequency());
            if(res != null && res.getDetectedObjects().length != 0){
                detectedObjects.add(res);
                for (DetectedObject detectedObject : res.getDetectedObjects()) {
                    folder.incrementDetectedObjects();
                }
                DetectObjectsEvent event1 = new DetectObjectsEvent(res,res.getTime());
                Future<StampedDetectedObjects> future = sendEvent(event1);
                this.addFuture(future);
            } else {

            }
            this.resoloceFutures(currentTick);
            currentTick++;
        });
        this.subscribeBroadcast(CrashedBroadcast.class, (event)->
        {
            this.terminate();
        });
        

        //subscribe to DetectObjectsEvent
        
    }
    private void addFuture(Future<StampedDetectedObjects> future){
        futureHashMap.put(currentTick,future);
    }
    private void resoloceFutures(int tick){
       for(int i = 0; i < futureHashMap.size(); i++){
           if(futureHashMap.containsKey(i)&& i+camera.getFrequency() <= tick){
            StampedDetectedObjects objs = camera.getDetectedObjectsByTime(i);
               futureHashMap.get(i).resolve(objs);
           }
       }
    }
}
