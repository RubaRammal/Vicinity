package vicinity.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Request {
    private static final String TAG = "RequestClass";
    //Request Atts
    private int requestID;
    private User requestedBy; //sender
    private User requestedTo; //receiver
    private String requestedAt;
    private boolean requestStatus; //is accepted?
    private String deviceID;


    //Constructor
    public Request(User requestedBy, User requestedTo ,String deviceID){

        Date currentDate= new Date();
        DateFormat dF=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        requestedAt= dF.format(currentDate);
        this.requestedBy=requestedBy;
        this.deviceID=deviceID; //the sender of the request must send the device ID cause I dunno if we're able to get others' device IDs!
        requestStatus=false; //since it's just been sent and not accepted

    }

    //Setters - Getters
    public boolean setRequestedBy(User newFriend){
        if(newFriend.equals(null))
            return false;
        this.requestedBy=requestedBy;
        return true;
    }
    public User getRequestedBy(){
        return requestedBy;
    }


    //Methods
    public boolean isAccepted(){
        return this.requestStatus;
    }
    public void viewRequest(){

    }



}
