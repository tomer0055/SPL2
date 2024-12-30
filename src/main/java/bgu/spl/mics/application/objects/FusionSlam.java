package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping
 * (SLAM). Combines data from multiple sensors (e.g., LiDAR, camera) to build
 * and update a global map. Implements the Singleton pattern to ensure a single
 * instance of FusionSlam exists.
 */
public class FusionSlam {

    private ArrayList<LandMark> landMarks;
    private List<Pose> poses;

    private static class FusionSlamHolder {

        public static FusionSlam instance = new FusionSlam();
    }

    private FusionSlam() {
    }

    public static FusionSlam getInstance() {
        return FusionSlamHolder.instance;
    }

    /**
     * Updates the global map with a new landmark.
     *
     * @param landMark The new landmark to add to the map.
     */
    public void updateLandMarks( LandMark landMark) {
            landMarks.add(landMark);
    }

    /**
     * Updates the global map with a new pose.
     *
     * @param pose The pose of the robot.
     */
    public void updatePoses(Pose pose) {
        poses.add(pose);
    }
    public List<LandMark> getLandMarks(){
        return landMarks;
    }
}
