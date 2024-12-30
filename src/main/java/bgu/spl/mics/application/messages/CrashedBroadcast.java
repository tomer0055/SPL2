package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;

public class CrashedBroadcast implements Broadcast {
    private MicroService service;
    private String description;
    public CrashedBroadcast(MicroService service, String description){
        this.service = service;
        this.description= description;
    }
    public CrashedBroadcast(MicroService service){
        this.service = service;
        this.description= "the "+MicroService.class.getName()+" has crashed";
    }

    public MicroService getService(){
        return service;
    }
    public String getDescription(){
        return description;
    }

}