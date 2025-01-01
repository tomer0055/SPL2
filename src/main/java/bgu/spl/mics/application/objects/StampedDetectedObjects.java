package bgu.spl.mics.application.objects;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    private int time;
    private DetectedObject[] detectedObjects;   

    public StampedDetectedObjects(int time, DetectedObject[] objects) {
        this.time = time;
        this.detectedObjects= objects;
    }

    public int getTime() {
        return time;
    }

    public  DetectedObject[] getDetectedObjects() {
        return detectedObjects;
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Time: " + time + "\nDetected Objects:\n");
        for (DetectedObject obj : detectedObjects) {
            result.append("  - ID: ").append(obj.getId()).append(", Description: ").append(obj.getDescription()).append("\n");
        }
        return result.toString();
    }
}
