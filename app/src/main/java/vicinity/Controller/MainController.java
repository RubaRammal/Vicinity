package vicinity.Controller;


import vicinity.ConnectionManager.WiFiP2pService;
import vicinity.model.*;
import vicinity.vicinity.TimelineSectionFragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;


public class MainController {

    private static final String TAG = "Main Controller";
    private SQLiteDatabase database;
    private DBHandler dbH;
    private Context context;
    private ArrayList<Friend> friendsList;
    private ArrayList<Request> requestsList;
    private ArrayList<Post> postList;
    private ArrayList<VicinityMessage> allMessages;
    public String query;
    public Cursor cursor;




    /**
     * Public constructor
     * @param context An application context to instantiate the database
     */
    public MainController(Context context){
        dbH=new DBHandler(context);
        this.context=context;

    }
/*****************************************User's methods**********************************************/

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
            database.execSQL("INSERT INTO CurrentUser (Username) VALUES ('" + newUser.getUsername() + "');");
            isCreated=true;
            dbH.close();
        }
        catch (SQLException e){
            Log.i(TAG, "SQLEXception IN createProfile > currentUser");
        }
        return isCreated;
    }

    /**
     *
     * Retrieves current user's username from database
     * This method is called in the service each time the user opens the app
     * to fetch the username and make it as the device name.
     * @return a username String
     * @throws java.sql.SQLException
     *
     */
    public String retrieveCurrentUsername()throws SQLException{
        String username2=null;

        try {
            database = dbH.getReadableDatabase();
            dbH.openDataBase();
            query="SELECT Username FROM CurrentUser";
            cursor = database.rawQuery(query,null);
            cursor.moveToFirst();
            username2=cursor.getString(cursor.getColumnIndex("Username"));
            cursor.close();
            dbH.close();
            return username2;
        }
        catch (SQLException e){
            Log.i(TAG,"SQLException IN retrieveCurrentUser > currentUser");
        }
        return username2;
    }

    /**
     * This method mutes user by searching for a peer with the given
     * device address in the neighbours list and deleting the user
     * It also deletes posts & comments by this specific user as well
     * @param userAddress Device address of the to-be muted user
     * @return isMuted true if the user was muted, false otherwise
     */
    public boolean muteUser(String userAddress){
        boolean isMuted = false;

        return isMuted;
    }


/***************************************Friend's Methods********************************************************/


    /**
     * Validates an input string (username) that shall contain letters, numbers, "-" and "_" ONLY
     * @param username A string
     * @return true if username matches the criteria, false other wise
     */
    public boolean nameValidation(String username){
        return !(username.isEmpty() || !username.matches("[a-zA-Z0-9_-]+"));
    }



    /**
     * Adds a new Friend to the database.
     * @param username a friend's instance name.
     * @param deviceAddress a friend's device address
     * @return isAdded true if the friend was added successfully, false otherwise.
     * @throws SQLException
     */
    public boolean addFriend(String username, String deviceAddress)throws SQLException{
        boolean isAdded=false;
        try{
            database = dbH.getReadableDatabase();
            dbH.openDataBase();
            ContentValues values = new ContentValues();
            values.put("Username", username);
            values.put("deviceID", deviceAddress);
            Log.i(TAG,"Adding.. "+deviceAddress);
            isAdded=database.insert("Friend", null, values)>0;
            dbH.close();
        }
        catch(SQLException e){
            Log.i(TAG,"SQLException > addFriend > MainController");
            e.printStackTrace();
        }
        return isAdded;
    }


    /**
     * Deletes a friend from the database given a device address
     * @param friendID A to-be deleted friend's ID.
     * @return isDeleted A boolean that equals true if operation is successful, false otherwise.
     */
    public boolean deleteFriend(String friendID){
        boolean isDeleted=false;
        try{
            database=dbH.getReadableDatabase();
            dbH.openDataBase();
            isDeleted=database.delete("Friend","deviceID"+"'"+friendID+"'",null)==1;
            Log.i(TAG,"Is friend deleted? "+isDeleted);
            dbH.close();
        }
        catch(SQLException e){
            Log.i(TAG,"SQLException > deleteFriend > MainController");
            e.printStackTrace();
        }
        return isDeleted;
    }

    /**
     * Fetches user's friends from the database
     * In order to be displayed.
     * @return friendsList

    public ArrayList<Friend> viewFriendsList(){

        friendsList=new ArrayList<>();

        try{
            database=dbH.getReadableDatabase();
            dbH.openDataBase();
            String query="SELECT * FROM Friend";
            cursor = database.rawQuery(query,null);
            if (cursor.moveToFirst()) {
                do {

                    Friend myFriend = new Friend();
                    myFriend.setInstanceName(cursor.getString(1)); //getting username from database column #: 1
                    //myFriend.setStatus(myFriend.isOnline());
                    myFriend.setAliasName(cursor.getString(3));
                    friendsList.add(myFriend);

                } while (cursor.moveToNext());
            }
            else{
                Log.i(TAG, "There are no friends in the DB.");
            }
            cursor.close();
            dbH.close();
        }
        catch(SQLException e){
            Log.i(TAG,"SQLException > viewFriendsList > MainController");
            e.printStackTrace();
        }

        return friendsList;}//end of viewFriendsList
     */
    /**
     * This method calls nameValidation from MainController to validate the new username
     * then adds it to the database as an alias name
     * @param aliasName A string that contains a new alias name for a certain friend
     * @param friendID A string that contains the id of the friend with the alias name
     * @return isUpdated A boolean that is true if the name was changed, false otherwise
     */
    public boolean changeName(String aliasName, String friendID) throws SQLException{
        boolean isUpdated=false;

        if(nameValidation(aliasName))
        {

            try{
                database = dbH.getReadableDatabase();
                dbH.openDataBase();
                ContentValues args = new ContentValues();
                args.put("aliasname", aliasName);
                isUpdated= database.update("Friend", args, "deviceID=" + friendID, null)>0;
                dbH.close();
            }
            catch(SQLiteException e){
                Log.i(TAG,"SQLiteException > Friend > ChangeName");
                e.printStackTrace();
            }
        }
        Log.i(TAG,"Is alias updated? "+isUpdated);
        return isUpdated;
    }

    /**
     * Checks if a peer is one of the user's friends
     * @param deviceAddress of a peer
     * @return true if this peer is a friend, false otherwise.
     */
    public boolean isThisMyFriend(String deviceAddress){
        boolean isFriend=true;
        try{
            database=dbH.getReadableDatabase();
            dbH.openDataBase();
            query="SELECT * FROM Friend WHERE deviceID='"+deviceAddress+"'";
            cursor = database.rawQuery(query,null);
            cursor.moveToFirst();
            if(cursor.getCount()==0)
                isFriend=false;
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        Log.i(TAG,"Is "+deviceAddress+" your friend? "+isFriend);
        return isFriend;

    }

/***************************************Request's Methods********************************************************/

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
                 //   requestObj.setReqBy(new User (c.getString(1)));
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
                    //post.setPostedBy(new User(c.getString(2)));
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
    public ArrayList<VicinityMessage> viewAllMessages(int chatId)

    {


        try
        {
            database=dbH.getReadableDatabase();
            dbH.openDataBase();
            String query="SELECT * FROM Message WHERE chatId = \""+chatId+"\"";
            Cursor c = database.rawQuery(query,null);
            if (c.moveToFirst())
            {
                do
                {
                    VicinityMessage msg = new VicinityMessage();
                    msg.setMessageBody(c.getString(4));
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


    /**
     * gets a post from the postList by ID
     * @param postID The wanted post.
     * @return returns the post.
     */
    public Post getPost(int postID)
    {
        //for now
        TimelineSectionFragment tsf = new TimelineSectionFragment();
        ArrayList<Post> posts= tsf.GetPosts();

        Post post = new Post();



        if(!posts.isEmpty())
        {
            for (int i = 0;  posts.size() >= i+1; i++) {
                post = posts.get(i);

                if (post.getPostID() == postID)
                    return post;
            }

        }
        return null;


    }

    /**
     * Adds a new Meesage to the database.
     * @param message An object of class VicinityMessage
     * @return isAdded true if the message was added successfully, false otherwise.
     * @throws SQLException
     */
    public boolean addMessage(VicinityMessage message) throws SQLException
    {
        boolean isAdded=false;
        try
        {
            database = dbH.getReadableDatabase();
            dbH.openDataBase();
            ContentValues values = new ContentValues();
            values.put("messageBody", message.getMessageBody());
            values.put("isMyMsg", message.isMyMsg());
            //values.put("msgTimestamp", new Timestamp(date.getTime()).getTime());


            isAdded=database.insert("Message", null, values)>0;
            Log.i(TAG, "ADD SUCCESSFUL");

            dbH.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return isAdded;}




}
