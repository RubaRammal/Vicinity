package vicinity.model;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private String messageBody; //I'll leave it as a String for now.



    //Constructor
    public Message(Context getApplicationContext, Friend sender, Friend receiver, String messageBody) {
        this.getApplicationContext = getApplicationContext;

        Date currentDate = new Date();
        DateFormat dF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sentAt = dF.format(currentDate);
        this.sender = sender;
        this.receiver = receiver;
        this.messageBody = messageBody;




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
            String[] data = null;

            if (cursor.moveToFirst()) {
                do {
                    // get  the  data into array,or class variable, instead i'm printing it for now
                  System.out.print(DatabaseUtils.dumpCursorToString(cursor));

                } while (cursor.moveToNext());


            }
                db.close();

        }



}



