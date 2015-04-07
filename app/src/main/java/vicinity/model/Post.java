package vicinity.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Post {

    private static final String TAG = "PostClass";
    //Post Atts
    private String postedAt;
    private User postedBy;
    private String postBody;
    private Comment[] postComments;
    private String picture;

    //Constructor

    public Post()
    {

    }

    public Post(User postedBy, String postBody){
        Date currentDate= new Date();
        DateFormat dF=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        postedAt= dF.format(currentDate);
        this.postBody=postBody;
        this.postedBy=postedBy;
    }

    //Setters and getters
    public String getPostedAt(){
        return this.postedAt;
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
    public Comment[] getComments(){
        return this.postComments;
    }

//Methods

    //I wrote this -Sarah
    public String toString()
    {
        return "Posted by: "+postedBy.getUsername()+ " Post Content: "+ postBody+" Date: "+ postedAt;
    }

}