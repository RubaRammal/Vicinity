package vicinity.model;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import java.sql.SQLException;
import java.util.ArrayList;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import vicinity.Controller.MainController;

public class Friend extends User{

    private static final String TAG = "FriendClass";
    private SQLiteDatabase database;
    private DBHandler dbH;

    private String _id;
    private ArrayList<Message> _privateMessages;//I don't think we need this array here -AFNAN
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
        super(username);
        dbH = new DBHandler(context);
        this._id=_id;
    }


    /**
     *
     * Setters-getters
     */
    public void setID(String id){this._id=id;}
    public String getFriendID(){
        return this._id;
    }
    public String getAliasName(){
        return this._aliasName;
    }
    public void setAliasName(String newName){_aliasName=newName;}
    public ArrayList<Message> getPrivateMessages(){
        return this._privateMessages;
    }








    //INCOMPLETE: must add column: AliasName to the db in Friend's table. -AFNAN
    /**
     * This method calls nameValidation from MainController to validate the new username
     * then adds it to the database as an alias name
     * @param aliasName A string that contains a new alias name for a certain friend
     * @param friendID A string that contains the id of the friend with the alias name
     * @return isUpdated A boolean that is true if the name was changed, false otherwise
     */
    public boolean changeName(String aliasName, String friendID) throws SQLException{
       MainController controller = new MainController();
       boolean isUpdated=false;
       if(controller.nameValidation(aliasName))
       {
           this._aliasName = aliasName;
           try{
            database = dbH.getReadableDatabase();
            dbH.openDataBase();
            ContentValues args = new ContentValues();
            args.put("aliasname", aliasName);
            isUpdated= database.update("User", args, "_id=" + friendID, null)>0;
            dbH.close();
           }
            catch(SQLiteException e){
                Log.i(TAG,"SQLiteException > Friend > ChangeName");
            e.printStackTrace();
            }
       }
        Log.i(TAG,"Is Updated? "+isUpdated);
        return isUpdated;
    }


    /**
     *
     * @return

    public Friend retrieveFriend(String friendID){
        Friend friend=null;
        try{
            String friendName;
            boolean status;
            database=dbH.getReadableDatabase();
            dbH.openDataBase();
            String query="SELECT * FROM Friend WHERE _id="+friendID;
            Cursor c= database.rawQuery(query,null);
            /**
             * String un, id;
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
        catch(SQLException e){

        }
        return friend;
    }
     */
    public boolean sendMessage(Message newMessage){
        return false;
    }


}
