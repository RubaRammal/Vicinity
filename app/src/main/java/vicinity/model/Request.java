package vicinity.model;

import android.util.Log;

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
    private String requestStatus1;


    //Constructor
    public Request ()
    {

    }
    public Request(User requestedBy, User requestedTo){

        Date currentDate= new Date();
        DateFormat dF=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        requestedAt= dF.format(currentDate);
        this.requestedBy=requestedBy;
        requestStatus=false; //since it's just been sent and not accepted, yet

    }

    public void setReqBy(User newFriend)
    {
        if(newFriend.getUsername().equals(null))
            requestedBy=null;
        else
            requestedBy=newFriend;


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

    //Added setRequestStatus Method because i will need it in the MainController Class -Sarah
    public void setRequestStatus(String requestStatus){
        if(requestStatus.equals("false"))
            requestStatus1="false";

        else
            this.requestStatus1 ="true";

    }

    //Methods
    public boolean isAccepted(){
        return this.requestStatus;
    }
    public void viewRequest(){

    }

    public String toString()
    {
        return "Requested by: "+requestedBy.getUsername()+"  Request Status: "+requestStatus1;

    }




}