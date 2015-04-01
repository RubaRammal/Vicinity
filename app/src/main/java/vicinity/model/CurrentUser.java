package vicinity.model;

import android.content.Context;
import java.sql.SQLException;
import android.util.Log;
import android.database.Cursor;


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
        Globals.dbH=new DBHandler(context);
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



    /**
     *
     * Retrieves current user's username from database
     * This method is called in the service each time the user opens the app
     * to fetch the username and make it as the device name.
     * @return a username String
     * @throws java.sql.SQLException
     *
     */
    public static String retrieveCurrentUsername()throws SQLException{
        String username2=null;

        try {
            Globals.database = Globals.dbH.getReadableDatabase();
            Globals.dbH.openDataBase();
            String query="SELECT username FROM User";
            Cursor c = Globals.database.rawQuery(query,null);
            c.moveToFirst();
            username2=c.getString(c.getColumnIndex("username"));
            c.close();
            Globals.dbH.close();
            return username2;
        }
        catch (SQLException e){
            Log.i(TAG,"SQLException IN retrieveCurrentUser > currentUser");
        }
        return username2;
    }

    /**
     * This method wipes out the user's account along with its data (friends, messages..etc)
     * If the user wants to start a new account.
     * @return boolean if the operation is successful
     */
    public boolean destroyUser(){
        return false;
    }

}
