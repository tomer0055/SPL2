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

    public List<StampedCloudPoints> getStampedCloudByTime(int time) {
        List<StampedCloudPoints> stampedCloudPoints = new ArrayList<>();
        for (StampedCloudPoints object : StampedCloudPoint) {
            if (object.getTime() == time) {
                stampedCloudPoints.add(object);
            }
        }
        return stampedCloudPoints;
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
                TrackedObject trackedObject = StampedCloudToTrackedObj(stampedCloudPoints);
                trackedObject.setDescription(obj.getDescription());
                trackedObjects.add(trackedObject);
            }
        }
        return trackedObjects;
    }

    public TrackedObject getTrackedObjectByTimeAndId(int time, String id) {

        List<StampedCloudPoints> stampedCloudPoints = getStampedCloudByTime(time);

        if (stampedCloudPoints == null) {
            System.out.println("ERROR: StampedCloudPoints is null");
            return null;
        }
        for (StampedCloudPoints scp : stampedCloudPoints) {

            if (scp.getId().equals(id)) {
                TrackedObject trackedObject = StampedCloudToTrackedObj(scp);
                return trackedObject;
            }
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
    private TrackedObject StampedCloudToTrackedObj(StampedCloudPoints stampedCloudPoints) {
        TrackedObject trackedObjects ;
        CloudPoint[] points = new CloudPoint[stampedCloudPoints.getPoints().size()];
        int i = 0;
        for (List<Double> obj : stampedCloudPoints.getPoints()) {

                double x = obj.get(0);
                double y = obj.get(1);
                CloudPoint cloudPoint = new CloudPoint(x, y);
                points[i] = cloudPoint;
                i++;
                
            
        }
        int time = stampedCloudPoints.getTime();
        trackedObjects = new TrackedObject(time,stampedCloudPoints.getId(),  points,"");
        return trackedObjects;
    }

}
