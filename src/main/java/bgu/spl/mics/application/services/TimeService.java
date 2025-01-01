package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    private int TickTime;
    private int Duration;
    private int time ;
    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        time = 1; // start from 1
        this.TickTime = TickTime;
        this.Duration = Duration;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    
    @Override
    protected synchronized void initialize() {


        while (time < Duration) {
            try {
                Thread.sleep(TickTime*100);
                this.sendBroadcast(new TickBroadcast(time));
                time++;
                System.out.println("TimeService: "+time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.terminate();
        this.sendBroadcast(new TerminatedBroadcast());
    }
}
