package bgu.spl.mics.application.services;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.StatisticalFolder;
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
    private StatisticalFolder statisticalFolder;
    private Queue<TrackedObjectsEvent> pendingEvents = new ConcurrentLinkedQueue<>();

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the
     *                   global map.
     */
    public FusionSlamService(FusionSlam fusionSlam, StatisticalFolder statisticalFolder) {
        super("FusionSlamService");
        this.fusionSlam = fusionSlam;
        this.TerminatedServices = 0;
        this.statisticalFolder = statisticalFolder;
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
            System.out.println(getName() + " subscribed to PoseEvent");

            fusionSlam.updatePoses(poseEvent.getPose());
            fusionSlam = fusionSlam.getInstance();
        }));
        this.subscribeEvent(TrackedObjectsEvent.class, ((TrackedObjectsEvent) -> {
            System.out.println(getName() + " subscribed to TrackedObjectsEvent");

            pendingEvents.add(TrackedObjectsEvent);
        }));
        this.subscribeBroadcast(TickBroadcast.class, (tickBroadcast) -> {
            for (TrackedObjectsEvent event : pendingEvents) {
                if(event.getFuture().isDone()){
                    List<TrackedObject> objects = event.getObjects();
                    pendingEvents.remove(event);
                    for (TrackedObject object : objects) {
                        LandMark LandMark = new LandMark(object.getId(), object.getDescription(), object.getCoordinates());
                        fusionSlam.updateLandMarks(LandMark);
                    }
                    fusionSlam = fusionSlam.getInstance();
                    complete(event, objects);
                }
            }
            time++;
            
        });

        this.subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast) -> {
            terminate();
        });
        this.subscribeBroadcast(TerminatedBroadcast.class, (terminatedBroadcast) -> {
            if (messageBus.getMicroServiceMap().isEmpty()) {
                // create outfile
                Map<String, Integer> statistics = Map.of(
                        "systemRuntime", statisticalFolder.getRuntime(),
                        "numDetectedObjects", statisticalFolder.getNumDetectedObjects(),
                        "numTrackedObjects", statisticalFolder.getNumTrackedObjects(),
                        "numLandmarks", statisticalFolder.getNumLandmarks());
                List<Map<String, Object>> landmarks = new ArrayList<>();
                for (LandMark landmark : fusionSlam.getLandMarks()) {
                    landmarks.add(Map.of(
                            "id", landmark.getId(),
                            "description", landmark.getDescription(),
                            "coordinates", List.of(landmark.getPoints())));
                }
                Map<String, Object> output = Map.of(
                        "statistics", statistics,
                        "landMarks", landmarks);
                String outputPath = "output_file.json"; // Output file path
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                try (FileWriter writer = new FileWriter(outputPath)) {
                    gson.toJson(output, writer);
                    System.out.println("Output file written to: " + outputPath);
                } catch (IOException e) {
                    System.err.println("Error writing the output file: " + e.getMessage());
                }
            }

        });
    }
}
