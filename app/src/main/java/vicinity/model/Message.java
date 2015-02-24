package vicinity.model;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Message {
    private static final String TAG = "MessageClass";
    //private final Context getApplicationContext;

    //Message Attributes
    private String sentAt;
    private Friend sender;
    private Friend receiver;
    private String messageBody; //I'll leave it as a String for now.
    DBHandler dbHandler;/*I'm just trying out some db functions so there might be better ways of
                          accessing the db from different classes other than this way - Amal */


    //Constructor
    public Message(Context getApplicationContext, Friend sender, Friend receiver, String messageBody){
        //this.getApplicationContext = getApplicationContext;

        Date currentDate = new Date();
        DateFormat dF=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sentAt = dF.format(currentDate);
        this.sender=sender;
        this.receiver=receiver;
        this.messageBody=messageBody;
       // dbHandler = new DBHandler(this.getApplicationContext);


    }

    //Setters & Getters

    public String getDate(){
        return this.sentAt;
    }

    public boolean setSender(Friend sender){
        this.sender=sender;
        return true;
    }
    public User getSender(){
        return this.sender;
    }

    public boolean setReceiver(Friend receiver){
        this.receiver=receiver;
        return true;
    }
    public User getReceiver(){
        return this.receiver;
    }

    public boolean setMessageBody(String messageBody){
        this.messageBody=messageBody;
        return true;
    }
    public String getMessageBody(){
        return this.messageBody;
    }


    //Message Methods [to implement after adding the database]
    public boolean saveImage(){


        return false;
    }
    public void viewMessage(int messageID)  {



        //"FetchAll" should be used
        /*
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cur = null;

        db.close();
        */
    }


}
