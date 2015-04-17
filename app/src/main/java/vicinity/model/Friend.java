package vicinity.model;
import android.content.ContentValues;
import java.sql.SQLException;
import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import vicinity.ConnectionManager.WiFiP2pService;
import vicinity.Controller.MainController;

/**
 * A structure class for holding friends information
 * this class extends WiFiP2pService
 */
public class Friend extends WiFiP2pService{

    private static final String TAG = "FriendClass";

    private ArrayList<VicinityMessage> _privateMessages;
    private String _aliasName;

    /**
     * Default public constructor
     */
    public Friend (){

    }

    /**
     * Public constructor
     * @param context application context
     * @param username A String that contains the friend's name
     * @param _id A User's device ID
     */
    public Friend(Context context,String username,String _id) {

    }


    /**
     *
     * Setters-getters
     */
    public String getAliasName(){
        return this._aliasName;
    }
    public void setAliasName(String newName){_aliasName=newName;}
    public ArrayList<VicinityMessage> getPrivateMessages(){
        return this._privateMessages;
    }






}
