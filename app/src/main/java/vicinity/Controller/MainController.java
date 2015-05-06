package vicinity.Controller;


import vicinity.model.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;


public class MainController {

    private static final String TAG = "Main Controller";
    private SQLiteDatabase database;
    private DBHandler dbH;
    private Context context;
    private ArrayList<WiFiP2pService> friendsList;
    private ArrayList<Post> postList;
    private ArrayList<VicinityMessage> allMessages;
    private ArrayList<VicinityMessage> allChatMessages;
    public String query;
    public Cursor cursor;
    private ArrayList<Comment> commentsList;
    JSONArray mMessageArray = new JSONArray();		// limit to the latest 50 messages





    /**
     * Public constructor
     * @param context An application context to instantiate the database
     */
    public MainController(Context context){
        dbH=new DBHandler(context);
        this.context=context;
        allMessages = new ArrayList<VicinityMessage>();

    }
/*****************************************User's methods**********************************************/

    /*------------------------------Works------------------------------*/
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

    /*------------------------------Works------------------------------*/
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

            if(cursor.moveToFirst())
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

    /*------------------------------Works------------------------------*/
    /**
     * Validates an input string (username) that shall contain letters, numbers, "-" and "_" ONLY
     * @param username A string
     * @return true if username matches the criteria, false other wise
     */
    public boolean nameValidation(String username){
        return !(username.isEmpty() || !username.matches("[a-zA-Z0-9_-]+"));
    }


    /*------------------------------Works------------------------------*/
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
            isDeleted=database.delete("Friend","deviceID="+"'"+friendID+"'",null)==1;
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
                isUpdated= database.update("Friend", args, "deviceID='" + friendID+"'", null)>0;
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

    /*------------------------------Works------------------------------*/
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
            String query = "SELECT * FROM Post WHERE 1";
            Cursor c = database.rawQuery(query, null);
            if (c.moveToFirst())
            {
                do
                {
                    Post post = new Post();
                    post.setPostBody(c.getString(c.getColumnIndex("postBody")));
                    post.setPostedBy(c.getString(c.getColumnIndex("postedBy")));
                    post.setPostedAt(c.getString(c.getColumnIndex("postedAt")));
                    post.setPostID(Integer.valueOf(c.getString(c.getColumnIndex("_id"))));
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
            Log.i(TAG, "Error in fetching all posts from DB.");
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
            values.put("postBody", post.getPostBody());
            values.put("postedBy", post.getPostedBy());
            values.put("postedAt", post.getPostedAt());
            isAdded=database.insert("Post", null, values)>0;

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally{
            if(database!=null && database.isOpen())
                dbH.close();
        }
        return isAdded;}



    /**
     +     * gets a post from the postList given its id
     +     * @param postID The integer id of the selected post.
     +     * @return returns the post.
     +     */
        public Post getPost(int postID)
        {
               Post post = null;
                try
                {
                           database = dbH.getReadableDatabase();
                   dbH.openDataBase();
                    String query = "SELECT * FORM Post WHERE postID="+"'"+postID+"'";
                   Cursor c = database.rawQuery(query, null);
                    if (c.moveToFirst()) {
                            post = new Post();
                            post.setPostID(c.getColumnIndex("_id"));
                            post.setPostBody(c.getString(c.getColumnIndex("postBody")));
                            post.setPostedBy(c.getString(c.getColumnIndex("postedBy")));
                                                    //contact.setPicture(c.getBlob(3));
                               }
                    else
                    {
                                Log.i(TAG, "This postID doesn't exist in the DB.");
                    }
                   dbH.close();
               }
               catch (SQLException e)
                {
                            e.printStackTrace();
                    Log.i(TAG, "Error in getting post from DB.");

                        }
                return post;
            } //END getPost


    public Post getPost2(int id){
        Post p = new Post();
        ArrayList<Post> allPosts = viewAllPosts();


        for (int i=0; i<allPosts.size(); i++){
            if(allPosts.get(i).getPostID()==id)
                p =  allPosts.get(i);
        }
        return p;
    }

                /**
          * Fetches the comments on a specified post
          * @param postID the integer id of the selected post
          * @return an ArrayList containing all comments on the specified post
          */
                public ArrayList<Comment> getPostComments(int postID)
        {
               commentsList = new ArrayList<Comment>();
                try
                {
                           database = dbH.getReadableDatabase();
                   dbH.openDataBase();
                   String query = "SELECT * FROM Comment WHERE postID="+"'"+postID+"'";
                   Cursor c = database.rawQuery(query, null);
                   if (c.moveToFirst())
                        {
                                    do
                            {
                                        Comment comment = new Comment ();
                           comment.setCommentBody(c.getString(c.getColumnIndex("commentBody")));
                           comment.setCommentedBy(c.getString(c.getColumnIndex("commentedBy")));
                           comment.setCommentID(c.getColumnIndex("commentID"));

                                    // Adding comment to commentsList
                                           commentsList.add(comment);
                        } while (c.moveToNext());
                    }
                    else
                    {
                                Log.i(TAG, "There are no comments on the specified post.");
                   }
                   dbH.close();
               }
               catch (SQLException e)
               {
                           e.printStackTrace();
                   Log.i(TAG, "Error in getting comments from DB.");

                       }

                       return commentsList;
           } //END getPostComments

                public boolean addAcomment(Comment comment) {
                boolean isAdded = false;
                try
                {
                            database = dbH.getReadableDatabase();
                    dbH.openDataBase();
                    ContentValues values = new ContentValues();
                   values.put("commentBody", comment.getCommentBody());
                    values.put("commentedBy", comment.getCommentedBy());
                    values.put("postID", comment.getCommentID());
                    isAdded=database.insert("Comment", null, values)>0;
                    dbH.close();
                }
                catch(SQLException e)
                {
                            e.printStackTrace();
                    Log.i(TAG, "Error in adding comments to DB.");
                }
                return isAdded;
            } //END addAcomment

                //still working on these methods - amjad
                public boolean deleteAcomment () {
                return true;
            }

                public boolean deleteAllcomments () {
                return true;
            }
        public boolean deleteAllPosts () {
                return true;
            }


    /*------------------------------Works------------------------------*/
    /**
     * Fetches user's Chats from the database
     * @return allMessages
     */
    public ArrayList<VicinityMessage> viewAllChatMessages(int cId)

    {

        try
        {
            database=dbH.getReadableDatabase();
            dbH.openDataBase();
            String query="SELECT * FROM Message WHERE chatId ="+cId;
            Cursor c = database.rawQuery(query,null);

            VicinityMessage msg = null;
            if (c.moveToFirst()) {

                do {

                    msg = new VicinityMessage();
                    msg.setMessageBody(c.getString(3));
                    msg.setFriendID(c.getString(2));
                    msg.setChatId(c.getInt(c.getColumnIndex("chatId")));

                    msg.setDate(c.getString(1));
                    //contact.setPicture(c.getBlob(3));

                    // Adding message to allMessages
                    allChatMessages.add(msg);
                } while (c.moveToNext());
            }else{
                Log.i(TAG, "There are no messages in the DB.");
            }
            dbH.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }


        return allChatMessages;
    }


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

            VicinityMessage msg = null;
            if (c.moveToFirst()) {

                do {

                    msg = new VicinityMessage();
                    msg.setMessageBody(c.getString(3));
                    msg.setFriendID(c.getString(2));
                    msg.setChatId(c.getInt(c.getColumnIndex("chatId")));

                    msg.setDate(c.getString(1));
                    //contact.setPicture(c.getBlob(3));

                    // Adding message to allMessages
                    allMessages.add(msg);
                } while (c.moveToNext());
            }else{
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


    /*------------------------------Works------------------------------*/
    /**
     * Fetches all the chat IDs from the db
     * @return chatIds int array
     */
    public int[] viewChatIds()

    {
        int[] chatIds = new int[30];

        try
        {
            database=dbH.getReadableDatabase();
            dbH.openDataBase();
            String query="SELECT DISTINCT chatId FROM Message";
            Cursor c = database.rawQuery(query,null);
            int count = 0;
            if (c.moveToFirst()) {

                do {

                    chatIds[count++] = c.getInt(c.getColumnIndex("chatId"));

                } while (c.moveToNext());
            }else{
                Log.i(TAG, "There are no chat ids in the DB.");
            }
            dbH.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        Log.i(TAG, "Successfully returned ids");

        return chatIds;
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

    /*------------------------------Works------------------------------*/
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
            values.put("chatId", message.getChatId());
            values.put("sentBy", message.getFriendID());
            values.put("msgTimestamp", message.getDate());


            isAdded=database.insert("Message", null, values)>0;
            Log.i(TAG, "ADD SUCCESSFUL");

            dbH.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return isAdded;}


    /*------------------------------Works------------------------------*/
    /**
     * Returns an array of all messages for a chat id.
     * @param chatId and int that is an id of a chat
     * @return an ArrayLis of VicinityMessages of all the messages for the specified chat id.
     */
    public ArrayList<VicinityMessage> getChatMessages(int chatId){

        ArrayList<VicinityMessage> chat = new ArrayList<VicinityMessage>();

        for(int i=0; i<this.allMessages.size(); i++){

            if(this.allMessages.get(i).getChatId()==chatId){
                chat.add(allMessages.get(i));
                Log.i(TAG, allMessages.get(i).getMessageBody());
                Log.i(TAG, allMessages.get(i).getChatId()+"");

            }
        }

        return chat;
    }

    public String shiftInsertMessage(VicinityMessage row) {
        JSONObject jsonobj = VicinityMessage.getAsJSONObject(row);
        if( jsonobj != null ){
            mMessageArray.put(jsonobj);
        }
        mMessageArray = JSONUtils.truncateJSONArray(mMessageArray, 10);  // truncate the oldest 10.
        return jsonobj.toString();
    }


}
