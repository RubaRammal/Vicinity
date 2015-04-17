package vicinity.vicinity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import vicinity.Controller.MainController;
import vicinity.model.Post;
import android.app.Activity;


/**
 * A list of all the posts is displayed in this class.
 */

public class TimelineSectionFragment extends Fragment {

    private static PostListAdapter adapter;
    private Context ctx;
    private static String TAG = "Timeline";
    private MainController controller;
    private static ArrayList<Post> posts ;
    //private TimelineInterface tlCommander;

    /* this is not tested - amjad
    // Interface to communicate with PostComment activity
    public interface TimelineInterface {
        public void sendClickedPost(Post post);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            tlCommander = (TimelineInterface)  activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TimelineInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        tlCommander = null;
    }*/

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);

        ctx = this.getActivity();
        controller = new MainController(ctx);
        posts = new ArrayList<Post>();
        posts.addAll(controller.viewAllPosts());
        Button addPost = (Button) rootView.findViewById(R.id.add_post);

        final ListView lv = (ListView) rootView.findViewById(android.R.id.list);
        adapter = new PostListAdapter(this.getActivity(), posts);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
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

        //When Post is clicked
        addPost.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), NewPost.class);
                        startActivity(intent);
                    }
                }
        );

        return rootView;
    } //END onCreateView

    /**
     * sends post form NewPost activity to TimelineSectionFragment
     * @param aPost the post to be added to posts list
     */
    public static void postToTimeline (Post aPost) {
        if (aPost != null) {
            posts.add(aPost);
            adapter.notifyDataSetChanged();
        }
        else
            Log.i(TAG, "Post object is null");
    } //END postToTimeline

}

