package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl instance = null;
	private ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventMap;
	private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastMap;
	private ConcurrentHashMap<MicroService,ConcurrentLinkedQueue<Message>> microServiceMap;
	private ConcurrentHashMap<Event,Future> events;
	private MessageBusImpl(){};

	public static MessageBusImpl getInstance() {
		if(instance==null){
			instance = new MessageBusImpl();
			instance.eventMap = new ConcurrentHashMap<>();
			instance.broadcastMap = new ConcurrentHashMap<>();
			instance.microServiceMap = new ConcurrentHashMap<>();
			instance.events = new ConcurrentHashMap<>();
		}
		return instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventMap.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		eventMap.get(type).add(m);	
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastMap.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		broadcastMap.get(type).add(m);
		
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		(events.get(e)).resolve(result);
	}

	@Override
	public synchronized  void sendBroadcast(Broadcast b) {
		ConcurrentLinkedQueue<MicroService> microServices = broadcastMap.get(b.getClass());
		if (microServices != null) {
            for (MicroService m : microServices) {
                ConcurrentLinkedQueue<Message> queue = microServiceMap.get(m);
                if (queue != null) {
                    queue.add(b);
                } else {
                    // Handle the case where the microServiceMap does not contain the MicroService
                    System.err.println("No queue found for MicroService: " + m.getName());
                }
            }
            notifyAll();
        } else {
            // Handle the case where there are no subscribers for the broadcast
            System.err.println("No subscribers for broadcast: " + b.getClass().getName());
        }
	}

	
	@Override
	public synchronized <T> Future<T> sendEvent(Event<T> e) {
		ConcurrentLinkedQueue<MicroService> microServices = eventMap.get(e.getClass());
        if (microServices != null) {
            MicroService m = microServices.peek();
            if (m != null) {
                ConcurrentLinkedQueue<Message> queue = microServiceMap.get(m);
                if (queue != null) {
                    queue.add(e);
                    Future<T> future = new Future<>();
                    events.put(e, future);
					microServices.remove(m);
					microServices.add(m);
                    return future;
                } else {
                    // Handle the case where the microServiceMap does not contain the MicroService
                    System.err.println("No queue found for MicroService: " + m.getName());
                }
            } else {
                // Handle the case where no MicroService is available to handle the event
                System.err.println("No MicroService available to handle event: " + e.getClass().getName());
            }
        } else {
            // Handle the case where there are no subscribers for the event
            System.err.println("No subscribers for event: " + e.getClass().getName());
        }
        return null;
	}

	@Override
	public void register(MicroService m) {
		microServiceMap.putIfAbsent(m,new ConcurrentLinkedQueue<>());
		

	}

	@Override
	public void unregister(MicroService m) {
		microServiceMap.remove(m);
		for(ConcurrentLinkedQueue<MicroService> microServices : eventMap.values()){
			microServices.remove(m);
		}
		for(ConcurrentLinkedQueue<MicroService> microServices : broadcastMap.values()){
			microServices.remove(m);
		}
	}

	@Override
	public synchronized Message awaitMessage(MicroService m) throws InterruptedException {


		while(microServiceMap.get(m).isEmpty()){
			wait();
		}
		return microServiceMap.get(m).poll();
	}
	public ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> getMicroServiceMap() {
		return microServiceMap;
	}
}
