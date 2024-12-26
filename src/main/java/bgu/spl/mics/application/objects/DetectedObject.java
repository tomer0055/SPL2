package bgu.spl.mics.application.objects;

/**
 * DetectedObject represents an object detected by the camera.
 * It contains information such as the object's ID and description.
 */
public class DetectedObject {

    private final int id;
    private  String description;
    public DetectedObject(int id, String description){ 
        this.id = id;
        this.description = description;
    }
    public int getId(){
        return id;
    }        
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    @Override
    public String toString() {
        return "id: " + id + " description: " + description;
    }
}

