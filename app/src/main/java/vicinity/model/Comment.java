package vicinity.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import vicinity.ConnectionManager.WiFiP2pService;

public class Comment {
    private static final String TAG = "CommentClass";

    //Comment Atts
    private int commentID; // postID in the database
    private String commentBody;
    private String commentedBy;
    private String commentedAt; //returns the date as a string

    //Constructors

    public Comment () {

    }

    public Comment(String commentBody, String commentBy){
        Date currentDate= new Date();
        DateFormat dF=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        commentedAt= dF.format(currentDate);
        this.commentBody=commentBody;
        this.commentedBy=commentBy;
    }

    public Comment(int commentID, String commentBody, String commentBy){
        this.commentID=commentID;
        Date currentDate= new Date();
        DateFormat dF=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        commentedAt= dF.format(currentDate);
        this.commentBody=commentBody;
        this.commentedBy=commentBy;
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
    public void setCommentedAt(String commentedAt) {
        this.commentedAt = commentedAt;
    }

    public String getCommentedAt(){
        return this.commentedAt;
    }

    public int getCommentID(){
        return this.commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }


    //Methods
    public void viewComment(){//I don't think the view methods are necessary but we'll see later -Afnan

    }

}
