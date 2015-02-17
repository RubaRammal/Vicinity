package vicinity.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {
    private static final String TAG = "CommentClass";

    //Comment Atts
    private int commentID; //I don't think this is necessary -afnan
    private String commentBody;
    private User commentBy;
    private String commentedAt; //returns the date as a string

    //Constructor
    public Comment(String commentBody, User commentBy){
        Date currentDate= new Date();
        DateFormat dF=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        commentedAt= dF.format(currentDate);
        this.commentBody=commentBody;
        this.commentBy=commentBy;
    }

    //Setters and Getters
    public boolean setCommentBody(String commentBody){
        this.commentBody=commentBody;
        return true;
    }
    public String getCommentBody(){
        return this.commentBody;
    }
    public boolean setCommentBy(User commentBy){
        this.commentBy=commentBy;
        return true;
    }
    public User getCommentBy(){
        return this.commentBy;
    }
    public int getCommentID(){
        return this.commentID;
    }
    public String getCommentedAt(){
        return this.commentedAt;
    }

    //Methods
    public void viewComment(){//I don't think the view methods are necessary but we'll see later -Afnan

    }

}
