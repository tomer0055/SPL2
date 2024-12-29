package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;

public class CrashedBroadcast implements Broadcast {
    private MicroService service;
    public CrashedBroadcast(MicroService service){
        this.service = service;
    }
    public MicroService getService(){
        return service;
    }
}