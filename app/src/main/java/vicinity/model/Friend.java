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
    public Friend(String username) {
        super(username);
        UUID randomID = UUID.randomUUID();//UUID Generates a universally unique ID for each friend
        _id= randomID.toString();

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


    //[after database implementation]
    public void viewFriend(){

    }

    public boolean sendMessage(Message newMessage){
        return false;
    }


}
