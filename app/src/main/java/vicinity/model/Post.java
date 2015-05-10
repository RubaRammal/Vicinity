package vicinity.model;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class that holds the structure of timeline posts
 */
public class Post  implements Serializable {

    private int postID;
    private String postedAt;
    private String postedBy;
    private String postBody;
    private ArrayList<Comment> postComments;
    private int commentsCount;
    private String PhotoPath;
    private String image;


    public Post()
    {
        image = "";

    }


    /**
     * Public constructor
     * @param postedBy username
     * @param postBody a String containing the post
     */
    public Post(String postedBy, String postBody){
        //Date and time the post was sent:
        Date currentDate= new Date();
        DateFormat dF=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        postedAt= dF.format(currentDate);
        this.postBody=postBody;
        this.postedBy=postedBy;
        postComments = new ArrayList<Comment>();
        commentsCount=0;
        image = "";
    }

    /*------Setters and Getters-----*/
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

    public void setPostID(int p){
        this.postID = p;
    }
    public void setPostedAt(String postedAt) {
               this.postedAt=postedAt;
           }
    public void setPostDate(){
        Date currentDate= new Date();
        DateFormat dF=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        postedAt= dF.format(currentDate);
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
        return "Posted by: "+postedBy+ " Post Content: "+ postBody+" Date: "+ postedAt+" PostID: "+postID;
    }

}