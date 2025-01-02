package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * TimeService acts as the global timer for the system, broadcasting
 * TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    /**
     * Constructor for TimeService.
     *
     * @param TickTime The duration of each tick in milliseconds.
     * @param Duration The total number of ticks before the service terminates.
     */
    private final int TickTime;
    private final int Duration;
    private int time;
    private StatisticalFolder folder;

    public TimeService(int TickTime, int Duration,StatisticalFolder folder) {
        super("TimeService");
        time = 1; // start from 1
        this.TickTime = TickTime;
        this.Duration = Duration;
        this.folder = folder;
       

    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified
     * duration.
     */

    @Override
    protected synchronized void initialize() {
        while (time < Duration & this.terminated == false) {
            try {
                Thread.sleep(TickTime * 100);
                
                this.sendBroadcast(new TickBroadcast(time));
                System.out.println("TimeService: " + time);
                time++;
                folder.incrementRuntime();
                if(messageBus.getMicroServiceMap().size() == 2)
                {
                    this.sendBroadcast(new TerminatedBroadcast(TimeService.class));
                    this.terminate();
                }
                else
                {
                    
                }

                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.terminate();
        this.sendBroadcast(new TerminatedBroadcast(TimeService.class));
    }
}
