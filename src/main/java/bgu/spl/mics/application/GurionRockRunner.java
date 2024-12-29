package bgu.spl.mics.application;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.FusionSlamService;
import bgu.spl.mics.application.services.LiDarService;
import bgu.spl.mics.application.services.PoseService;
import bgu.spl.mics.application.services.TimeService;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */

public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {

        // TODO: Parse configuration file.
        if (args.length == 0) {
            System.err.println("Please provide the path to the configuration file.");
            return;
        }
        String configFilePath = args[0];
        List<Camera> camerasList = new ArrayList<>();
        List<LiDarWorkerTracker> LiDarList = new ArrayList<>();
        try (FileReader reader = new FileReader(configFilePath)) {
            // Parse JSON with Gson
            Gson gson = new Gson();
            JsonObject config = gson.fromJson(reader, JsonObject.class);

            // Access Cameras Configurations
            JsonObject cameras = config.getAsJsonObject("Cameras");
            JsonArray cameraConfigs = cameras.getAsJsonArray("CamerasConfigurations");
            String cameraDataPath = cameras.get("camera_datas_path").getAsString();

            for (int i = 0; i < cameraConfigs.size(); i++) {
                JsonObject camera = cameraConfigs.get(i).getAsJsonObject();
                camerasList.add(new Camera(camera.get("id").getAsInt(), camera.get("frequency").getAsInt()));
            }

            // Access LiDAR Configurations
            JsonObject lidarWorkers = config.getAsJsonObject("LidarWorkers");
            JsonArray lidarConfigs = lidarWorkers.getAsJsonArray("LidarConfigurations");
            String lidarDataPath = lidarWorkers.get("lidars_data_path").getAsString();
            

            System.out.println("\nLiDAR Configurations:");
            for (int i = 0; i < lidarConfigs.size(); i++) {
                JsonObject lidarWorker = lidarConfigs.get(i).getAsJsonObject();
                LiDarList.add(new LiDarWorkerTracker(lidarWorker.get("id").getAsInt(), lidarWorker.get("frequency").getAsInt()));
            }

            // Access Other Configurations
            String poseJsonFile = config.get("poseJsonFile").getAsString();
            int tickTime = config.get("TickTime").getAsInt();
            int duration = config.get("Duration").getAsInt();

            System.out.println("\nOther Configurations:");
            System.out.println("  Pose Data Path: " + poseJsonFile);
            System.out.println("  Tick Time: " + tickTime);
            System.out.println("  Duration: " + duration);

        } catch (IOException e) {
            System.err.println("Error reading the configuration file: " + e.getMessage());
        }

        

        // TODO: Initialize system components and services.
        //create statistical foldrer
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        MicroService timeService = new TimeService(100, 1000);
        for(LiDarWorkerTracker lidar: LiDarList){
            MicroService LiDARService = new LiDarService(lidar);
        }
        for(Camera camera: camerasList){
            MicroService cameraService = new CameraService(camera);
        }
        MicroService poseService = new PoseService(new GPSIMU());
        MicroService fusionSlamService = new FusionSlamService( FusionSlam.getInstance());

        // TODO: Start the simulation.
        timeService.run();
    }
}
