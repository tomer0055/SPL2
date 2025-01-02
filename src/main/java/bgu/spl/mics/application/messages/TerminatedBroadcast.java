package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;

public class TerminatedBroadcast implements Broadcast {
    private Class<? extends MicroService> microServiceClass;
    public TerminatedBroadcast(Class<? extends MicroService> m) {
        this.microServiceClass = m;
    }
    public Class<? extends MicroService> getMicroService() {
        return microServiceClass;
    }
}