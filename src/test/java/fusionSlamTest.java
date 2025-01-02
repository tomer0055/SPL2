import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.Pose;

public class fusionSlamTest {
    FusionSlam fusionSlam = FusionSlam.getInstance();

    @Test
    public void testcalCloudPoints() {
        CloudPoint[] points = new CloudPoint[2];
        points[0] = new CloudPoint(-0.012505242601037025, -0.32929766178131104);
        points[1] = new CloudPoint(-0.00588871818035841,-0.31736207008361816);
        Pose pose1 = new Pose((float) 0.0, (float) 0.0, 0, 1);
        CloudPoint[] result1 = fusionSlam.calCloudPoints(points, pose1);
        System.out.println("result1: " + result1[0].getX() + " " + result1[0].getY());
        System.out.println("result2: " + result1[1].getX() + " " + result1[1].getY());
        // CloudPoint expected = result1[0];
        // double x = Math.round(expected.getX() * 1000.0) / 1000.0;
        // double y = Math.round(expected.getY() * 1000.0) / 1000.0;
        // System.out.println("x: " + x + " y: " + y);
        // assertTrue(x == 5.232);
        // assertTrue(y == 13.598);
    }

    @Test
    public void testavreageCoordination() {
        CloudPoint[] currentPoints = { new CloudPoint(1.0, 4.0), new CloudPoint(3.0, 2.0), new CloudPoint(5.0, 10.0) };
        CloudPoint[] points = { new CloudPoint(2.0, 9.0), new CloudPoint(4.0, 11.0), new CloudPoint(2.0, 3.0) };
        CloudPoint[] result = fusionSlam.avreageCoordination(currentPoints, points);
        CloudPoint[] expected = { new CloudPoint(1.5, 6.5), new CloudPoint(3.5, 6.5), new CloudPoint(3.5, 6.5) };
        for (int i = 0; i < result.length; i++) {
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
