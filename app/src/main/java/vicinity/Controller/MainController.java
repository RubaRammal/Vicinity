package vicinity.Controller;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import vicinity.ConnectionManager.ChatClient;
import vicinity.ConnectionManager.ConnectAndDiscoverService;
import vicinity.ConnectionManager.UDPpacketListner;
import vicinity.model.Comment;
import vicinity.model.CurrentUser;
import vicinity.model.DBHandler;
import vicinity.model.Neighbor;
import vicinity.model.Post;
import vicinity.model.VicinityMessage;
import vicinity.vicinity.NeighborListAdapter;
import vicinity.vicinity.TabsActivity;

/**
 * This is the main class that integrates different components of the system
 * it acts as the intermediary between model and view
 */
public class MainController {

    private static final String TAG = "Main Controller";
    private SQLiteDatabase database;
    private DBHandler dbH;
    private Context context;
    private ArrayList<Post> postList;
    private ArrayList<VicinityMessage> allMessages;
    private ArrayList<VicinityMessage> allChatMessages;
    public String query;
    public Cursor cursor;
    private ArrayList<Comment> commentsList;
    private ArrayList<ChatClient> clientThreads;

    public static ArrayList<Neighbor> mutedNeighbors = new ArrayList<>();







    /**
     * Public constructor
     * @param context An application context to instantiate the database
     */
    public MainController(Context context){
        dbH=new DBHandler(context);
        this.context=context;
        allMessages = new ArrayList<VicinityMessage>();
        clientThreads = new ArrayList<>();

        try{
            dbH.createDataBase();
            dbH.openDataBase();}
        catch(SQLException e){

        }
        catch(IOException e){

        }


    }



    /*---------------------------------User methods---------------------------------------*/

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

            database = dbH.getWritableDatabase();
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

        database = dbH.getReadableDatabase();
        query="SELECT Username FROM CurrentUser";
        cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
            username2=cursor.getString(cursor.getColumnIndex("Username"));
        cursor.close();
        dbH.close();
        return username2;

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
     * Deletes system's database
     */
    public void deleteAccount(){
        dbH.deleteDatabase();
    }
    /*--------------------------------------------------------------------------------------*/



    /*--------------------------Neighbors Methods----------------------------------------------*/

    /**
     * This method mutes user by searching for a peer with the given
     * device address in the neighbours list and deleting the user
     * It also deletes posts & comments by this specific user as well
     * @param neighbor the to-be muted user
     * @return isMuted true if the user was muted, false otherwise
     */
    public static boolean muteNeighbor(Neighbor neighbor) throws NullPointerException{
        boolean isMuted = false;
        if(!isUserMuted(neighbor)) {
            if (UDPpacketListner.doesAddressExist(neighbor.getDeviceAddress())) {
                //Get the neighbor's IP
                neighbor.setIpAddress(UDPpacketListner.getPeerAddress(neighbor.getDeviceAddress()));

                isMuted = mutedNeighbors.add(neighbor);
                Log.i("mute", neighbor.getInstanceName() + " is muted? " + isMuted);
                if(isMuted)
                    Toast.makeText(TabsActivity.ctx, neighbor.getInstanceName()+" has been muted", Toast.LENGTH_LONG).show();

            }
        }
        return isMuted;
    }

    /**
     * Removes a given neighbor from the muted neighbors list
     * @param neighbor a Neighbor object to be removed from the list
     * @return a boolean true if the operation was successful,
     *          false otherwise.
     */
    public static boolean unmuteNeighbor(Neighbor neighbor){
        if(isUserMuted(neighbor))
        {
            Toast.makeText(TabsActivity.ctx, neighbor.getInstanceName()+" has been unmuted", Toast.LENGTH_LONG).show();
            return mutedNeighbors.remove(neighbor);}
        return false;
    }

    /**
     * Checks if the given user exists in Muted users list
     * or not
     * @param user a Neighbor to check if muted or not.
     * @return isMuted a boolean that is true if user exists in muted users list
     * false otherwise
     */
    public static boolean isUserMuted(Neighbor user){
        Iterator<Neighbor> it = mutedNeighbors.iterator();
        while (it.hasNext()) {
            Neighbor peer = it.next();
            if(peer.getDeviceAddress().equals(user.getDeviceAddress())) {
                Log.i(TAG,"User is muted");
                return true;
            }
        }
        Log.i(TAG,"User is NOT muted");
        return false;
    }

    /**
     * Checks if the IP belongs to a muted neighbor
     * @param IP InetAddress IP to be checked
     * @return boolean that is true if the IP belongs
     *          to a muted user, false otherwise.
     */
    public static boolean isThisIPMuted(InetAddress IP){
        Iterator<Neighbor> it = mutedNeighbors.iterator();
        while (it.hasNext()) {
            Neighbor peer = it.next();
            if(peer.getIpAddress().equals(IP))
                return true;
        }
        return false;
    }




    /*--------------------------------------------------------------------------------------*/




    /*---------------------------Friends Methods--------------------------------------------*/

    /**
     * Adds the newly added Friend to the database.
     * @param requestedTo a Neighbor object to be added as a friend
     * @return isAdded true if the friend was added successfully, false otherwise.
     */
    public boolean addFriend(Neighbor requestedTo){
        boolean isAdded=false;
        try{
            database = dbH.getReadableDatabase();
            dbH.openDataBase();
            ContentValues values = new ContentValues();
            values.put("Username", requestedTo.getInstanceName());
            values.put("deviceID", requestedTo.getDeviceAddress());
            Log.i(TAG,"Adding.. "+requestedTo.getDeviceAddress());
            isAdded=database.insert("Friend", null, values)>0;
            dbH.close();

        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return isAdded;
    }

    /**
     *
     */
    public static boolean isDeleted = false;
    public void alertUserOfRequestReply(boolean reply, Neighbor neighbor) throws SQLException{
        CharSequence text=neighbor.getInstanceName()+" has been removed from your friends";
        int duration = Toast.LENGTH_LONG;
        if(reply){
            addFriend(neighbor);
            NeighborListAdapter.updateNeighborsList(neighbor);
            text = neighbor.getInstanceName()+" is now your friend!";
        }
        else{
            if(!isDeleted)
                text = neighbor.getInstanceName()+" has rejected your request...";
        }
        Toast toast = Toast.makeText(ConnectAndDiscoverService.ctx,text,duration);
        toast.show();
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
            e.printStackTrace();
        }
        return isDeleted;
    }

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
            cursor.close();
            dbH.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        Log.i(TAG,"Is "+deviceAddress+" your friend? "+isFriend);
        return isFriend;

    }

    /*--------------------------------------------------------------------------------------*/


    /*-------------------------------Posts Methods------------------------------------------*/


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
                    post.setPostID(Integer.valueOf(c.getString(c.getColumnIndex("postID"))));
                    post.setBitmap(c.getString(c.getColumnIndex("image")));

                    postList.add(post);
                } while (c.moveToNext());
            }

            else
            {
                Log.i(TAG, "There are no posts in the DB.");
            }
            c.close();
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
            values.put("postBody", post.getPostBody());
            values.put("postedBy", post.getPostedBy());
            values.put("postedAt", post.getPostedAt());
            values.put("postID", post.getPostID());
            values.put("image" , post.getBitmap());

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
            String query = "SELECT * FROM Post WHERE postID="+"'"+postID+"'";
            Cursor c = database.rawQuery(query, null);
            if (c.moveToFirst()) {
                post = new Post();
                post.setPostID(c.getColumnIndex("postID"));
                post.setPostBody(c.getString(c.getColumnIndex("postBody")));
                post.setPostedBy(c.getString(c.getColumnIndex("postedBy")));
                post.setBitmap(c.getString(c.getColumnIndex("image")));

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



    /**
     * Deletes all records in Post table in database
     * @return true if the operation is successful, false otherwise
     */
    public boolean deleteAllPosts () throws SQLException{
        boolean areDeleted;
        database = dbH.getReadableDatabase();
        dbH.openDataBase();
        areDeleted= database.delete("Post", null, null)>0;

        dbH.close();
        return areDeleted;

    }

    /*--------------------------------------------------------------------------------------*/


    /*---------------------------Comments Methods-------------------------------------------*/

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
                    comment.setPostID(c.getColumnIndex("postID"));


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
            values.put("postID", comment.getPostID());
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

    /**
     * Deletes all records in Comment table in database
     * @return true if the operation is successful
     */
    public boolean deleteAllcomments () throws SQLException{
        boolean areDeleted;
        database = dbH.getReadableDatabase();
        dbH.openDataBase();
        areDeleted= database.delete("Comment", null, null)>0;

        dbH.close();
        return areDeleted;
    }



    /*------------------------------Chat Methods--------------------------------------------*/

    /**
     * Fetches user's Chats from the database
     * @return allMessages
     */
    public ArrayList<VicinityMessage> viewAllChatMessages(String ip)

    {

        try
        {
            database=dbH.getReadableDatabase();
            dbH.openDataBase();
            String query="SELECT * FROM Message WHERE fromIP="+"\""+ip+"\";";
            Cursor c = database.rawQuery(query,null);

            VicinityMessage msg = null;
            if (c.moveToFirst()) {

                do {

                    msg = new VicinityMessage();
                    msg.setMessageBody(c.getString(3));
                    msg.setFriendID(c.getString(2));
                    msg.setChatId(c.getInt(c.getColumnIndex("chatId")));
                    msg.setFrom(c.getString(c.getColumnIndex("fromIP")));
                    msg.setImageString(c.getString(c.getColumnIndex("image")));



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
                    msg.setIsMyMsg(c.getInt(c.getColumnIndex("isMyMsg"))>0);
                    msg.setChatId(c.getInt(c.getColumnIndex("chatId")));
                    msg.setFrom(c.getString(c.getColumnIndex("fromIP")));
                    msg.setImageString(c.getString(c.getColumnIndex("image")));

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


    /**
     * Fetches all the chat IDs from the db
     * @return chatIds InetAddress array
     */
    public ArrayList<String> viewChatIps()

    {
        ArrayList<String> chatIps = new ArrayList<String>();

        try
        {
            database=dbH.getReadableDatabase();
            dbH.openDataBase();
            String query="SELECT DISTINCT fromIP FROM Message";
            Cursor c = database.rawQuery(query,null);
            int count = 0;
            if (c.moveToFirst()) {

                do {

                    chatIps.add(c.getString(c.getColumnIndex("fromIP")));
                    Log.i(TAG, chatIps.get(count));

                } while (c.moveToNext());
            }else{
                Log.i(TAG, "There are no IPs in the DB.");
            }
            dbH.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        Log.i(TAG, "Successfully returned IPs");

        return chatIps;
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
     * Deletes all records in table Message in the database
     * @return true if the operation is successful, false otherwise
     * @throws SQLException
     */
    public boolean deleteAllMessages() throws SQLException{
        boolean areDeleted;
        database = dbH.getReadableDatabase();
        dbH.openDataBase();
        areDeleted= database.delete("Message", null, null)>0;

        dbH.close();
        return areDeleted;

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
            values.put("chatId", message.getChatId());
            values.put("sentBy", message.getFriendID());
            values.put("msgTimestamp", message.getDate());
            values.put("fromIP", message.getFrom().toString());
            values.put("image" , message.getImageString());


            isAdded=database.insert("Message", null, values)>0;
            Log.i(TAG, "ADD SUCCESSFUL");

            dbH.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return isAdded;}


    /**
     * Returns an array of all messages for a chat id.
     * @param fromIp an InetAddress that is the id of a chat
     * @return an ArrayLis of VicinityMessages of all the messages for the specified chat id.
     */
    public ArrayList<VicinityMessage> getChatMessages(String fromIp){

        ArrayList<VicinityMessage> chat = new ArrayList<VicinityMessage>();

        for(int i=0; i<this.allMessages.size(); i++){

            if(this.allMessages.get(i).getFrom().equals(fromIp)){
                chat.add(allMessages.get(i));
                Log.i(TAG, allMessages.get(i).getMessageBody());

            }
        }

        return chat;
    }


    /*--------------------------------------------------------------------------------------*/

    public void addClientThread(ChatClient c){
        clientThreads.add(c);
    }

    public ArrayList<ChatClient> getClientThreads(){
        return clientThreads;
    }

}

