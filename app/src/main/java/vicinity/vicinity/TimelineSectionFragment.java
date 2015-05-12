package vicinity.vicinity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import java.sql.SQLException;
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


    /*
    private void TimerMethod()
    {
        getActivity().runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            adapter.notifyDataSetChanged();
        }
    };*/



}