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
    private CloudPoint[] cloudPoints;
    private LiDarDataBase(String filePath) {
        Gson gson = new Gson();
        try(FileReader reader = new FileReader(filePath)){
            Type dataType = new TypeToken<List<CloudPoint>>(){}.getType();
            cloudPoints = gson.fromJson(reader, dataType);
            for (CloudPoint point : cloudPoints ) {
                System.out.println(point); // print the cloud points
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
}
