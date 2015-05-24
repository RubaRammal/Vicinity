package vicinity.model;

import java.io.Serializable;

/**
 * This class holds the structure of the posts' comment.
 */
public class Comment implements Serializable{

    private static final String TAG = "CommentClass";

    private int commentID; // Comment ID
    private int postID; // Post ID
    private String commentBody; // Comment body
    private String commentedBy; // Commenter


    /**
     * Setters And Getters
     */

    public boolean setCommentBody(String commentBody)
    {
        this.commentBody = commentBody;
        return true;
    }

    public String getCommentBody()
    {
        return this.commentBody;
    }

    public boolean setCommentedBy(String commentBy)
    {
        this.commentedBy = commentBy;
        return true;
    }

    public String getCommentedBy()
    {
        return this.commentedBy;
    }

    public int getCommentID()
    {
        return this.commentID;
    }

    public void setCommentID(int commentID)
    {
        this.commentID = commentID;
    }

    public int getPostID()
    {
        return postID;
    }

    public void setPostID(int pid)
    {
        postID = pid;
    }


}
