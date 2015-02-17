package vicinity.model;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.io.IOException;
import android.util.Log;

public class CurrentUser extends User {

    private int _id;
    private SQLiteDatabase database;
    private DBHandler dbH;
    private static final String TAG = "CurrentUser";

//Setters and Getters
    public int getId() {
        return _id;
    }
    public void setId(int id) {
        this._id = id;
    }



//Constructor (it must take a Context object in order to be passed to the database
    public CurrentUser(Context context, String username){
        super(username);
        dbH=new DBHandler(context);
    }



//Methods

    /**
     * Creates a Profile table for the user when the app is first launched
     * @param newUser
     * @throws IOException
     * @throws java.sql.SQLException
     */
    public void createProfile (CurrentUser newUser)throws IOException, java.sql.SQLException{

        database = dbH.getReadableDatabase();

        try{
            dbH.createDataBase();
            dbH.openDataBase();
            database.execSQL("INSERT INTO User (username) VALUES ('"+newUser.getUsername()+"');");
            Log.i(TAG, "User added from CurrentUser class");
            dbH.close();
        }
        catch(IOException e){
            Log.i(TAG,"DATABASE ERROR IN createProfile > currentUser");
        }
        catch (SQLException e){
            Log.i(TAG,"SQLEXception IN createProfile > currentUser");
        }

    }


    /**
     * Retrieves user profile from database
     * @param user
     */
    public void retrieveProfile(CurrentUser user){
        String _username = user.getUsername();

        Log.i(TAG, "retrieveProfile test");

    }
}
