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
		for(MicroService m : microServices){
			microServiceMap.get(m).add(b);
		}
		notifyAll();
	}

	
	@Override
	public synchronized <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = new Future<>();
		events.put(e,future);
		ConcurrentLinkedQueue<MicroService> microServices = eventMap.get(e.getClass());
		MicroService m = microServices.poll();
		microServiceMap.get(m).add(e);
		notifyAll();
		microServices.add(m);
		return future;
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
