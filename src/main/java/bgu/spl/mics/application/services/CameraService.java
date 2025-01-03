package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CameraTerminate;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.STATUS;
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
    private HashMap<Integer,Future<StampedDetectedObjects>> futureHashMap = new HashMap<>();
    private List<StampedDetectedObjects> detectedObjects;
    private StatisticalFolder folder;
    private StampedDetectedObjects lastFrame;
    public CameraService(Camera camera,StatisticalFolder folder) {
        super("Camera_"+camera.getId());
        futureHashMap = new HashMap<>();
        this.camera = camera;
        detectedObjects = new ArrayList<>();
        this.folder = folder;
        this.initialize();
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

            time = event.getTick();

            StampedDetectedObjects res = camera.getDetectedObjectsByTime(time);
            //check if error detected
            if(res != null && res.getDetectedObjects().length != 0){
                detectErorr(res);
                detectedObjects.add(res);
               

                DetectObjectsEvent event1 = new DetectObjectsEvent(res,res.getTime());
                Future<StampedDetectedObjects> future = sendEvent(event1);
                System.out.println("CameraService: "+getName()+" detected "+res.getDetectedObjects().length+" objects at time: "+time);
                if(future != null)
                {
                    event1.setFuture(future);

                    addFuture(future);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } 
          
            checkIfSelfTermination();
            this.resolveFutures(time);

        });
        this.subscribeBroadcast(CrashedBroadcast.class, (event)->
        {
            this.sendEvent(new CameraTerminate(lastFrame));
            messageBus.terminateT();
            this.terminate();
        });
       
        this.subscribeEvent(CameraTerminate.class, (event)->
        {
            this.sendEvent(new CameraTerminate(lastFrame));
            messageBus.terminateT();
            this.terminate();
        });
        

        //subscribe to DetectObjectsEvent
        
    }
    private void addFuture(Future<StampedDetectedObjects> future){
        futureHashMap.put(time,future);
    }
    private void resolveFutures(int tick){
        Iterator<Integer> iterator = futureHashMap.keySet().iterator();
        while (iterator.hasNext()) {
            Integer key = iterator.next();
            if(key + camera.getFrequency() <= tick){
                for (StampedDetectedObjects stampedDetectedObjects : detectedObjects) {
                    if(stampedDetectedObjects.getTime()==key)
                    {
                        lastFrame = stampedDetectedObjects;
                        futureHashMap.get(key).resolve(stampedDetectedObjects);
                        iterator.remove();
                    }
                    
                }
            }
        }
    }
    private void  detectErorr(StampedDetectedObjects objs) {
        if (objs == null || objs.getDetectedObjects() == null) {
            System.err.println("Warning: StampedDetectedObjects or DetectedObjects is null");
             
        }
        for (DetectedObject detectedObject : objs.getDetectedObjects()) {
            if (detectedObject != null && detectedObject.getId().equals("ERROR")) {
                this.messageBus.terminateT();
                DetectedObject errorObj = detectedObject;
                camera.setStatus(STATUS.ERROR);
               
                this.sendEvent(new CameraTerminate(lastFrame));
                
                CrashedBroadcast e = new CrashedBroadcast(this,errorObj.getDescription());
                System.out.println("CameraService: "+getName()+" detected error: "+errorObj.getDescription() + " at time: "+time);
                this.sendBroadcast(e);
            }
        }
        
    }
    private void checkIfSelfTermination() {
        if(camera.getStatus() == STATUS.DOWN && futureHashMap.isEmpty())
        {
            System.out.println(futureHashMap.toString());
            System.out.println("CameraService: "+getName()+" terminated in time: "+time);
            this.terminate();
        }
    }
}
