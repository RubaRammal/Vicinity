package vicinity.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Post implements Serializable{

    private static final String TAG = "PostClass";
    private int postID;
    private String postedAt;
    private String postedBy;
    private String postBody;
    private boolean isText;// to see if the post is an image or a text
    private ArrayList<Comment> postComments;

    //Constructors
    public Post() {

    }

    public Post(String postedBy, String postBody, boolean flag){
        Date currentDate= new Date();
        DateFormat dF=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        this.isText=flag;
        postedAt= dF.format(currentDate);
        this.postBody=postBody;
        this.postedBy=postedBy;
        postComments = new ArrayList<Comment>();
    }

    public Post(String postedBy, String postBody, int postID){
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
    public boolean setPostedBy(String postedBy){
        this.postedBy=postedBy;
        return true;
    }
    public String getPostedBy(){
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

    public String toString() {
        return "Posted by: "+postedBy+ " Post Content: "+ postBody+" Date: "+ postedAt;
    }

}