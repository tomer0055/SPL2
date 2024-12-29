package bgu.spl.mics.application.services;

import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.TrackedObject;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 *
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents
 * from the PoseService, transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {

    private FusionSlam fusionSlam;
    private int TerminatedServices;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the
     * global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlamService");
        this.fusionSlam = fusionSlam;
        this.TerminatedServices = 0;
    }

    /**
     * Initializes the FusionSlamService. Registers the service to handle
     * TrackedObjectsEvents, PoseEvents, and TickBroadcasts, and sets up
     * callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        this.register();
        this.subscribeEvent(PoseEvent.class, ((poseEvent) -> {
            fusionSlam.updatePoses(poseEvent.getPose());
            fusionSlam = fusionSlam.getInstance();
        }));
        this.subscribeEvent(TrackedObjectsEvent.class, ((TrackedObjectsEvent) -> {
            List<TrackedObject> objects = TrackedObjectsEvent.getObjects();
            for (TrackedObject object : objects) {
                LandMark LandMark = new LandMark(object.getId(), object.getDescription(), object.getCoordinates());
                fusionSlam.updateLandMarks(LandMark);
            }
            fusionSlam = fusionSlam.getInstance();
        }));
        this.subscribeBroadcast(TickBroadcast.class, (tickBroadcast) -> {
            time++;
        });
        this.subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast) -> {
            terminate();
        });
        this.subscribeBroadcast(TerminatedBroadcast.class, (terminatedBroadcast) -> {
            if(messageBus.getMicroServiceMap().isEmpty()){
                //create outfile
            }

        });

    }
}
