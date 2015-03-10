package vicinity.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Message {
    private static final String TAG = "MessageClass";
    private static Context getApplicationContext = null;
    DBHandler dbh;
    SQLiteDatabase db;
    //Message Attributes
    private String sentAt;
    private Friend sender;
    private Friend receiver;
    private String messageBody; //I'll leave it as a String for now.(I think we should keep it as a string - Amal)




    //Constructor
    public Message(Context getApplicationContext, Friend sender, Friend receiver, String messageBody) {
        this.getApplicationContext = getApplicationContext;

        Date currentDate = new Date();
        DateFormat dF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sentAt = dF.format(currentDate);
        this.sender = sender;
        this.receiver = receiver;
        this.messageBody = messageBody;
        //this.senderID = sender.getFriendID();
        //this.receiverID = receiver.getFriendID();





    }

    //Setters & Getters

    public Context getApplicationContext() {
        return getApplicationContext();
    }
    public String getDate() {
        return this.sentAt;
    }

    public boolean setSender(Friend sender) {
        this.sender = sender;
        return true;
    }

    public User getSender() {
        return this.sender;
    }

    public boolean setReceiver(Friend receiver) {
        this.receiver = receiver;
        return true;
    }

    public User getReceiver() {
        return this.receiver;
    }

    public boolean setMessageBody(String messageBody) {
        this.messageBody = messageBody;
        return true;
    }

    public String getMessageBody() {
        return this.messageBody;
    }


    //Message Methods [to implement after adding the database]
    //we might not need this method since i read that android provides the option of saving an image by default
    private String saveImage(Bitmap bitmapImage){
        //this single line of code is only if we want to save the image to the phone gallery along with all the images
        //MediaStore.Images.Media.insertImage(getContentResolver(), yourBitmap, yourTitle , yourDescription);


        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/Vicinity/app_data/imageDir
        File directory = cw.getDir("imageDir" , Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory , "profile.jpg");

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

       return directory.getAbsolutePath();

    }



    private void loadImage(String path){
        try {
        File f = new File(path , "profile.jpg");
        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
        ImageView img = null;
           // img = (ImageView) findViewById(android.support.v7.appcompat.R.id.imgPicker);
           //img.setImageBitmap(b);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    public void viewMessage(int messageID) throws SQLException{

            String Table_Name = "Message";
            String selectQuery = "SELECT * FROM" + Table_Name + "WHERE _ID=" + messageID;


        try {
            db = dbh.getReadableDatabase();
            dbh.openDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Cursor cursor = db.rawQuery(selectQuery, null);
            String[] data = null;// we need to use this later in order to store the message

            if (cursor.moveToFirst()) {
                do {
                    // get the  data into array,or class variable, instead i'm printing it for now

                    Log.i(TAG, DatabaseUtils.dumpCursorToString(cursor));

                } while (cursor.moveToNext());


            }
                db.close();

        }

      //we could instead make this method receive nothing and make it call the above setters and getters in case we don't need the ID's
      public void insertMessage(Friend sender, Friend receiver, String messageBody ) throws SQLException {

          String Table_Name = "Message";

          //I think we need to add reciever_id to the message table, what do you think?
          // so that we can retrieve the message using both the sender and the receiver ID along with message id
          //the message id should be supplied by the database automatically

          //String insertQuery = "INSERT INTO"+ Table_Name +"(friend_id , reciever_id , message ) VALUES ('" + sender.getFriendID() + "','" + receiver.getFriendID() +"','"+ messageBody + "')";
          // Log.v("Test Saving", insertQuery);
          //db.rawQuery(insertQuery , null );

          try {
              db = dbh.getWritableDatabase();
              dbh.openDataBase();
          } catch (Exception e) {
              e.printStackTrace();
          }



          ContentValues values = new ContentValues();

          values.put("friend_id" , sender.getFriendID());

          values.put("reciever_id" , receiver.getFriendID());

          values.put("Message" , messageBody);

          db.insertOrThrow("Message" , null , values);


          db.close();




      }


}



