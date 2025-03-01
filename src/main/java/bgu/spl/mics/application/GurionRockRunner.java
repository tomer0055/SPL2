package bgu.spl.mics.application;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.StatisticalFolder;
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
     * @param args Command-line arguments. The first argument is expected to be the
     *             path to the configuration file.
     */
    public static void main(String[] args) {

        // TODO: Parse configuration file.
        if (args.length == 0) {
            System.err.println("Please provide the path to the configuration file.");
            return;
        }
        String poseJsonFile = "";
        String configFilePath = args[0];
        // make sure it will work if the files are on the same folder as the config
        String path = "";
        if (configFilePath.contains("/")) {
            String[] p = configFilePath.split("/");
            p[p.length - 1] = "";
            for (int i = 0; i < p.length - 1; i++) {
                path += p[i] + "/";
            }
        }
        List<Camera> camerasList = new ArrayList<>();
        List<LiDarWorkerTracker> LiDarList = new ArrayList<>();
        int tickTime = 0;
        int duration = 0;

        try (FileReader reader = new FileReader(configFilePath)) {
            // Parse JSON with Gson
            Gson gson = new Gson();
            JsonObject config = gson.fromJson(reader, JsonObject.class);

            // Access Cameras Configurations

            JsonObject cameras = config.getAsJsonObject("Cameras");
            JsonArray cameraConfigs = cameras.getAsJsonArray("CamerasConfigurations");
            final String cameraDataPath = path + cameras.get("camera_datas_path").getAsString().substring(2);
            System.out.println(cameraDataPath);

            for (int i = 0; i < cameraConfigs.size(); i++) {
                JsonObject camera = cameraConfigs.get(i).getAsJsonObject();
                camerasList.add(new Camera(camera.get("id").getAsInt(), camera.get("frequency").getAsInt(),
                        camera.get("camera_key").getAsString(), cameraDataPath));
            }

            // Access LiDAR Configurations
            JsonObject lidarWorkers = config.getAsJsonObject("LiDarWorkers");
            JsonArray lidarConfigs = lidarWorkers.getAsJsonArray("LidarConfigurations");
            final String lidarDataPath = path + lidarWorkers.get("lidars_data_path").getAsString().substring(2);
            System.out.println(lidarDataPath);

            // System.out.println("\nLiDAR Configurations:");
            for (int i = 0; i < lidarConfigs.size(); i++) {
                JsonObject lidarWorker = lidarConfigs.get(i).getAsJsonObject();
                LiDarList.add(new LiDarWorkerTracker(lidarWorker.get("id").getAsInt(),
                        lidarWorker.get("frequency").getAsInt(), lidarDataPath));
            }

            // Access Other Configurations
            poseJsonFile = path + config.get("poseJsonFile").getAsString().substring(2);
            tickTime = config.get("TickTime").getAsInt();
            duration = config.get("Duration").getAsInt();

            // System.out.println("\nOther Configurations:");
            // System.out.println(" Pose Data Path: " + poseJsonFile);
            // System.out.println(" Tick Time: " + tickTime);
            // System.out.println(" Duration: " + duration);

        } catch (IOException e) {
            // System.err.println("Error reading the configuration file: " +
            // e.getMessage());
        }
        // TODO: Initialize system components and services.
        StatisticalFolder folder = new StatisticalFolder();
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        List<Thread> threads = new ArrayList<>();
        FusionSlamService fusionSlamService = new FusionSlamService(FusionSlam.getInstance(), folder, path);
        threads.add(new Thread(() -> fusionSlamService.run()));

        for (LiDarWorkerTracker lidar : LiDarList) {
            LiDarService service = new LiDarService(lidar, folder);
            threads.add(new Thread(() -> {
                service.run();
            }));
        }
        for (Camera camera : camerasList) {
            CameraService service = new CameraService(camera, folder);
            threads.add(new Thread(() -> {
                service.run();
            }));
        }
        final GPSIMU gpsimu = new GPSIMU(poseJsonFile);
        threads.add(new Thread(() -> {
            (new PoseService(gpsimu)).run();
        }));

        TimeService timeService = new TimeService(tickTime, duration, folder);
        Thread th = new Thread(() -> {
            timeService.run();
        });
        threads.add(th);

        // TODO: Start the simulation.
        threads.forEach(Thread::start);
    }

}
