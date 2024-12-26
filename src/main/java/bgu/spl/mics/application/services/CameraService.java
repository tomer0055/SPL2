package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;

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
    private Camera camera;
    public CameraService(Camera camera) {
        super("Camera_"+camera.getId());
        this.camera = camera;
        
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        this.register();
        this.subscribeBroadcast(TickBroadcast.class,(t) -> {
            //TODO Implement this
            DetectedObject obj = this.camera.detect();
            if( obj!=null){
                //DetectedObjectEvent event =new DetectedObjectEvent(obj);
                //sendEvent(event);
            }
            else{
                //logger?
            }
            //detect objects
            //send DetectObjectsEvent to msgBus
            
        }
        );

        //subscribe to DetectObjectsEvent
        
        // TODO Implement this
    }
}
