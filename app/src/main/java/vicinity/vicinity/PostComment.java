package vicinity.vicinity;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.sql.SQLException;
import java.util.ArrayList;
import vicinity.Controller.MainController;
import vicinity.model.Comment;
import vicinity.model.Globals;
import vicinity.model.Post;

public class PostComment extends ActionBarActivity {

    private ListView commentListView;
    private EditText commentTextField;
    private Button sendCommentButton;
    private Comment comment;
    private static CommentListAdapter adapter;
    public static Context ctx;
    private static ArrayList<Comment> commentsList;
    private Post commentedOn;
    private int postID;
    private String TAG = "PostComment";
    private MainController controller;


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
        abar.setDisplayHomeAsUpEnabled(false);
        abar.setHomeButtonEnabled(true);

        ctx = this;
        controller = new MainController(this);
        Bundle extras = getIntent().getExtras();
        postID = extras.getInt("POST_ID");

     /*if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                postID = 0;
            } else {
                postID = extras.getInt("POST_ID");
            }
        } else {
            postID = (int) savedInstanceState.getSerializable("POST_ID");
        }

        commentedOn = controller.getPost(postID);
        comment = new Comment(commentedOn.getPostBody(), commentedOn.getPostedBy().getUsername());
        commentsList.add(comment);
        commentsList.add(new Comment("", "Comments"));
        commentsList.addAll(controller.getPostComments(postID));
        */
        commentsList = controller.getPostComments(postID);
        commentListView = (ListView) findViewById(android.R.id.list);
        adapter = new CommentListAdapter(ctx, commentsList);
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

        sendCommentButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View view) {
                        String commentText = commentTextField.getText().toString();
                        Comment aComment = null;
                        try {
                            String username = controller.retrieveCurrentUsername();
                            aComment = new Comment (postID, commentText, username);
                            if (controller.addAcomment(aComment))
                                Log.i(TAG, "Comment is added to DB");
                            else
                                Log.i(TAG, "Comment is NOT added to DB");

                        } catch (SQLException e) {
                            Log.i(TAG, "A problem in adding comment to DB");
                        }
                        commentsList.add(aComment);
                        adapter.notifyDataSetChanged();
                        commentTextField.setText("");
                    }
                }
        );
    }//End onCreate
}

