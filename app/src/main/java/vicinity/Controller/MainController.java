package vicinity.Controller;


import vicinity.model.Friend;
import vicinity.model.Message;
import vicinity.model.Post;
import vicinity.model.Request;
import vicinity.model.User;

/**
 * Created by Sarah on 2/23/15.
 */
public class MainController {

    //MainController attributes
    private String username;
    private User [] onlineUsers;
    private Friend [] friendList;
    private Request[] requestsList;
    private Post [] postList;
    private Message [] allMessages;


    //Constructor
    public MainController(){

    }

    //This methods used to be called changeUsername.
    //The parameters might need to be changed.
    public boolean ChangeFriendsUsername(){ return true; }

    public User[] viewOnlineUsers(){return null;}

    public boolean muteUser(User user){ return true;}

    public boolean deleteFriend(Friend friend){ return true;}

    public Friend[] viewFriendsList(){return null;}

    public Request[] viewAllRequests(){return null;}

    public boolean acceptRequest(int num){return true;}

    public boolean denyRequest(int num){return true;}

    public boolean sendRequest(User user){return true;}

    public Post[] viewAllPosts(){ return null;}

    public boolean addPost(Post post){return true;}

    public void viewAllMessages(){}

    public boolean deleteMessage(int num){return true;}












}
