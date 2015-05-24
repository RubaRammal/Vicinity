package vicinity.vicinity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import vicinity.Controller.MainController;
import vicinity.model.Comment;
import vicinity.model.Post;


/**
 * A list of all the posts is displayed in this class.
 */

public class TimelineSectionFragment extends Fragment {

    private static String TAG = "Timeline";
    private static PostListAdapter adapter; // Binds Post objects with the fragment's ListView
    private Context ctx; // The parent Activity's context
    private MainController controller;
    private MediaScannerConnection msConn ;


        /*---------Overridden Methods------------*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
        setRetainInstance(true);
        if (savedInstanceState != null) {
           // Restore last state for checked position.
        }

    }

    @Override
    public void onStart()
    {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);

        // Initialization
        ctx = this.getActivity();
        controller = new MainController(ctx);
        Button addPost = (Button) rootView.findViewById(R.id.add_post);
        ListView lv = (ListView) rootView.findViewById(android.R.id.list);
        adapter = new PostListAdapter(this.getActivity());
        lv.setAdapter(adapter);

        // When the item is clicked the ID of the Post bound to it
        // is obtained and added to an Intent that calls the CommentActivity
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        int postID = ((Post) adapter.getItem(position)).getPostID();
                        Log.i(TAG, ((Post) adapter.getItem(position)).getPostBody());
                        Intent intent = new Intent(getActivity(), CommentActivity.class);
                        intent.putExtra("POST_ID", postID);
                        startActivity(intent);
                    }
                }
        ); //END setOnItemClickListener


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            /**
             * Prompts the user to choose whether to save the image or
             * to cancel. Then gets the image attribute from the post
             * object, after it decodes the String to bitmap.
             * @param parent returns the adapter view where the click occurred
             * @param view A View occupies a rectangular area on
             *             the screen and is responsible for
             *             drawing and event handling
             * @param position position of the clicked item
             * @param id id of the item
             * @return
             */
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {


                if(!((Post) adapter.getItem(position)).getBitmap().equals("")){

                    new AlertDialog.Builder(TabsActivity.ctx)
                            .setTitle("Save image")
                            .setMessage("Do you want to save this image?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "YES");
                                    String imageBitmap = ((Post) adapter.getItem(position)).getBitmap();

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

                Log.i("long clicked","pos"+" "+position);

                return true;
            }
        });

        // Receives objects from LocalBroadcastManager
        BroadcastReceiver updateUI = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    final Bundle bundle = intent.getExtras();

                    // If the object is a Post object
                    if (intent.getAction().equals("POST"))
                    {
                        // Push the Post to the ListView using the adapter
                        Post receivedPost = (Post) bundle.getSerializable("NEW_POST");
                        Log.i(TAG, "Received in timeline: " + receivedPost.toString());
                        adapter.addPost(receivedPost);

                    }
                    // If the object is a Comment object
                    else if (intent.getAction().equals("COMMENT"))
                    {
                        Comment receivedComment = (Comment) bundle.getSerializable("NEW_COMMENT");
                        Log.i(TAG, "Received in timeline: " + receivedComment.getCommentBody());

                        // Add the Comment to the database
                        controller.addAcomment(receivedComment);
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }


            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("POST");
        filter.addAction("COMMENT");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((updateUI), filter);


        // Click on Post Button calls the NewPostActivity
        addPost.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), NewPostActivity.class);
                        startActivity(intent);
                    }
                }
        );


        return rootView;
    } //END onCreateView


    /**
     * Takes the image bitmap and creates a unique metadata
     * for it in order to store it in the phone gallery (external storage) using an OutPutStream.
     * @param bmp image bitmap
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
     * @param val calender value
     * @return String
     */
    public String fromInt(int val)
    {
        return String.valueOf(val);
    }

    /**
     * Triggers the MediaScannerConnection which provides a way
     * for the application to pass an image file to the media scanner
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
                Log.i("Photo Utility", "connection established");
            }

            @Override
            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
                Log.i("Photo Utility","scan completed");

            }
        });
        msConn.connect();
    }


   public static void clearPosts()
   {
       adapter.notifyDataSetChanged();
   }


}