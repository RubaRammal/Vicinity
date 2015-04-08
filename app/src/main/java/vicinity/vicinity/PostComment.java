package vicinity.vicinity;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import vicinity.model.Comment;
import vicinity.model.Globals;
import vicinity.model.Post;

/**
 * Post comment activity
 * Still blank
 */
public class PostComment extends ActionBarActivity {

    private ListView commentListView;
    private EditText commentText;
    private Button sendComment;
    private Comment comment;
    private static CommentListAdapter adapter;
    public static Context ctx;
    private static ArrayList<Comment> commentsList;
    private Post commentedOn;
    private int postId;
    private String TAG = "PostComment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);
        commentsList = new ArrayList<Comment>();

        final ActionBar abar = getSupportActionBar();
        abar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01aef0")));//line under the action bar
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Comment");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setHomeButtonEnabled(true);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                postId = 0;
            } else {
                postId = extras.getInt("POST_ID");
            }
        } else {
            postId = (int) savedInstanceState.getSerializable("POST_ID");
        }

        commentedOn = Globals.controller.getPost(postId);
        //Initializations
        Log.i(TAG, commentedOn.getPostBody());
        Log.i(TAG, commentedOn.getPostedBy().getUsername());

        Comment c = new Comment(commentedOn.getPostBody(), commentedOn.getPostedBy().getUsername());
        commentsList.add(c);

        commentsList.add(new Comment("", "Comments"));


        //To get comments of a post
        /*for (int i = 0; i < controller.getPostComments(postID).size(); i++) {
            commentsList.add(s.get(i));
        }*/

        commentsList.add(new Comment("Cool", "Ruba")); //Dummy

        ctx = this;
        commentListView = (ListView) findViewById(android.R.id.list);
        adapter = new CommentListAdapter(ctx, commentsList);
        commentText = (EditText) findViewById(R.id.comment);
        sendComment = (Button) findViewById(R.id.sendComment);

        commentListView.setAdapter(adapter);
        commentListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        commentListView.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                commentListView.setSelection(adapter.getCount() - 1);
            }
        });


    }


}

