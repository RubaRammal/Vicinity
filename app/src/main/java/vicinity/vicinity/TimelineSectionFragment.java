package vicinity.vicinity;

import android.app.Activity;
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

    private static PostListAdapter adapter;
    private Context ctx;
    private static String TAG = "Timeline";
    private MainController controller;
    //private static ArrayList<Post> posts ;
    BroadcastReceiver updateUI;
    private MediaScannerConnection msConn ;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            Log.i(TAG, "OnAttach");
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TimelineInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

    }
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);

        ctx = this.getActivity();
        controller = new MainController(ctx);
        //posts = new ArrayList<Post>();
        //posts.addAll(controller.viewAllPosts());
        final Button addPost = (Button) rootView.findViewById(R.id.add_post);

        final ListView lv = (ListView) rootView.findViewById(android.R.id.list);
        //adapter = new PostListAdapter(this.getActivity(), posts);
        adapter = new PostListAdapter(this.getActivity());
        //UDPpacketListner.setPostListAdapter(adapter);
        //adapter.notifyDataSetChanged();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //((ClickedPost) PostComment.ctx).sendClickedPost((Post) adapter.getItem(position));
                        int postID = ((Post) adapter.getItem(position)).getPostID();
                        Log.i(TAG, ((Post) adapter.getItem(position)).getPostBody());
                        Intent intent = new Intent(getActivity(), PostComment.class);
                        intent.putExtra("POST_ID", postID);
                        startActivity(intent);
                        Log.i(TAG, "ItemClicked");
                    }
                }
        ); //END setOnItemClickListener

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {


                //int postID = ((Post) adapter.getItem(position)).getPostID();
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

        updateUI = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    final Bundle bundle = intent.getExtras();

                    if(intent.getAction().equals("POST")) {
                        Log.i(TAG,"onReceive");
                        Post receivedPost = (Post) bundle.getSerializable("NEW_POST");
                        Log.i(TAG,"Received in timeline: "+receivedPost.toString());
                        adapter.addPost(receivedPost);

                    } else if (intent.getAction().equals("COMMENT")){

                        Comment receivedComment = (Comment) bundle.getSerializable("NEW_COMMENT");
                        Log.i(TAG,"Received in timeline: "+receivedComment.getCommentBody());

                        controller.addAcomment(receivedComment);

                    }



                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }




            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("POST");
        filter.addAction("COMMENT");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((updateUI),
                filter
        );


        //When Post is clicked
        addPost.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), NewPost.class);
                        startActivity(intent);
                    }
                }
        );


    /*
        try {

        Timer myTimer;
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 10000);

    }
    catch(NullPointerException e){
        e.printStackTrace();
    }
    */
        return rootView;
    } //END onCreateView


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

   public static void clearPosts(){

       adapter.notifyDataSetChanged();

   }



}