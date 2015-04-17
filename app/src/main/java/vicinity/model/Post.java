package vicinity.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Post {

    private static final String TAG = "PostClass";
    //Post Atts
    private int postID;
    private String postedAt;
    private User postedBy;
    private String postBody;
    private ArrayList<Comment> postComments;
    private String deviceID;  // I'm not sure why we added this attribute in the class diagram -Afnan
    private String picture;   //this might take another type i'll look it up later

    //Constructor

    //we need this in the MainController -Sarah
    public Post()
    {

    }

    public Post(User postedBy, String postBody){
        Date currentDate= new Date();
        DateFormat dF=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        postedAt= dF.format(currentDate);
        this.postBody=postBody;
        this.postedBy=postedBy;
        postComments = new ArrayList<Comment>();

    }

    public Post(User postedBy, String postBody, int postID){
        Date currentDate= new Date();
        DateFormat dF=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        postedAt= dF.format(currentDate);
        this.postBody=postBody;
        this.postedBy=postedBy;
        this.postID = postID;
        postComments = new ArrayList<Comment>();
    }

    //Setters and getters
    public int getPostID(){
        return this.postID;
    }

    public void setPostID (int postID) {
        this.postID = postID;
    }
    public String getPostedAt(){
        return this.postedAt;
    }

    public void setPostedAt(String postedAt) {
        this.postedAt=postedAt;
    }
    public boolean setPostedBy(User postedBy){
        this.postedBy=postedBy;
        return true;
    }
    public User getPostedBy(){
        return this.postedBy;
    }
    public boolean setPostBody(String postBody){
        this.postBody=postBody;
        return true;
    }
    public String getPostBody(){
        return this.postBody;
    }
    public ArrayList<Comment> getComments(){
        return this.postComments;
    }

    /**
     * adds a comment to the ArrayList postComments
     * @param comment
     */
    public void addAcomment (Comment comment) {
        postComments.add(comment);
    }

//Methods

    //I wrote this -Sarah
    public String toString()
    {
        return "Posted by: "+postedBy.getUsername()+ " Post Content: "+ postBody+" Date: "+ postedAt;
    }

}