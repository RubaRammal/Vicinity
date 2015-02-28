package vicinity.model;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;
import android.content.Context;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class DBHandler extends SQLiteOpenHelper {


    private static final String TAG = "DBHandler";
    private static final String DB_PATH = "/data/data/vicinity.vicinity/databases/";
    private static final String DB_NAME="VicinityDB.db";
    private static final int DATABASE_VERSION = 3;
    private final Context myContext;
    private SQLiteDatabase vicinityDB;
    //The following table must be created automatically whenever you add an external database
    //private static final String CREATE_TABLE_ANDROID="CREATE TABLE \"android_metadata\" (\"locale\" TEXT DEFAULT 'en_US');";
    //private static final String INSERT_ANDROID_VALUES="INSERT INTO \"android_metadata\" VALUES ('en_US');";


    /**
     * Public constructor
     * @param context ,
     */
    public DBHandler(Context context){
        super(context, DB_NAME , null, DATABASE_VERSION);
        this.myContext=context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
        createDataBase();}
        catch (Exception e){
            Log.i(TAG,"Error in database creation");
        }
        //db.execSQL(CREATE_TABLE_ANDROID);
        //db.execSQL(INSERT_ANDROID_VALUES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    /**
     * createDatabase()
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
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            Log.i(TAG,"Database does NOT exist");
        }
        if(checkDB != null){

            checkDB.close();//
        }
        return checkDB != null ? true : false;
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

    @Override
    public synchronized void close() {

        if(vicinityDB != null)
            vicinityDB.close();
        super.close();
    }



}
