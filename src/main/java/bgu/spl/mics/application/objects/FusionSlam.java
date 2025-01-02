package bgu.spl.mics.application.objects;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping
 * (SLAM). Combines data from multiple sensors (e.g., LiDAR, camera) to build
 * and update a global map. Implements the Singleton pattern to ensure a single
 * instance of FusionSlam exists.
 */
public class FusionSlam {

    private ConcurrentHashMap<String, LandMark> landMarks;
    private ConcurrentHashMap<Integer, Pose> poses;

    private static class FusionSlamHolder {

        public static FusionSlam instance = new FusionSlam();
    }

    private FusionSlam() {
        landMarks = new ConcurrentHashMap<>();
        poses = new ConcurrentHashMap<>();

    }

    public static FusionSlam getInstance() {
        return FusionSlamHolder.instance;
    }

    /**
     * Updates the global map with a new landmark.
     *
     * @param landMark The new landmark to add to the map.
     */
    public void updateLandMarks(TrackedObject ob, StatisticalFolder statisticalFolder) {
        CloudPoint[] newPoints = calCloudPoints(ob.getCoordinates(), poses.get(ob.getTime()));
        if (landMarks.containsKey(ob.getId())) {
            LandMark landMark = landMarks.get(ob.getId()); // get the current LandMark
            CloudPoint[] currentPoints = landMark.getPoints().toArray(new CloudPoint[landMark.getPoints().size()]); // get the current points
            CloudPoint[] avreagedPointes = avreageCoordination(currentPoints, newPoints);
            landMark.updateLocation(avreagedPointes);
        } else {
            landMarks.put(ob.getId(), new LandMark(ob.getId(), ob.getDescription(), newPoints));
            statisticalFolder.incrementLandmarks();
        }
    }

    /**
     * Updates the global map with a new pose.
     *
     * @param pose The pose of the robot.
     */
    public void updatePoses(Pose pose) {
        poses.put(pose.getTime(), pose);
    }

    public ConcurrentHashMap<String, LandMark> getLandMarks() {
        return landMarks;
    }

    public CloudPoint[] calCloudPoints(CloudPoint[] localCoordinates, Pose pose) {
        CloudPoint[] globalCoordinates = new CloudPoint[localCoordinates.length];
        double yawRadians = Math.toRadians(pose.getYaw());
    
        // Precompute cosine and sine of the yaw angle
        double cosTheta = Math.cos(yawRadians);
        double sinTheta = Math.sin(yawRadians);
    
        // Transform each local coordinate to global
        for (int i = 0; i < localCoordinates.length; i++) {
            CloudPoint local = localCoordinates[i];
            double xLocal = local.getX();
            double yLocal = local.getY();
    
            // Apply rotation
            double xRotated = cosTheta * xLocal - sinTheta * yLocal;
            double yRotated = sinTheta * xLocal + cosTheta * yLocal;
    
            // Apply translation
            double xGlobal = xRotated + pose.getX();
            double yGlobal = yRotated + pose.getY();
    
            // Assign to globalCoordinates array
            globalCoordinates[i] = new CloudPoint(xGlobal, yGlobal);
        }
    
        return globalCoordinates;
    }
    

    public CloudPoint[] avreageCoordination(CloudPoint[] currePoints, CloudPoint[] newPoints) {
        CloudPoint[] avreagePoints = new CloudPoint[newPoints.length];
        for (int i = 0; i < newPoints.length; i++) {
            if (i>=currePoints.length) {
                avreagePoints[i] = newPoints[i];
            } else {
                avreagePoints[i] = new CloudPoint((currePoints[i].getX() + newPoints[i].getX()) / 2,
                        (currePoints[i].getY() + newPoints[i].getY()) / 2);
            }
        }
        return avreagePoints;
    }
}
