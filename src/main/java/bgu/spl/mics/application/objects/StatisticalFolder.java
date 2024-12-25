package bgu.spl.mics.application.objects;

/**
 * Holds statistical information about the system's operation. This class
 * aggregates metrics such as the runtime of the system, the number of objects
 * detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private int runtime;
    private int numDetectedObjects;
    private int numTrackedObjects;
    private int numLandmarks;
    public StatisticalFolder(){
        this.runtime = 0;
        this.numDetectedObjects = 0;
        this.numTrackedObjects = 0;
        this.numLandmarks = 0;
    }
    public void incrementRuntime(){
        runtime++;
    }
    public void incrementDetectedObjects(){
        numDetectedObjects++;
    }
    public void incrementTrackedObjects(){
        numTrackedObjects++;
    }
    public void incrementLandmarks(){
        numLandmarks++;
    }
}

