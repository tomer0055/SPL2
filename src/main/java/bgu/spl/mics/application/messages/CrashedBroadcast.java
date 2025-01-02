package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;

public class CrashedBroadcast implements Broadcast {
    private String error;
    private MicroService service;
    public CrashedBroadcast(MicroService service, String error){
        this.service = service;
        this.error = error;
    }
    public CrashedBroadcast(MicroService service){
        this.service = service;
        this.error= "lidar is disconnected";
    }

    public MicroService getService(){
        return service;
    }
    public String getError(){
        return error;
    }
}