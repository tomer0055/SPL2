
import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.objects.LiDarDataBase;
 
public class LidarDataBaseTest {
    //Junit test for LidarDataBase
    @Test
    public void testLidarDataBaseInstance() {
        String path = "src/test/java/resources/lidar_data.json";
        LiDarDataBase lidarDataBase =  LiDarDataBase.getInstance(path);
        assert lidarDataBase != null;
    }
    @Test
    public void testLidarDataBaseGetStampedCloudByTime() {
        String path = "src/test/java/resources/lidar_data.json";
        LiDarDataBase lidarDataBase =  LiDarDataBase.getInstance(path);
        assert lidarDataBase.getStampedCloudByTime(2) != null;
    }
}
