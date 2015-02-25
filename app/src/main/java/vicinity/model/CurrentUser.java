package vicinity.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.sql.SQLException;
import java.io.IOException;
import android.provider.Settings;
import android.util.Log;
import android.database.Cursor;


//There's something wrong with the logic of this class ._. -AFNAN
//I thought we could only have the super class User
//and then Friend extends it and overrides methods like createProfile since it uses a different table in the db
//but still we have a problem with Stranger.class, we don't need all of these methods there.

public class CurrentUser extends User {

    private SQLiteDatabase database;
    private DBHandler dbH;
    private String _userID;
    private static final String TAG = "CurrentUser";



    /**Getters/Setters
    */
    public String getId() {
        return _userID;
    }
    public void setId(String id) {
        this._userID = id;
    }


    /**
     * Public Constructor
     * @param context
     * @param username
     */
    public CurrentUser(Context context, String username){
        super(username);
        dbH=new DBHandler(context);
        _userID = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        //ANDROID_ID is a 64-bit number (as a hex string) that is randomly generated when the user first sets up the device
        //and should remain constant for the lifetime of the user's device.
    }

    /**
     * This constructor is user for the methods bellow
     * @param username
     * @param _userID
     */
    public CurrentUser(String username, String _userID){
        super(username);
        this._userID=_userID;
    }

    /**
     * Creates a Profile table for the user when the app is first launched
     * @param newUser
     * @throws IOException
     * @throws java.sql.SQLException
     */
    public void createProfile (CurrentUser newUser)throws SQLException{

        database = dbH.getReadableDatabase();
        try{
            dbH.openDataBase();
            database.execSQL("INSERT INTO User (username) VALUES ('" + newUser.getUsername() + "');");
            Log.i(TAG, "User added from CurrentUser class");
            Log.i(TAG,"This is the user ID: "+_userID);
            dbH.close();
        }
        catch (SQLException e){
            Log.i(TAG,"SQLEXception IN createProfile > currentUser");
        }
    }


    /**
     *
     * Retrieves current user's information
     * @return a CurrentUser object
     * @throws IOException
     * @throws java.sql.SQLException
     * This method could be used in order to send friend requests.
     */
    public CurrentUser retrieveCurrentUser()throws SQLException{

        try {
            CurrentUser thisUser;
            String un, id;
            database = dbH.getReadableDatabase();
            dbH.openDataBase();
            String query="SELECT * FROM User";
            Cursor c = database.rawQuery(query,null);
            c.moveToFirst();
            //There's no loop because we'll be retrieving the only row in the db
            un=c.getString(c.getColumnIndex("username"));
            id=c.getString(c.getColumnIndex("_id"));
            thisUser= new CurrentUser(un,id);

            dbH.close();
            return thisUser;
        }
        catch (SQLException e){
            Log.i(TAG,"SQLEXception IN retrieveCurrentUser > currentUser");
        }

        return null;
    }

    /**
     * This method wipes out the user's account along with its data (friends, messages..etc)
     * If the user wants to start a new account.
     * @return boolean if the operation is successful
     */
    //WE'LL DISCUSS THIS METHOD LATER
    public boolean destroyUser(){
        return false;
    }

}
