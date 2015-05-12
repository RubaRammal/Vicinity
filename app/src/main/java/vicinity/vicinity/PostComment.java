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
import vicinity.Controller.MainController;
import vicinity.model.Comment;
import vicinity.model.Post;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import java.sql.SQLException;
import vicinity.ConnectionManager.BroadcastManager;

/* this class is supposed to implement the method sendClickedPost from TimelineInterface
* still working on it
* amjad
*/

public class PostComment extends ActionBarActivity {

    private ListView commentListView;
    private EditText commentTextField;
    private Button sendCommentButton;
    private TextView commentedOnName;
    private TextView commentedOnText;
    private Comment comment;
    private static CommentListAdapter adapter;
    public static Context ctx;
    private static ArrayList<Comment> commentsList;
    private Post commentedOn;
    private int postID;
    private String TAG = "PostComment";
    private MainController controller;
    private int commentCount;
    private BroadcastManager broadcastManager;//-Lama

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);

        final ActionBar abar = getSupportActionBar();
        abar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01aef0")));//line under the action bar
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Comments");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        // I disabled the back button in the action bar cuz it causes an error
        abar.setDisplayHomeAsUpEnabled(false);
        abar.setHomeButtonEnabled(true);

        ctx = this;
        controller = new MainController(ctx);
        comment = new Comment();//-Lama
        broadcastManager = new BroadcastManager();//-Lama
        Bundle extras = getIntent().getExtras();
        postID = extras.getInt("POST_ID");

        commentedOn = controller.getPost(postID);


        commentsList = controller.getPostComments(postID);
        commentCount = commentsList.size();
        commentListView = (ListView) findViewById(R.id.commentsList);
        adapter = new CommentListAdapter(ctx, commentsList);
        View cHeader = getLayoutInflater().inflate(R.layout.comment_header, null);
        commentListView.addHeaderView(cHeader);
        commentListView.setAdapter(adapter);
        commentListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);



        commentListView.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                commentListView.setSelection(adapter.getCount() - 1);
            }
        });

        commentTextField = (EditText) findViewById(R.id.commentTextField);
        commentedOnName = (TextView) findViewById(R.id.commentedOnName);
        commentedOnText = (TextView) findViewById(R.id.commentedOn);
        commentedOnName.setText(commentedOn.getPostedBy());
        commentedOnText.setText(commentedOn.getPostBody());
        sendCommentButton = (Button) findViewById(R.id.sendCommentButton);
        sendCommentButton.setEnabled(false);
        commentTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sendCommentButton.setEnabled(!TextUtils.isEmpty(commentTextField.getText().toString().trim()));
            }
        });

        // When send button is clicked
        sendCommentButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View view) {
                        String commentText = commentTextField.getText().toString();
                        try {

                            comment.setCommentBody(commentText);
                            comment.setCommentedBy(controller.retrieveCurrentUsername());
                            comment.setPostID(postID);
                            broadcastManager.setComment(comment);


                        } catch (SQLException e) {
                            Log.i(TAG, "A problem in adding comment to DB");
                        }
                        commentsList.add(comment);
                        adapter.notifyDataSetChanged();
                        commentTextField.setText("");
                        commentedOn.setCommentCount(commentCount);
                        broadcastManager.execute();
                        //finish();


                    }
                }
        );
    }//End onCreate

}
