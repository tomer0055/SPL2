package bgu.spl.mics.application.objects;

import java.util.Stack;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int CurrentTick;
    private STATUS status;
    private Stack<Pose> PoseList;
    public GPSIMU( ){
        this.CurrentTick = 0;
        this.status = STATUS.UP;
        this.PoseList = new Stack<Pose>();
    }
    public void incrementTick(){
        CurrentTick++;
    }
    public Pose getPose(){
        //todo: implement
        return PoseList.pop();
    }
    public void addPose(Pose pose){
        PoseList.push(pose);
    }
}
