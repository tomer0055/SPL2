package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
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
    public CameraService(Camera camera,StatisticalFolder folder) {
        super("Camera_"+camera.getId());
        this.camera = camera;
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
            StampedDetectedObjects res = camera.getDetectedObjectsByTime(time+camera.getFrequency());
            //check if error detected
            
            DetectedObject errorObj= detectErorr(res);
            if(res != null && errorObj!=null){
                CrashedBroadcast e = new CrashedBroadcast(this,camera.getCamera_key()+" "+errorObj.getDescription());
                this.sendBroadcast(e);
            }

            if(res != null && res.getDetectedObjects().length != 0){
                detectedObjects.add(res);
                for (DetectedObject detectedObject : res.getDetectedObjects()) {
                    folder.incrementDetectedObjects();
                }
                DetectObjectsEvent event1 = new DetectObjectsEvent(res,res.getTime());
                Future<StampedDetectedObjects> future = sendEvent(event1);
                if(future != null)
                {
                    event1.setFuture(future);
                    addFuture(future);
                }
                
            } else {

            }
            this.resolveFutures(time);
            time++;
        });
        this.subscribeBroadcast(CrashedBroadcast.class, (event)->
        {
            this.terminate();
        });
        this.subscribeBroadcast(TerminatedBroadcast.class, (event)->
        {
            if(messageBus.getMicroServiceMap().isEmpty())
            {
                //folder.writeToFile();
                this.terminate();
            }
        });
        

        //subscribe to DetectObjectsEvent
        
    }
    private void addFuture(Future<StampedDetectedObjects> future){
        futureHashMap.put(time,future);
    }
    private void resolveFutures(int tick){
       for(int i = 0; i < futureHashMap.size(); i++){
           if(futureHashMap.containsKey(i)&& i+camera.getFrequency() <= tick){
            StampedDetectedObjects objs = camera.getDetectedObjectsByTime(i);
               futureHashMap.get(i).resolve(objs);
               futureHashMap.remove(i);
               
           }
       }
    }
    private DetectedObject detectErorr(StampedDetectedObjects objs){
        for (DetectedObject detectedObject : objs.getDetectedObjects()) {
            if(detectedObject.getId().equals("ERROR") ){
                camera.setStatus(STATUS.ERROR);
                return detectedObject;
            }
        }
        return null;
    }
}
