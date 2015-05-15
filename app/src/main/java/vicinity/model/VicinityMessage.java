package vicinity.model;

import android.content.Context;
import android.os.StrictMode;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A structure that holds information about chat messages
 */
public class VicinityMessage implements Serializable{

    private static final String TAG = "MessageClass";
    private static Context getApplicationContext;


    private long msgTimestamp;
    private String sentAt;
    private String friendID;
    private String from;
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



    /**
     * setters/getters
     */

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

    public void setFrom(String from)  {
        this.from = from;
    }


    public String getFrom(){
        return from;
    }





}