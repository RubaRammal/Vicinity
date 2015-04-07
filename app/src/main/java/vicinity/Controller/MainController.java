package vicinity.Controller;


import vicinity.model.*;


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
    private ArrayList<VicinityMessage> allMessages;

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



    /**
     * Validates an input string (username) that shall contain letters, numbers, "-" and "_" ONLY
     * @param username A string
     * @return true if username matches the criteria, false other wise
     */
    public boolean nameValidation(String username){
        return !(username.isEmpty() || !username.matches("[a-zA-Z0-9_-]+"));
    }



    /**
     * NOTE: //Should be implemented after integrating p2p code
     * @return onlineUsers all online users (either friends or neighbors)
     */
    public ArrayList<User> viewOnlineUsers(){return null;}


    public boolean muteUser(User user){

        return true;}


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
            values.put("username", newFriend.getInstanceName());
            //values.put("id", newFriend.getFriendID());
            isAdded=database.insert("Friend", null, values)>0;
            dbH.close();
        }
        catch(SQLException e){
            Log.i(TAG,"SQLException > addFriend > MainController");
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
            Log.i(TAG,"SQLException > deleteFriend > MainController");
            e.printStackTrace();
        }
        return isDeleted;
    }

    //UNTESTED: DB needs editing
    /**
     * Fetches user's friends from the database
     * In order to be displayed.
     * @return friendsList
     */
    public ArrayList<Friend> viewFriendsList(){

        friendsList=new ArrayList<>();

        try{
            database=dbH.getReadableDatabase();
            dbH.openDataBase();
            String query="SELECT * FROM Friend";
            Cursor c = database.rawQuery(query,null);
            if (c.moveToFirst()) {
                do {

                    Friend myFriend = new Friend();
                    myFriend.setInstanceName(c.getString(1)); //getting username from database column #: 1
                    //myFriend.setStatus(myFriend.isOnline());
                    myFriend.setAliasName(c.getString(3));
                    friendsList.add(myFriend);

                } while (c.moveToNext());
            }
            else{
                Log.i(TAG, "There are no friends in the DB.");
            }
            dbH.close();
        }
        catch(SQLException e){
            Log.i(TAG,"SQLException > viewFriendsList > MainController");
            e.printStackTrace();
        }

        return friendsList;}//end of viewFriendsList


    /**
     * Fetches user's Requests from the database
     * @return requestsList
     */
    public ArrayList<Request> viewAllRequests()
    {

        requestsList=new ArrayList<Request>();
        Log.i("Sarah's message", "entered the view all requests method");



        try
        {
            database=dbH.getReadableDatabase();
            dbH.openDataBase();
            String query="SELECT * FROM Request";
            Cursor c = database.rawQuery(query,null);
            if (c.moveToFirst())
            {
                do
                {
                    Request requestObj= new Request();
                    requestObj.setReqBy(new User (c.getString(1)));
                    requestObj.setRequestStatus(c.getString(2));
                    requestsList.add(requestObj);
                } while (c.moveToNext());
            }
            else
            {
                Log.i(TAG, "There are no requests in the DB.");
            }
            dbH.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return requestsList;
    }

    public boolean acceptRequest(int num){return true;}

    public boolean denyRequest(int num){return true;}

    public boolean sendRequest(User user){return true;}

    /**
     * Fetches posts from the database
     * @return postList
     */
    public ArrayList<Post> viewAllPosts()

    {
        postList = new ArrayList<Post>();

        try
        {
            database = dbH.getReadableDatabase();
            dbH.openDataBase();
            String query = "SELECT * FROM Post";
            Cursor c = database.rawQuery(query, null);
            if (c.moveToFirst())
            {
                do
                {
                    Post post = new Post();
                    post.setPostBody(c.getString(1));
                    post.setPostedBy(new User(c.getString(2)));
                    //contact.setPicture(c.getBlob(3));

                    // Adding post to postList
                    postList.add(post);
                } while (c.moveToNext());
            }

            else
            {
                Log.i(TAG, "There are no posts in the DB.");
            }
            dbH.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return postList;
    }


    /**
     * Adds a new Post to the database.
     * @param post An object of class Post
     * @return isAdded true if the post was added successfully, false otherwise.
     * @throws SQLException
     */
    public boolean addPost(Post post) throws SQLException
    {
        boolean isAdded=false;
        try
        {
            database = dbH.getReadableDatabase();
            dbH.openDataBase();
            ContentValues values = new ContentValues();
            values.put("post", post.getPostBody());
            isAdded=database.insert("Post", null, values)>0;
            dbH.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return isAdded;}

    /**
     * Fetches user's Chats from the database
     * @return allMessages
     */
    public ArrayList<VicinityMessage> viewAllMessages()

    {


        try
        {
            database=dbH.getReadableDatabase();
            dbH.openDataBase();
            String query="SELECT * FROM Message";
            Cursor c = database.rawQuery(query,null);
            if (c.moveToFirst())
            {
                do
                {
                    VicinityMessage msg = new VicinityMessage();
                    msg.setMessageBody(c.getString(2));
                    msg.setFriendID(c.getString(1));
                    //msg.setTime(c.getString(4));
                    //contact.setPicture(c.getBlob(3));

                    // Adding message to allMessages
                    allMessages.add(msg);
                } while (c.moveToNext());
            }
            else
            {
                Log.i(TAG, "There are no messages in the DB.");
            }

            dbH.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }


        return allMessages;
    }

    /**
     * Deletes a message from the database given an ID
     * @param messageID A to-be deleted Message's ID.
     * @return isDeleted A boolean that equals true if operation is successful, false otherwise.
     */
    public boolean deleteMessage(int messageID)
    {
        boolean isDeleted=false;
        try
        {
            database=dbH.getReadableDatabase();
            dbH.openDataBase();
            isDeleted=database.delete("Message","_id="+"'"+messageID+"'",null)==1;
            Log.i(TAG,"Is Message deleted? "+isDeleted);
            dbH.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }


        return isDeleted;

    }


}
