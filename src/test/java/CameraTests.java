import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.services.CameraService;



public class CameraTests {

    private CameraService cameraService;
    private Camera camera;


    @Test
    public void setUp() {
        int id = 1;
        int ferequency = 1;
        StatisticalFolder folder = new StatisticalFolder();
        String path= "./example_input_with_error/camera_data.json";
        this.camera = new Camera(id, ferequency,"camera1", path);
        StampedDetectedObjects t = camera.getDetectedObjectsByTime(2);
        System.out.println("result --->>       "+t.toString());
        assertNotNull(t);
        
    }
    @Test
    public void setUpWithErrors() {
        int id = 1;
        int ferequency = 1;
        StatisticalFolder folder = new StatisticalFolder();
        String path= "./example_input_with_error/camera_data.json";
        this.camera = new Camera(id, ferequency,"camera1", path);
        StampedDetectedObjects t = camera.getDetectedObjectsByTime(14);
        System.out.println("result --->>       "+t.toString());
        assertNotNull(t);
        
    }


    
}