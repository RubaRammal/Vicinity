package vicinity.model;

import android.content.Context;


/**
 * This class holds the structure of the current app user.
 */
public class CurrentUser {


    private  static final String TAG = "CurrentUser";
    private  String username;



    /**
     * Public Constructor initializes username, saves it to the database
     * and changes the device name accordingly
     * @param context an activity context
     * @param username New user's username.
     */
    public CurrentUser(Context context, String username){
        this.username=username;

    }

    /**
     * setters/getters
     */
    public void setUsername(String username){
        this.username=username;
    }
    public String getUsername(){
        return this.username;
    }







}
