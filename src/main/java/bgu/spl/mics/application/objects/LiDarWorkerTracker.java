package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private int Id;
    private int frequency;
    private STATUS status;
    private List<TrackedObject>lastTrackedObjects;

    public LiDarWorkerTracker(int Id, int frequency,STATUS status) {
        this.Id = Id;
        this.frequency = frequency;
        this.status = status;
        this.lastTrackedObjects = new ArrayList<>();
    }

}
