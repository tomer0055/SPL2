import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.LiDarService;
import bgu.spl.mics.application.services.TimeService;

class CameraServicesTest {
    private CameraService cameraServices;
    private Camera camera;
    private int id;
    private int ferequency;
    private TimeService timeService;
    LiDarService lidarS;

    @BeforeEach
    public void setUp() {
        id = 1;
        ferequency = 1;
        StatisticalFolder folder = new StatisticalFolder();
        String cameraPath = "src/test/java/resources/camera_data.json";
        String LidarPath = "src/test/java/resources/lidar_data.json";
        this.camera = new Camera(id, ferequency, "camera1", cameraPath);
        cameraServices = new CameraService(camera, folder);
        timeService = new TimeService(10, 15, new StatisticalFolder());
        LiDarWorkerTracker lidar = new LiDarWorkerTracker(1, 2, LidarPath);
        lidarS = new LiDarService(lidar, folder);
    }

    @Test
    public void testSetUp() {
        StampedDetectedObjects t = camera.getDetectedObjectsByTime(2);
        System.out.println("result --->>       " + t.toString());
        assertNotNull(t);
    }

    @Test
    public void testSetUpWithErrors() {
        StampedDetectedObjects t = camera.getDetectedObjectsByTime(14);
        System.out.println("result --->>       " + t.toString());
        assertNotNull(t);
    }

    @Test
    public void TestSyncWithTime() {
        Thread t1 = new Thread(() -> {
            cameraServices.run();
        });
        Thread t2 = new Thread(() -> {
            timeService.run();
        });
        Thread t3 = new Thread(() -> {
            timeService.run();
        });
        t1.start();
        t2.start();
        t3.start();

    }
}