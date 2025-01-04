import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.services.CameraService;
public class MsessageBusTest {
    @BeforeEach
    public void setUp()
    {
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        messageBus.getMicroServiceMap().clear();
        messageBus.getBroadCastMap().clear();
    }
    


    //before -> microServiceMap is empty
    //after -> microServiceMap has one element

@Test
    public void TestRegister()
    {
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        Camera camera = new Camera(1, 1, "camera1", "./example_input_with_error/camera_data.json");
        StatisticalFolder folder = new StatisticalFolder();
        CameraService cameraService = new CameraService(camera, folder);
        assertTrue(messageBus.getMicroServiceMap().size()==1);


    }
    //before -> microServiceMap has one element
    //after -> microServiceMap is empty
    @Test
    public void TestUnregister()
    {
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        Camera camera = new Camera(1, 1, "camera1", "./example_input_with_error/camera_data.json");
        StatisticalFolder folder = new StatisticalFolder();
        CameraService cameraService = new CameraService(camera, folder);
        messageBus.unregister(cameraService);
        assertTrue(messageBus.getMicroServiceMap().isEmpty());
    }
    //before -> tickBroadcast has no subscribers
    //after -> tickBroadcast has one subscriber
    @Test
    public void TestSubscribeBroadcast()
    {
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        Camera camera = new Camera(1, 1, "camera1", "./example_input_with_error/camera_data.json");
        StatisticalFolder folder = new StatisticalFolder();
        CameraService cameraService = new CameraService(camera, folder);
        assertTrue(messageBus.getBroadCastMap().get(bgu.spl.mics.application.messages.TickBroadcast.class).contains(cameraService));
    }

}

