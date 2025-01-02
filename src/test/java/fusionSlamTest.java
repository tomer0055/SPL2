import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.Pose;

public class fusionSlamTest {
    FusionSlam fusionSlam=FusionSlam.getInstance();



    @Test
    public void testCalCloudPoints() {
        CloudPoint[] points = new CloudPoint[1];
        points[0] = new CloudPoint(2.0, 3.0);
        Pose pose = new Pose((float)5.0, (float)10.0, 30, 1);
        CloudPoint[] result = fusionSlam.calCloudPoints(points, pose);
        CloudPoint expected = result[0];
        double x = Math.round(expected.getX() * 1000.0) / 1000.0;
        double y = Math.round(expected.getY() * 1000.0) / 1000.0;
        System.out.println("x: " + x + " y: " + y);
        assertTrue(x == 5.232); 
        assertTrue(y==13.598);
    }
    @Test
    public void testAvreageCoordination() {
        CloudPoint[] currentPoints ={new CloudPoint(1.0, 4.0), new CloudPoint(3.0, 2.0), new CloudPoint(5.0, 10.0)};
        CloudPoint[] points = {new CloudPoint(2.0, 9.0), new CloudPoint(4.0, 11.0), new CloudPoint(2.0, 3.0)};
        CloudPoint[] result = fusionSlam.avreageCoordination(currentPoints, points);
        CloudPoint[] expected = {new CloudPoint(1., 6.5), new CloudPoint(3.5, 6.5), new CloudPoint(3.5, 6.5)};
        for(int i=0;i<result.length;i++){
            CloudPoint resultPoint = result[i];
            CloudPoint expectedPoint = expected[i];
            double xResult = resultPoint.getX();
            double yResult = resultPoint.getY();
            double xExpected = expectedPoint.getX();
            double yExpected = expectedPoint.getY();
            assertTrue(xResult == xExpected);
            assertTrue(yResult == yExpected);
        }
        
    }
    
}
