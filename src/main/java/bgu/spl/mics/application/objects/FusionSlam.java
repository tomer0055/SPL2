package bgu.spl.mics.application.objects;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    
    private static class FusionSlamHolder {
        public static FusionSlam instance = new FusionSlam();
    }
    private FusionSlam() {
    }
    public static FusionSlam getInstance() {
        return FusionSlamHolder.instance;
    }
}
