package vicinity.Controller;


import vicinity.model.CurrentUser;
import vicinity.model.DBHandler;
import vicinity.model.Friend;
import vicinity.model.Message;
import vicinity.model.Post;
import vicinity.model.Request;
import vicinity.model.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.sql.SQLException;
import java.util.ArrayList;


public class MainController {

    private static final String TAG = "Main Controller";
    private SQLiteDatabase database;
    private DBHandler dbH;
    private Context context;
    private ArrayList<User> onlineUsers;
    private ArrayList<Friend> friendsList;
    private ArrayList<Request> requestsList;
    private ArrayList<Post> postList;
    private ArrayList<Message> allMessages;

    /**
     * Default constructor
     */
    public MainController(){}
    /**
     * Public constructor
     * @param context An application context to instantiate the database
     */
    public MainController(Context context){
        dbH=new DBHandler(context);
        this.context=context;

    }

    /**
     * Creates a new user, this method shall be used once only
     * when the user launches the app for the first time
     * @param username A string
     * @return true if the user was created, false other wise
     */
    public boolean createNewUser(String username){
        boolean isCreated=false;
        CurrentUser newUser=new CurrentUser(context,username);
        try{
            database = dbH.getReadableDatabase();
            dbH.openDataBase();
            database.execSQL("INSERT INTO User (username) VALUES ('" + newUser.getUsername() + "');");
            isCreated=true;
            dbH.close();
        }
        catch (SQLException e){
            Log.i(TAG,"SQLEXception IN createProfile > currentUser");
        }
        return isCreated;
    }


    //NOTE ON nameVaildation: this method was implemented twice in Friend's class and MainActivity
    //so i thought I'd implement it here then it'd only be called, to avoid code redundancy -AFNAN
    /**
     * Validates an input string (username) that shall contain letters, numbers, "-" and "_" ONLY
     * @param username A string
     * @return true if username matches the criteria, false other wise
     */
    public boolean nameValidation(String username){
        if(username.isEmpty() ||!username.matches("[a-zA-Z0-9_-]+"))
            return false;
        return true;
    }

    public User[] viewOnlineUsers(){return null;}

    public boolean muteUser(User user){ return true;}


    //works but we need to edit _id column in database -AFNAN
    /**
     * Adds a new Friend to the database.
     * @param newFriend An object of class Friend
     * @return isAdded true if the friend was added successfully, false otherwise.
     * @throws SQLException
     */
    public boolean addFriend(Friend newFriend)throws SQLException{
        boolean isAdded=false;
        try{
            database = dbH.getReadableDatabase();
            dbH.openDataBase();
            ContentValues values = new ContentValues();
            values.put("username", newFriend.getUsername());
            //values.put("id", newFriend.getFriendID());
            isAdded=database.insert("Friend", null, values)>0;
            dbH.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return isAdded;
    }


    //NEEDS TO BE TESTED AGAIN AFTER EDITING THE DATABASE -AFNAN
    /**
     * Deletes a friend from the database given an ID
     * @param friendID A to-be deleted friend's ID.
     * @return isDeleted A boolean that equals true if operation is successful, false otherwise.
     */
    public boolean deleteFriend(String friendID){
        boolean isDeleted=false;
        try{
            database=dbH.getReadableDatabase();
            dbH.openDataBase();
            isDeleted=database.delete("Friend","_id="+"'"+friendID+"'",null)==1;
            Log.i(TAG,"Is friend deleted? "+isDeleted);
            dbH.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return isDeleted;
    }

    //INCOMPLETE
    /**
     * Fetches user's friends from the database
     * @return friendsList
     */
    public ArrayList<Friend> viewFriendsList(){

        friendsList=new ArrayList<Friend>();

        try{
            database=dbH.getReadableDatabase();
            dbH.openDataBase();
            String query="SELECT * FROM Friend";
            Cursor c = database.rawQuery(query,null);
            if (c.moveToFirst()) {
                do {
                    //STORING DATA HERE
                } while (c.moveToNext());
            }
            dbH.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return null;}

    public ArrayList<Request> viewAllRequests(){return null;}

    public boolean acceptRequest(int num){return true;}

    public boolean denyRequest(int num){return true;}

    public boolean sendRequest(User user){return true;}

    public ArrayList<Post> viewAllPosts(){ return null;}

    public boolean addPost(Post post){return true;}

    public ArrayList<Message> viewAllMessages(){return null;}

    public boolean deleteMessage(int num){return true;}












}
