package vicinity.model;

import java.io.Serializable;

/**
 * Created by AMAL on 5/2/15.
 */
public class Photo implements Serializable {

    private int PhotoID;
    private String PhotoPath;


    public void setPhotoID(int PhotoID){
        this.PhotoID = PhotoID;
    }
    public int getPhotoID(){
        return PhotoID;
    }
public void setPhotoPath(String PhotoPath){
    this.PhotoPath = PhotoPath;
}
    public String getPhotoPath(){
     return PhotoPath;
    }
}
