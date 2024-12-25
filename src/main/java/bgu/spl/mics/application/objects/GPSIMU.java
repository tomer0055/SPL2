package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int CurrentTick;
    private STATUS status;
    private List<Pose> PoseList;
    public GPSIMU(int CurrentTick, STATUS status, List<Pose> PoseList){
        this.CurrentTick = CurrentTick;
        this.status = status;
        this.PoseList = PoseList;
    }
    public void incrementTick(){
        CurrentTick++;
    }   
}
