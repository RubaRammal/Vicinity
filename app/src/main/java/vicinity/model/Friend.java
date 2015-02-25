package vicinity.model;

import java.util.UUID;

public class Friend extends User{
    private static final String TAG = "FriendClass";

    //Friend Atts
    private String _id;
    private Message[] _privateMessages;
    private boolean _status;
    private String _aliasName;



    //Constructor
    public Friend(String username,String _id) {
        super(username);
        this._id=_id;
    }


    //Setters & Getters

    public String getFriendID(){
        return this._id;
    }
    public String getAliasName(){
        return this._aliasName;
    }
    public Message[] getPrivateMessages(){
        return this._privateMessages;
    }
    public void setStatus(boolean status){
        this._status = status;
    }
    public boolean isOnline(){
        return this._status;
    }



    //Methods
    public boolean changeName(String aliasName){
        //Alias validation
       if(aliasName.isEmpty() || !aliasName.matches("[a-zA-Z0-9_-]+"))
           return false;
       this._aliasName = aliasName;
        return true;
    }
    /**
     *


    public boolean changeUsername(String newUsername)throws SQLException{
    boolean isUpdated=false;
    try {

    database = dbH.getReadableDatabase();
    dbH.openDataBase();
    ContentValues args = new ContentValues();
    args.put("username", newUsername);
    isUpdated= database.update("User", args, "_id=" + 1, null)>0;
    Log.i(TAG,"Is Updated? "+isUpdated);
    dbH.close();

    }
    catch (SQLException e){
    Log.i(TAG,"SQLEXception IN retrieveCurrentUser > currentUser");
    }

    return isUpdated;
    }
     */

    //[after database implementation]
    public void viewFriend(){

    }

    public boolean sendMessage(Message newMessage){
        return false;
    }


}
