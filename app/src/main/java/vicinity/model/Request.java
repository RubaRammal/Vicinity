package vicinity.model;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Request {
    private static final String TAG = "RequestClass";
    //Request Atts
    private String requestedBy; //sender
    private String requestedAt;
    private boolean requestStatus; //is accepted?
    private String requestStatus1;


    //Constructor
    public Request ()
    {

    }
    public Request(String requestedBy, String requestedTo){

        Date currentDate= new Date();
        DateFormat dF=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        requestedAt= dF.format(currentDate);
        this.requestedBy=requestedBy;
        requestStatus=false; //since it's just been sent and not accepted, yet

    }

    public void setReqBy(String newFriend)
    {
        if(newFriend.equals(null))
            requestedBy=null;
        else
            requestedBy=newFriend;


    }
    //Setters - Getters
    public boolean setRequestedBy(String newFriend){
        if(newFriend.equals(null))
            return false;
        this.requestedBy=requestedBy;
        return true;
    }
    public String getRequestedBy(){
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
        return "Requested by: "+requestedBy+"  Request Status: "+requestStatus1;

    }




}
