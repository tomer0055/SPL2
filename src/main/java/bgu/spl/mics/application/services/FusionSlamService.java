package bgu.spl.mics.application.services;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CameraTerminate;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.LidarTerminated;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
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
    private StatisticalFolder statisticalFolder;
    private Queue<TrackedObjectsEvent> pendingEvents = new ConcurrentLinkedQueue<>();
    private List<TrackedObject> LastTrackedObject;
    private StampedDetectedObjects LastStampedDetectedObject;
    private Map<String, Object> output;
    private String error;
    private String faultySensor;
    private List<Pose> poses;
    private String outputFolder;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the
     *                   global map.
     */
    public FusionSlamService(FusionSlam fusionSlam, StatisticalFolder statisticalFolder, String outputFolder) {
        super("FusionSlamService");
        this.fusionSlam = fusionSlam;
        this.statisticalFolder = statisticalFolder;
        this.output = new LinkedHashMap<>();
        this.initialize();
        this.outputFolder = outputFolder;
    }

    /**
     * Initializes the FusionSlamService. Registers the service to handle
     * TrackedObjectsEvents, PoseEvents, and TickBroadcasts, and sets up
     * callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        this.register();
        //System.out.println(getName() + " subscribed to TrackedObjectsEvent");
        this.subscribeEvent(TrackedObjectsEvent.class, ((event) -> {
        //    System.out.println(getName() + " received TrackedObjectsEvent");
            pendingEvents.add(event);
        }));

        //System.out.println(getName() + " subscribed to PoseEvent");
        this.subscribeEvent(PoseEvent.class, ((poseEvent) -> {
         //   System.out.println(getName() + " received PoseEvent");
            fusionSlam.updatePoses(poseEvent.getPose());

        }));
        this.subscribeEvent(CameraTerminate.class, (e) -> {
            messageBus.terminateT();
            this.LastStampedDetectedObject = e.getStampedObjects();
            if (messageBus.getMicroServiceMap().size() == 1) {
                output.put("error", error);
                output.put("faultySensor", faultySensor);
                output.put("poses", poses);
                output.put("lastCamerasFrame", LastStampedDetectedObject);
                output.put("lastLiDarFrame", LastTrackedObject);
                terminate();
                // create outfile
                this.createOutputFile(output);
            }
        });
        this.subscribeEvent(LidarTerminated.class, (e) -> {
            messageBus.terminateT();
            this.LastTrackedObject = e.getTrackedObjects();
            if (messageBus.getMicroServiceMap().size() == 1) {
                output.put("error", error);
                output.put("faultySensor", faultySensor);
                output.put("poses", poses);
                output.put("lastCamerasFrame", LastStampedDetectedObject);
                output.put("lastLiDarFrame", LastTrackedObject);
                terminate();
                // create outfile
                this.createOutputFile(output);
            }
        });

        this.subscribeBroadcast(TickBroadcast.class, (tickBroadcast) -> {
            time = tickBroadcast.getTick();
            Iterator<TrackedObjectsEvent> itr = pendingEvents.iterator();
            while (itr.hasNext()) {
                TrackedObjectsEvent event = itr.next();
                if (event.getFuture().isDone()) {

                    itr.remove();
                    for (TrackedObject object : event.getFuture().get()) {
                        statisticalFolder.incrementTrackedObjects();
                        fusionSlam.updateLandMarks(object, statisticalFolder);
                    }
                    complete(event, event.getFuture().get());
                }

            }
        });

        this.subscribeBroadcast(CrashedBroadcast.class, (c) -> {
            error = c.getError();
            faultySensor = c.getService().getName();
            poses = fusionSlam.getPoses();
        });
        
        this.subscribeBroadcast(TerminatedBroadcast.class, (t) -> {
            if (messageBus.getMicroServiceMap().size() == 1& pendingEvents.isEmpty()) {
                terminate();
                // create outfile               
                this.createOutputFile(output);
               
            }

        });
    }

    public void createOutputFile(Map<String, Object> output) {
        Map<String, Integer> statistics = new LinkedHashMap<>();
        statistics.put("systemRuntime", statisticalFolder.getRuntime());
        statistics.put("numDetectedObjects", statisticalFolder.getNumDetectedObjects());
        statistics.put("numTrackedObjects", statisticalFolder.getNumTrackedObjects());
        statistics.put("numLandmarks", statisticalFolder.getNumLandmarks());
        List<Map<String, Object>> landmarks = new ArrayList<>();
        for (LandMark landmark : fusionSlam.getLandMarks().values()) {
            Map<String,Object> map=new LinkedHashMap<>();
            map.put("id",landmark.getId());
            map.put("description",landmark.getDescription());
            map.put("coordinates",landmark.getPoints());
            landmarks.add(map);
        }

        output.put("statistics", statistics);
        output.put("landMarks", landmarks);
        String outputPath = outputFolder+"output_file.json"; // Output file path
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(outputPath)) {
            gson.toJson(output, writer);
           // System.out.println("Output file written to: " + outputPath);
        } catch (IOException e) {
           // System.err.println("Error writing the output file: " + e.getMessage());
        }
    }
}
