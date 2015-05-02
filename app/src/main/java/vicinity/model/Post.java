package vicinity.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Post  implements Serializable {

    private static final String TAG = "PostClass";
    private int postID;
    private String postedAt;
    private String postedBy;
    private String postBody;
    private ArrayList<Comment> postComments;
    private int commentsCount;
    private String PhotoPath;
    private String image;

        //Constructor

    //we need this in the MainController -Sarah
    public Post()
    {
        image = "";

    }

    public Post(String postedBy, String postBody){
                Date currentDate= new Date();
                DateFormat dF=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                postedAt= dF.format(currentDate);
                this.postBody=postBody;
                this.postedBy=postedBy;
                postComments = new ArrayList<Comment>();
                commentsCount=0;
        image = "";

    }


    public Post(String postedBy, String postBody, int postID){
        Date currentDate= new Date();
        DateFormat dF=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        postedAt= dF.format(currentDate);
        this.postBody=postBody;
        this.postedBy=postedBy;
        this.postID = postID;
        postComments = new ArrayList<Comment>();
        commentsCount=0;
        image = "";
    }

    //Setters and getters
    public int getPostID(){
        return this.postID;
    }
    public String getPostedAt(){
        return this.postedAt;
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

    public void setPostID (int postID) {
                this.postID = postID;
            }

    public void setPostedAt(String postedAt) {
               this.postedAt=postedAt;
           }

    public int getCommentsCount(){
        return commentsCount;
    }

    public void setCommentCount(int c){
        commentsCount = c;
    }

    public String getPhotoPath(){
        return  PhotoPath;}

    public void setPhotoPath(String PhotoPath){
        this.PhotoPath = PhotoPath;}

    public void setBitmap(String img){
        image = img;
    }

    public String getBitmap(){
        return image;
    }

         /**
          * adds a comment to the ArrayList postComments
          * @param comment
          */
        public void addAcomment (Comment comment) {
                postComments.add(comment);
           }

    public String toString()
    {
        return "Posted by: "+postedBy+ " Post Content: "+ postBody+" Date: "+ postedAt;
    }

}