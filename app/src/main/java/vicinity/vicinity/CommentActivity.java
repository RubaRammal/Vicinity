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

/**
 * Displays the post selected in the TimelineSectionFragment
 * and the list of comments on that post.
 * Allows sending a comment to the selected post.
 */
public class CommentActivity extends ActionBarActivity {

    private String TAG = "PostComment";
    private ListView commentListView; // Comments' list view
    private EditText commentTextField; // Text field for adding a comment
    private Button sendCommentButton; // Button for sending the comment
    private Comment comment; // Comment to be sent
    private static CommentListAdapter adapter; // Binds the comments list with a ListView
    public static Context ctx; // Activity's context
    private static ArrayList<Comment> commentsList; // An ArrayList of Comment objects
    private Post commentedOn; // The selected Post in TimelineSectionFragment
    private int postID; // The selected Post's ID
    private MainController controller;
    private MediaScannerConnection msConn ;


            /*----------Overridden Methods------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);

        /*----------Change the style of the ActionBar------------*/
        final ActionBar abar = getSupportActionBar();
        abar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01aef0")));//line under the action bar
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Comments");//ActionBar title
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(false);
        abar.setHomeButtonEnabled(true);



        // Initialization
        ctx = this;
        controller = new MainController(ctx);
        comment = new Comment();
        // Obtain the selected Post's ID in the TimelineSectionFragment from the Intent
        Bundle extras = getIntent().getExtras();
        postID = extras.getInt("POST_ID");
        // Retrieve the Post from the database
        commentedOn = controller.getPost(postID);
        // Retrieve the list of comments of the post from the databse
        commentsList = controller.getPostComments(postID);
        commentListView = (ListView) findViewById(R.id.commentsList);
        adapter = new CommentListAdapter(ctx, commentsList);
        View cHeader = getLayoutInflater().inflate(R.layout.comment_header, null);
        // Add a header to label the comments' ListView
        commentListView.addHeaderView(cHeader);
        commentListView.setAdapter(adapter);
        commentListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);



        //AMJAD'S
        commentListView.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                commentListView.setSelection(adapter.getCount() - 1);
            }
        });

        commentTextField = (EditText) findViewById(R.id.commentTextField);
        TextView commentedOnName = (TextView) findViewById(R.id.commentedOnName);
        TextView commentedOnText = (TextView) findViewById(R.id.commentedOn);
        ImageView commentedOnImage = (ImageView) findViewById(R.id.commentedOnImage);

        // Display the post's attributes
        commentedOnName.setText(commentedOn.getPostedBy());
        // Make the post body's TextView invisible if the text equals null
        if(!commentedOn.getPostBody().equals("")){
            commentedOnText.setText(commentedOn.getPostBody());
        }else {
            commentedOnText.setVisibility(View.GONE);
        }

        // Display an ImageView and set an image to it if an image existed
        if(!commentedOn.getBitmap().equals("")){
            String imageBitmap = commentedOn.getBitmap();
            byte[] decodedString = Base64.decode(imageBitmap, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            commentedOnImage.setImageBitmap(decodedByte);
        }
        else {
            commentedOnImage.setVisibility(View.GONE);
        }

        // Enables the send button only if the EditText is not empty
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


        // Send comment button is clicked
        sendCommentButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View view) {
                        String commentText = commentTextField.getText().toString();
                        try {
                            //
                            comment.setCommentBody(commentText);
                            comment.setCommentedBy(controller.retrieveCurrentUsername());
                            comment.setPostID(postID);
                            postComment(comment);


                        } catch (SQLException e) {
                            Log.i(TAG, "A problem in adding comment to DB");
                        }
                        // Add the comment to the comment adapter and update the list
                        commentsList.add(comment);
                        adapter.notifyDataSetChanged();
                        commentTextField.setText("");

                    }
                }
        );
    }//End onCreate


    /**
     * Displays a dialog that prompts the user to choose
     * whether to save an image to gallery or not. If yes,
     * it gets the image attribute from the post object,
     * after it decodes the String to bitmap.
     * @param view A View occupies a rectangular area on
     *             the screen and is responsible for
     *             drawing and event handling
     */
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

    /**
     * Takes the image bitmap and creates a unique metadata
     * for it in order to store it in the phone gallery (external storage) using an OutPutStream.
     * @param bmp bitmap of the image
     */
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

    /**
     * Converts the retrieved int values from calendar to a String
     * @param val int calender value
     * @return String
     */
    public String fromInt(int val)
    {
        return String.valueOf(val);
    }

    /**
     * Triggers the MediaScannerConnection which provides a way
     * for applications to pass an image file to the media scanner
     * service. The media scanner service will read metadata from
     * the file and add the file to the media content provider.
     * @param imageFileName String for the image path
     */
    public void scanPhoto(final String imageFileName)
    {

        msConn = new MediaScannerConnection(ctx , new MediaScannerConnection.MediaScannerConnectionClient() {

            @Override
            public void onMediaScannerConnected() {

                msConn.scanFile(imageFileName, null);
                Log.i(TAG, "connection established");
            }

            @Override
            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
                Log.i(TAG,"scan completed");

            }
        });
        msConn.connect();
    }


    /**
     * Sets the added comment in a UdpBroadcastManager instance
     * that extends AsyncTask and executes it to broadcast
     * the comment to the connected devices
     * @param comment the Comment to be broadcasted
     */
    private void postComment(Comment comment){
        UdpBroadcastManager broadcastManager = new UdpBroadcastManager();
        broadcastManager.setComment(comment);
        broadcastManager.execute();
    }

}