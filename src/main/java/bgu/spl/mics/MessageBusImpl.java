package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus
 * interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl instance = null;
	private ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventMap; // for each event
																										// type, a list
																										// of
																										// microservices
																										// that
																										// subscribed to
																										// it
	private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastMap; // for each
																												// broadcast
																												// type,
																												// a
																												// list
																												// of
																												// microservices
																												// that
																												// subscribed
																												// to it
	private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> microServiceMap; // for each microservice, a
																								// list of messages that
																								// it should handle
	private ConcurrentHashMap<Event, Future> events; //

	private MessageBusImpl() {
	};

	public static MessageBusImpl getInstance() {
		if (instance == null) {
			instance = new MessageBusImpl();
			instance.eventMap = new ConcurrentHashMap<>();
			instance.broadcastMap = new ConcurrentHashMap<>();
			instance.microServiceMap = new ConcurrentHashMap<>();
			instance.events = new ConcurrentHashMap<>();
		}
		return instance;
	}

	@Override
public synchronized <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
    eventMap.putIfAbsent(type, new ConcurrentLinkedQueue<>());
    if (!eventMap.get(type).contains(m)) {
        eventMap.get(type).add(m);
    } else {
        System.out.println(m.getName() + " is already subscribed to event: " + type.getSimpleName());
    }
}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastMap.computeIfAbsent(type, k->new ConcurrentLinkedQueue<>()).add(m);
		
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// Resolve the future associated with the event
		//remove from thq queue of the microservice the event
		Future<T> future = events.get(e);
		if(future!=null){
			future.resolve(result);
			events.remove(e);

			
		}
		else{
			// Handle the case where the event is not in the events map
			System.err.println("Event not found in events map: " + e.getClass().getName());
		}
	}

	@Override
public synchronized void sendBroadcast(Broadcast b) {
    ConcurrentLinkedQueue<MicroService> microServices = broadcastMap.get(b.getClass());
    if (microServices != null) {
        for (MicroService m : microServices) {
            ConcurrentLinkedQueue<Message> queue = microServiceMap.get(m);
            if (queue != null) {
                queue.add(b);
			            } else {
                System.err.println("No queue found for MicroService: " + m.getName());
            }
        }
        notifyAll();
    } else {
        System.err.println("No subscribers for broadcast: " + b.getClass().getName());
    }
}

	
	@Override
	public synchronized <T> Future<T> sendEvent(Event<T> e) {
		ConcurrentLinkedQueue<MicroService> microServices = eventMap.get(e.getClass());
		if (microServices != null && !microServices.isEmpty()) {
			MicroService m = microServices.poll();
			if (m != null) {
				try {
					ConcurrentLinkedQueue<Message> queue = microServiceMap.get(m);
					if (queue != null) {
						queue.add(e);
						Future<T> future = new Future<>();
						events.put(e, future);
						return future;
					} else {
						System.err.println("No queue found for MicroService: " + m.getName());
					}
				} catch (Exception ex) {
					System.err.println("Exception in sendEvent: " + ex.getMessage());
				} finally {
					if (m != null) {
						microServices.add(m);
						System.out.println(m.getName() + " was re-added to the " + e.getClass().getSimpleName() + " queue.");
					}
				}
			}
		}
		System.err.println("No subscribers for event: " + e.getClass().getName());
		return null;
	}

	

	@Override
	public void register(MicroService m) {
		microServiceMap.putIfAbsent(m,new ConcurrentLinkedQueue<>());


	}

	@Override
	public void unregister(MicroService m) {
		microServiceMap.remove(m);
		for (ConcurrentLinkedQueue<MicroService> microServices : eventMap.values()) {
			microServices.remove(m);
		}
		for (ConcurrentLinkedQueue<MicroService> microServices : broadcastMap.values()) {
			microServices.remove(m);
		}
	}

	@Override
public synchronized Message awaitMessage(MicroService m) throws InterruptedException {
    ConcurrentLinkedQueue<Message> queue = microServiceMap.get(m);

    if (queue == null) {
        throw new IllegalStateException("MicroService not registered: " + m.getName());
    }

    // Clean up stale or completed events
    queue.removeIf(message -> (message instanceof Event) && events.get(message) == null);

    while (queue.isEmpty()) {
        wait(); // Wait until a message is available in the queue
    }

    Message message = queue.poll(); // Retrieve and remove the head of the queue
	System.out.println(m.getName() + " fetch message: " + message.getClass().getSimpleName());

    if (message == null) {
        throw new IllegalStateException("Queue was unexpectedly empty after wait: " + m.getName());
    }

    return message;
}
	public ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> getMicroServiceMap() {
		return microServiceMap;
	}
}
