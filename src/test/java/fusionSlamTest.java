import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.Pose;

public class fusionSlamTest {
    FusionSlam fusionSlam = FusionSlam.getInstance();

    @Test
    public void testCalCloudPoints() {
        CloudPoint[] points = new CloudPoint[1];
        points[0] = new CloudPoint(2.0, 3.0);
        Pose pose = new Pose((float) 5.0, (float) 10.0, 30, 1);
        CloudPoint[] result = fusionSlam.calCloudPoints(points, pose);
        CloudPoint expected = result[0];
        double x = Math.round(expected.getX() * 1000.0) / 1000.0;
        double y = Math.round(expected.getY() * 1000.0) / 1000.0;
        assertTrue(x == 5.232);
        assertTrue(y == 13.598);
    }

    @Test
    public void testCalCloudPoints_MultiplePoints() {
        CloudPoint[] points = {
                new CloudPoint(1.0, 1.0),
                new CloudPoint(-2.0, 3.0),
                new CloudPoint(0.0, 0.0)
        };
        Pose pose = new Pose((float) 5.0, (float) 10.0, 90, 1);
        CloudPoint[] result = fusionSlam.calCloudPoints(points, pose);

        CloudPoint[] expected = {
                new CloudPoint(4.0, 11.0), // Rotated 90° and translated
                new CloudPoint(2.0, 8.0), // Rotated 90° and translated
                new CloudPoint(5.0, 10.0) // No change
        };

        for (int i = 0; i < result.length; i++) {
            double xResult = Math.round(result[i].getX() * 1000.0) / 1000.0;
            double yResult = Math.round(result[i].getY() * 1000.0) / 1000.0;

            double xExpected = Math.round(expected[i].getX() * 1000.0) / 1000.0;
            double yExpected = Math.round(expected[i].getY() * 1000.0) / 1000.0;

            assertEquals(xExpected, xResult, 0.001);
            assertEquals(yExpected, yResult, 0.001);
        }
    }

    @Test
    public void testAvreageCoordination() {
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
            assertEquals(xResult, xExpected, 0.001);
            assertEquals(yResult, yExpected, 0.001);
        }

    }

    @Test
    public void testAvreageCoordination_EdgeCases() {
        // Test identical points
        CloudPoint[] currentPoints = {
                new CloudPoint(5.0, 5.0),
                new CloudPoint(5.0, 5.0),
                new CloudPoint(5.0, 5.0)
        };
        CloudPoint[] points = {
                new CloudPoint(5.0, 5.0),
                new CloudPoint(5.0, 5.0),
                new CloudPoint(5.0, 5.0)
        };

        CloudPoint[] result = fusionSlam.avreageCoordination(currentPoints, points);

        for (CloudPoint result1 : result) {
            assertEquals(5.0, result1.getX(), 0.001);
            assertEquals(5.0, result1.getY(), 0.001);
        }

        // Test averaging with all-zero points
        CloudPoint[] zeros = {
                new CloudPoint(0.0, 0.0),
                new CloudPoint(0.0, 0.0),
                new CloudPoint(0.0, 0.0)
        };

        result = fusionSlam.avreageCoordination(currentPoints, zeros);

        for (CloudPoint result1 : result) {
            assertEquals(2.5, result1.getX(), 0.001);
            assertEquals(2.5, result1.getY(), 0.001);
        }
    }

    @Test
    public void testIntegration_CalAndAverage() {
        // Local coordinates for calCloudPoints
        CloudPoint[] localPoints = {
                new CloudPoint(1.0, 1.0),
                new CloudPoint(-2.0, 3.0)
        };
        Pose pose = new Pose((float) 5.0, (float) 10.0, 45, 1);
        CloudPoint[] globalPoints = fusionSlam.calCloudPoints(localPoints, pose);

        // Current global points to average
        CloudPoint[] currentGlobalPoints = {
                new CloudPoint(6.0, 10.0),
                new CloudPoint(3.0, 12.0)
        };

        // Average the current and newly calculated points
        CloudPoint[] averagedPoints = fusionSlam.avreageCoordination(currentGlobalPoints, globalPoints);

        // Expected averaged points
        CloudPoint[] expectedPoints = {
                new CloudPoint(5.5, 10.707),
                new CloudPoint(2.232, 11.353)
        };

        for (int i = 0; i < averagedPoints.length; i++) {
            assertEquals(expectedPoints[i].getX(), averagedPoints[i].getX(), 0.001);
            assertEquals(expectedPoints[i].getY(), averagedPoints[i].getY(), 0.001);
        }
    }

}
