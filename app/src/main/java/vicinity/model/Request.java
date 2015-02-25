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


    //Constructor
    public Request(User requestedBy, User requestedTo){

        Date currentDate= new Date();
        DateFormat dF=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        requestedAt= dF.format(currentDate);
        this.requestedBy=requestedBy;
        requestStatus=false; //since it's just been sent and not accepted, yet

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
