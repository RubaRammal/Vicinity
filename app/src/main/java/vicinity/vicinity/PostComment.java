package vicinity.vicinity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import vicinity.ConnectionManager.UdpBroadcastManager;
import vicinity.Controller.MainController;
import vicinity.model.Comment;
import vicinity.model.Post;

public class PostComment extends ActionBarActivity {

    private ListView commentListView;
    private EditText commentTextField;
    private Button sendCommentButton;
    private TextView commentedOnName;
    private TextView commentedOnText;
    private ImageView commentedOnImage;
    private Comment comment;
    private static CommentListAdapter adapter;
    public static Context ctx;
    private static ArrayList<Comment> commentsList;
    private Post commentedOn;
    private int postID;
    private String TAG = "PostComment";
    private MainController controller;
    private int commentCount;
    private UdpBroadcastManager broadcastManager;//-Lama
    private MediaScannerConnection msConn ;

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
        commentedOnImage = (ImageView) findViewById(R.id.commentedOnImage);
        commentedOnName.setText(commentedOn.getPostedBy());

        if(!commentedOn.getPostBody().equals("")){
            commentedOnText.setText(commentedOn.getPostBody());
        }else {
            commentedOnText.setVisibility(View.GONE);
        }

        if(!commentedOn.getBitmap().equals("")){
            String imageBitmap = commentedOn.getBitmap();
            byte[] decodedString = Base64.decode(imageBitmap, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            commentedOnImage.setImageBitmap(decodedByte);
        }
        else {
            commentedOnImage.setVisibility(View.GONE);
        }
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
                            postComment(comment);


                        } catch (SQLException e) {
                            Log.i(TAG, "A problem in adding comment to DB");
                        }
                        commentsList.add(comment);
                        adapter.notifyDataSetChanged();
                        commentTextField.setText("");
                        commentedOn.setCommentCount(commentCount);

                        //finish();


                    }
                }
        );
    }//End onCreate
    public void imageClick(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Save image")
                .setMessage("Do you want to save this image?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "YES");
                        String imageBitmap = commentedOn.getBitmap();

                        byte[] decodedString = Base64.decode(imageBitmap, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        saveImage(bitmap);

                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(TabsActivity.ctx, "The photo has been saved", duration);
                        toast.show();
                    }
                })

                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "no");

                    }
                })//
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public void saveImage(Bitmap bmp)
    {
        File imageFileFolder = new File(Environment.getExternalStorageDirectory(),"Rotate");
        imageFileFolder.mkdir();
        FileOutputStream out = null;
        Calendar c = Calendar.getInstance();
        String date = fromInt(c.get(Calendar.MONTH))
                + fromInt(c.get(Calendar.DAY_OF_MONTH))
                + fromInt(c.get(Calendar.YEAR))
                + fromInt(c.get(Calendar.HOUR_OF_DAY))
                + fromInt(c.get(Calendar.MINUTE))
                + fromInt(c.get(Calendar.SECOND));
        File imageFileName = new File(imageFileFolder, date.toString() + ".jpg");
        try
        {
            out = new FileOutputStream(imageFileName);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            scanPhoto(imageFileName.toString());
            out = null;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String fromInt(int val)
    {
        return String.valueOf(val);
    }

    public void scanPhoto(final String imageFileName)
    {


        msConn = new MediaScannerConnection(ctx , new MediaScannerConnection.MediaScannerConnectionClient() {

            @Override
            public void onMediaScannerConnected() {

                msConn.scanFile(imageFileName, null);
                Log.i("msClient obj  in Photo Utility", "connection established");
            }

            @Override
            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
                Log.i("msClient obj in Photo Utility","scan completed");

            }
        });
        msConn.connect();
    }


    private void postComment(Comment comment){
        broadcastManager = new UdpBroadcastManager();//-Lama
        broadcastManager.setComment(comment);
        broadcastManager.execute();
    }

}