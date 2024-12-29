package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.Stack;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int CurrentTick;
    private STATUS status;
    private Stack<Pose> PoseList;
    public GPSIMU(int CurrentTick, STATUS status, List<Pose> PoseList){
        this.CurrentTick = CurrentTick;
        this.status = status;
        this.PoseList = new Stack<Pose>();
        for (Pose pose : PoseList){
            this.PoseList.push(pose);
        }
    }
    public void incrementTick(){
        CurrentTick++;
    }
    public Pose getPose(){
        //todo: implement
        return PoseList.pop();
    }
}
