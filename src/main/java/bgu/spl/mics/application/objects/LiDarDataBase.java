package bgu.spl.mics.application.objects;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for
 * tracked objects.
 */
public class LiDarDataBase {

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    private static LiDarDataBase instance;
    private List<StampedCloudPoints> StampedCloudPoint;

    private LiDarDataBase(String filePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            Type dataType = new TypeToken<List<StampedCloudPoints>>() {
            }.getType();
            StampedCloudPoint = gson.fromJson(reader, dataType);
           // System.out.println(StampedCloudPoint);
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LiDarDataBase getInstance(String filePath) {
        if (instance == null) {
            instance = new LiDarDataBase(filePath);
        }
        return instance;

    }

    public StampedCloudPoints getStampedCloudByTime(int time) {
        for (StampedCloudPoints object : StampedCloudPoint) {
            if (object.getTime() == time) {
                return object;
            }
        }
        return null;
    }

    /**
     * Converts a StampedCloudPoints object to a TrackedObject object.
     *
     * @param objs The DetectedObjects objects to convert.
     * @return The TrackedObject objects.
     */
    public List<TrackedObject> MatchTrackedObjectToDetobj(List<DetectedObject> objs) {
        List<TrackedObject> trackedObjects = new ArrayList<>();
        for (DetectedObject obj : objs) {
            StampedCloudPoints stampedCloudPoints = getStampedCloudById(obj.getId());
            if (stampedCloudPoints != null) {
                List<TrackedObject> trackedObject = StampedCloudToTrackedObj(List.of(stampedCloudPoints));
                trackedObjects.addAll(trackedObject);
            }
        }
        return trackedObjects;
    }
    public TrackedObject getTrackedObjectByTimeAndId(int time, String id) {
        StampedCloudPoints stampedCloudPoints = getStampedCloudByTime(time);
        if(stampedCloudPoints==null)
        {
            return null;
        }
        if(stampedCloudPoints.getId().equals(id))
        {
            List<TrackedObject> trackedObject = StampedCloudToTrackedObj(List.of(stampedCloudPoints));
            return trackedObject.get(0);
        }
        return null;
    }

    private StampedCloudPoints getStampedCloudById(String id) {
        for (StampedCloudPoints object : StampedCloudPoint) {
            if (object.getId().equals(id)) {
                return object;
            }
        }
        return null;
    }

    /**
     * Converts a StampedCloudPoints object to a TrackedObject object.
     *
     * @param stampedCloudPoints The StampedCloudPoints object to convert.
     * @return The TrackedObject object.
     */
    private List<TrackedObject> StampedCloudToTrackedObj(List<StampedCloudPoints> stampedCloudPoints) {
        List<TrackedObject> trackedObjects = new ArrayList<>();

        for (StampedCloudPoints obj : stampedCloudPoints) {
            List<List<Double>> points = obj.getPoints();

            CloudPoint[] cloudPoints = new CloudPoint[points.size()];
            for (int i = 0; i < points.size(); i++) {
                int x = points.get(i).get(0).intValue();
                int y = points.get(i).get(1).intValue();
                cloudPoints[i] = new CloudPoint(x, y);
            }

            TrackedObject trackedObject = new TrackedObject(obj.getTime(), obj.getId(), cloudPoints, "");
            trackedObjects.add(trackedObject);
        }
        return trackedObjects;
    }

}
