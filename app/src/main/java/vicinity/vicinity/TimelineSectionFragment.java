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
import vicinity.model.Globals;
import vicinity.model.Post;
import vicinity.model.User;



/**
 * A list of all the posts is displayed in this class.
 */

public class TimelineSectionFragment extends Fragment {

    private PostListAdapter adapter;
    private Context ctx;
    private String TAG = "Timeline";
    private MainController controller;


    public TimelineSectionFragment(){}


    public interface ClickedPost {
        public void sendClickedPost(Post post);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);


        ctx = this.getActivity();
        controller = new MainController(ctx);
        ArrayList<Post> posts = GetPosts();
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

        );

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
    }

    //The PRIVATE METHOD SHOULD BE DELETED AND THE METHOD THAT
    // RETURNS THE POSTS ARRAY LIST SHOULD BE CALLED IN THIS CLASS
    public ArrayList<Post> GetPosts(){
        ArrayList<Post> posts = new ArrayList<Post>();

        Post post = new Post(new User("Ruba"), "Vicinity is a great way to communicate with people around you!", 1);
        posts.add(post);

        post = new Post(new User("Afnan"), "\"The best way to predict your future is to create it.\"",2);
        posts.add(post);

        post = new Post(new User("Amal"), "Cool app!",3);
        posts.add(post);

        post = new Post(new User("Sarah"), "\"Be nice to nerds. Chances are you'll end up working for one.\" - Bill gates",4);
        posts.add(post);

        post = new Post(new User("Lama"), "On my way to KSU...",5);
        posts.add(post);

        post = new Post(new User("Amjad"), "\"My advice is to never do tomorrow what you can do today. Procrastination is the thief of time.\" - Charles Dickens",6);
        posts.add(post);

        return posts;
    }

}

