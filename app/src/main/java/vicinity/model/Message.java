package vicinity.model;

import android.content.ContentResolver;
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


    /**
     * Public constructor, initiates a message
     * attaches to it its timestamp and date
     * @param context activity context
     * @param friendID string
     * @param isMyMsg boolean
     * @param messageBody string
     */
    public Message(Context context, String friendID, boolean isMyMsg, String messageBody){

        getApplicationContext=context;
        this.friendID=friendID;
        this.messageBody=messageBody;
        this.isMyMsg = isMyMsg;
        //The following lines will create a string of the time & date the message was sent at
        //in order to be displayed with the message
        Date msgSentAt= new Date();
        DateFormat dF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sentAt = dF.format(msgSentAt);
        //Message's timestamp: Messages for each friend shall be ordered according to this attribute.
        msgTimestamp = msgSentAt.getTime();

    }


    /**
     * setters/getters
     */
    public ContentResolver getContentResolver() {
        return contentResolver;
    }
    public void setFriendID(String friendID){
        this.friendID=friendID;
    }
    public String getFriendID(){return friendID;}
    public String getDate() {
        return this.sentAt;
    }
    public long getMsgTimestamp(){
        return msgTimestamp;
    }
    public boolean isMyMsg() {
        return isMyMsg;
    }
    public void setIsMyMsg(boolean isMyMsg){
        this.isMyMsg=isMyMsg;
    }
    public boolean setMessageBody(String messageBody) {
        this.messageBody = messageBody;
        return true;
    }
    public String getMessageBody() {
        return this.messageBody;
    }



    /**
     * Adds a single message to the database.
     * @param newMessage a new Message object to be added in the db
     * @return isAdded a boolean that is true if the operation is successful, false otherwise
     * @throws SQLException
     */
    public boolean addMessage(Message newMessage ) throws SQLException {

        boolean isAdded=false;

        try {
            db = dbh.getWritableDatabase();
            dbh.openDataBase();
            ContentValues values = new ContentValues();
            values.put("message", newMessage.getMessageBody());
            //values.put("time", newMessage.getMsgTimestamp()); // I think we need generate time automatically in the db -AFNAN
            values.put("isMyMsg",newMessage.isMyMsg());
            values.put("friend_id",newMessage.getFriendID());
            isAdded=db.insert("Message", null, values)>0;

            dbh.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isAdded;
    }

    /** UNTESTED METHODS
     *  WILL BE TESTED AFTER CONNECTING
     *  TO THE INTERFACES
     * */

    /**
     * this method saves the image to the gallery and not to the DB
     * */

    private void saveImage(Bitmap bitmapImage){

        //this single line of code is only if we want to save the image to the phone gallery along with all the images
        //MediaStore.Images.Media.insertImage(getContentResolver(), yourBitmap, yourTitle , yourDescription);

        capturePhotoUtils = new CapturePhotoUtils();
        capturePhotoUtils.insertImage(contentResolver, bitmapImage, " " , " ");
    }

    /**
     * Inserts image to the DB
     * */

    private String insertImage(Bitmap bitmapImage){//inserts the Image to the DB


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

    /**
     * Retrieves image from DB
     * */

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

    //this method can be replaced by alternatives (e.g. list) after connecteing to the interfaces
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
}