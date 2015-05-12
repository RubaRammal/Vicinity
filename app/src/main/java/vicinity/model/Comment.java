package vicinity.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment implements Serializable{
    private static final String TAG = "CommentClass";

    //Comment Atts
    private int commentID;
    private int postID;
    private String commentBody;
    private String commentedBy;
    private String commentedAt; //returns the date as a string

    //Constructors
    public Comment(){

    }

    public Comment(String commentBody, String commentBy){
        Date currentDate= new Date();
        DateFormat dF=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        commentedAt = dF.format(currentDate);
        this.commentBody = commentBody;
        this.commentedBy = commentBy;
    }

    public Comment(int commentID, String commentBody, String commentedBy){
                this.commentID = commentID;
                Date currentDate = new Date();
                DateFormat dF =  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                commentedAt = dF.format(currentDate);
                this.commentBody = commentBody;
                this.commentedBy = commentedBy;
    }


    //Setters and Getters
    public boolean setCommentBody(String commentBody){
        this.commentBody=commentBody;
        return true;
    }
    public String getCommentBody(){
        return this.commentBody;
    }
    public boolean setCommentedBy(String commentBy){
        this.commentedBy=commentBy;
        return true;
    }

    public String getCommentedBy(){
        return this.commentedBy;
    }

    public String getCommentedAt(){
        return this.commentedAt;
    }

    public void setCommentedAt(String commentedAt) {
        this.commentedAt = commentedAt;
    }

    public int getCommentID(){
        return this.commentID;
    }
    public void setCommentID(int commentID) {
               this.commentID = commentID;
            }

    public int getPostID(){
        return postID;
    }
    public void setPostID(int pid) {
        postID = pid;
    }



    //Methods
    public void viewComment(){//I don't think the view methods are necessary but we'll see later -Afnan

    }

}
