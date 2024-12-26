package bgu.spl.mics.application.objects;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
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

    private  LiDarDataBase(String filePath) {
        Gson gson = new Gson();
        try(FileReader reader = new FileReader(filePath)){
            Type dataType = new TypeToken<List<StampedCloudPoints>>(){}.getType();
            StampedCloudPoint = gson.fromJson(reader, dataType);
            for (StampedCloudPoints point : StampedCloudPoint ) {
                System.out.println(point); // print the tracked object
            }
        } 
        catch (IOException e) {
            e.printStackTrace();  
    }
}
    public static LiDarDataBase getInstance(String filePath) {
        if (instance == null) 
        {
            instance = new LiDarDataBase(filePath);
        }
        return instance;
            
    }
    public StampedCloudPoints getStampedCloudByTime(int time) {
        for (StampedCloudPoints object : StampedCloudPoint) {
            if (object.getTime()==time) {
                return object;
            }
        }
        return null;
    }
}
