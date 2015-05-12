package vicinity.model;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A structure that holds information about chat messages
 */
public class VicinityMessage implements Parcelable{

    private static final String TAG = "MessageClass";
    private static Context getApplicationContext;
    DBHandler dbh;
    SQLiteDatabase db;
    ContentResolver contentResolver = getContentResolver();
    CapturePhotoUtils capturePhotoUtils;
    ContextWrapper cw = new ContextWrapper(getApplicationContext);

    private long msgTimestamp;
    private String sentAt;
    private String friendID;
    private boolean isMyMsg;
    private String messageBody;
    private int chatId;
    private String imageString;

    public VicinityMessage(){}
    /**
     * Public constructor, initiates a message
     * attaches to it its timestamp and date
     * @param context activity context
     * @param friendId String
     * @param isMyMsg boolean
     * @param messageBody string
     */
    public VicinityMessage(Context context, String friendId, int chatId , boolean isMyMsg, String messageBody){

        getApplicationContext = context;
        this.friendID = friendId;
        this.messageBody = messageBody;
        this.isMyMsg = isMyMsg;
        this.chatId = chatId;
        //The following lines will create a string of the time & date the message was sent at
        //in order to be displayed with the message
        Date msgSentAt = new Date();
        DateFormat dF = new SimpleDateFormat("yyyy/MM/dd");
        sentAt = dF.format(msgSentAt);
        //VicinityMessage's timestamp: Messages for each friend shall be ordered according to this attribute.
        msgTimestamp = msgSentAt.getTime();
        imageString = "";

    }

    public VicinityMessage(Context context, String friendId, int chatId , boolean isMyMsg){

        getApplicationContext = context;
        this.friendID = friendId;
        this.isMyMsg = isMyMsg;
        this.chatId = chatId;
        //The following lines will create a string of the time & date the message was sent at
        //in order to be displayed with the message
        Date msgSentAt = new Date();
        DateFormat dF = new SimpleDateFormat("yyyy/MM/dd");
        sentAt = dF.format(msgSentAt);
        //VicinityMessage's timestamp: Messages for each friend shall be ordered according to this attribute.
        msgTimestamp = msgSentAt.getTime();
        imageString = "";

    }

    public VicinityMessage(Parcel in) {
        readFromParcel(in);
    }


    /**
     * setters/getters
     */
    public ContentResolver getContentResolver() {
        return contentResolver;
    }
    public void setFriendID(String friendID){
        this.friendID = friendID;
    }
    public String getFriendID(){return friendID;}

    public String getDate() {
        return this.sentAt;
    }

    public void setDate(String d) {
        sentAt = d;
    }
    public long getMsgTimestamp(){
        return msgTimestamp;
    }
    public boolean isMyMsg() {
        return isMyMsg;
    }
    public void setIsMyMsg(boolean isMyMsg){
        this.isMyMsg = isMyMsg;
    }
    public boolean setMessageBody(String messageBody) {
        this.messageBody = messageBody;
        return true;
    }
    public String getMessageBody() {
        return this.messageBody;
    }

    public int getChatId(){
        return chatId;
    }

    public void setChatId( int cid){
        chatId = cid;
    }

    public void setImageString( String img){
        imageString = img;
    }

    public String getImageString(){
        return imageString;
    }

    public static JSONObject getAsJSONObject(VicinityMessage msgrow) {
        JSONObject jsonobj = new JSONObject();
        try{
            jsonobj.put(Globals.MSG_SENDER, msgrow.getFriendID());
            jsonobj.put(Globals.MSG_ID, msgrow.getChatId());
            jsonobj.put(Globals.MSG_CONTENT, msgrow.getMessageBody());
            jsonobj.put(Globals.MSG_MINE, msgrow.isMyMsg());
            jsonobj.put(Globals.MSG_IMG, msgrow.getImageString());


        }catch(JSONException e){
           Log.i(TAG, "getAsJSONObject : " + e.toString());
        }
        return jsonobj;
    }


    /**
     * convert json object to message row.
     */
    public static VicinityMessage parseMessageRow(JSONObject jsonobj) {
        VicinityMessage row = null;
        if( jsonobj != null ){
            try{
                row = new VicinityMessage(getApplicationContext,
                        jsonobj.getString(Globals.MSG_SENDER),
                        Integer.parseInt(jsonobj.getString(Globals.MSG_ID)),
                        Boolean.parseBoolean(jsonobj.getString(Globals.MSG_MINE)),
                        jsonobj.getString(Globals.MSG_CONTENT));

            }catch(JSONException e){
                Log.i(TAG, "parseMessageRow: " + e.toString());
            }
        }
        return row;
    }

    /**
     * convert a json string representation of messagerow into messageRow object.
     */
    public static VicinityMessage parseMessageRow(String jsonMsg){
        JSONObject jsonobj = JSONUtils.getJsonObject(jsonMsg);
        Log.i(TAG, "parseMessageRow : " + jsonobj.toString());
        return parseMessageRow(jsonobj);
    }

    public static final Parcelable.Creator<VicinityMessage> CREATOR = new Parcelable.Creator<VicinityMessage>() {
        public VicinityMessage createFromParcel(Parcel in) {
            return new VicinityMessage(in);
        }

        public VicinityMessage[] newArray(int size) {
            return new VicinityMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(friendID);
        dest.writeString(messageBody);
        dest.writeString(String.valueOf(isMyMsg));
        dest.writeString(String.valueOf(chatId));

    }

    public void readFromParcel(Parcel in) {
        friendID = in.readString();
        messageBody = in.readString();
        isMyMsg = Boolean.parseBoolean(in.readString());
        chatId = Integer.parseInt(in.readString());

    }
}