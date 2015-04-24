package vicinity.model;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class DBHandler extends SQLiteOpenHelper {


    private static final String TAG = "DBHandler";
    private static final String DB_PATH = "/data/data/vicinity.vicinity/databases/";
    private static final String DB_NAME="VicinityDatabase.db";
    private static final int DATABASE_VERSION = 3;
    private static Context myContext;
    private SQLiteDatabase vicinityDB;



    /**
     * Public constructor
     * @param context ,
     */
    public DBHandler(Context context){
        super(context, DB_NAME , null, DATABASE_VERSION);
        myContext=context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() throws IOException{

        boolean dbExist = checkDataBase();

        if(dbExist){
            Log.i(TAG,"External DB exists.");
        }
        else{
            this.getReadableDatabase();
            try {
                copyDataBase();
            }
            catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying
     * the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
        File dbFile = myContext.getDatabasePath(DB_NAME);
        Log.i(TAG,"Database exists? "+dbFile.exists());

        return dbFile.exists();
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    /**
     *Opens database for readonly
     * @throws SQLException
     */
    public void openDataBase() throws SQLException{
        String myPath = DB_PATH + DB_NAME;
        vicinityDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    /**
     * Deletes a copied database from the system
     * (NOT THE ASSETS' ONE)
     * @return boolean equals true if database was
     * deleted successfully, false otherwise.
     */
    public static boolean deleteDatabase(){
        boolean deleted=myContext.deleteDatabase(DB_NAME);
        Log.i(TAG,DB_NAME+" deleted? "+deleted);
        return deleted;
    }

    @Override
    public synchronized void close() {

        if(vicinityDB != null)
            vicinityDB.close();
        super.close();
    }



}
