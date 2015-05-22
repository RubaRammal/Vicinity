package vicinity.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class implements Serializable.
 * It is a structure that holds information about chat messages.
 */
public class VicinityMessage implements Serializable{

    private static final String TAG = "MessageClass";

    private String sentAt;
    private String friendName;
    private String from;
    private boolean isMyMsg;
    private String messageBody;
    private String imageString;


    public VicinityMessage(){}
    /**
     * Public constructor, initiates a message
     * attaches to it its timestamp and date
     * @param friendName String
     * @param isMyMsg boolean
     * @param messageBody string
     */
    public VicinityMessage(String friendName , boolean isMyMsg, String messageBody){

        this.friendName = friendName;
        this.messageBody = messageBody;
        this.isMyMsg = isMyMsg;
        //The following lines will create a string of the time & date the message was sent at
        //in order to be displayed with the message
        Date msgSentAt = new Date();
        DateFormat dF = new SimpleDateFormat("yyyy/MM/dd");
        sentAt = dF.format(msgSentAt);
        //VicinityMessage's timestamp: Messages for each friend shall be ordered according to this attribute.
        imageString = "";

    }


    /**
     * Setters And Getters
     */

    public void setFriendName(String friendName)
    {
        this.friendName = friendName;
    }
    public String getFriendName()
    {
        return friendName;
    }

    public String getDate()
    {
        return this.sentAt;
    }

    public void setDate(String d)
    {
        sentAt = d;
    }

    public boolean isMyMsg()
    {
        return isMyMsg;
    }

    public void setIsMyMsg(boolean isMyMsg)
    {
        this.isMyMsg = isMyMsg;
    }

    public boolean setMessageBody(String messageBody)
    {
        this.messageBody = messageBody;
        return true;
    }

    public String getMessageBody()
    {
        return this.messageBody;
    }

    public void setImageString( String img)
    {
        imageString = img;
    }

    public String getImageString()
    {
        return imageString;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getFrom()
    {
        return from;
    }


}