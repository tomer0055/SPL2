package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.Pose;

/**
 * PoseService is responsible for maintaining the robot's current pose (position
 * and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    private GPSIMU gpsimu;

    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the
     * current pose.
     */
    @Override
    protected void initialize() {
        register();
        this.subscribeBroadcast(TickBroadcast.class, (tickBroadcast) -> {
            time++;
            Pose pose = gpsimu.onTick();
            if (pose != null) {

                PoseEvent event = new PoseEvent(pose);
                this.sendEvent(event);
            }
        });
        this.subscribeBroadcast(TerminatedBroadcast.class, (TerminateBroadcast) -> {
            this.terminate();
        });
        this.subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast) -> {
            // TODO Implement this
        });
    }
}
