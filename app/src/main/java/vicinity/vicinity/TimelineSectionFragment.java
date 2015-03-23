package vicinity.vicinity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import vicinity.model.Post;
import vicinity.model.User;



/**
 * A list of all the posts is displayed in this class.
 */

public class TimelineSectionFragment extends Fragment {

    public TimelineSectionFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);


        ArrayList<Post> posts = GetPosts();
        Button addPost = (Button) rootView.findViewById(R.id.add_post);


        final ListView lv = (ListView) rootView.findViewById(android.R.id.list);


        lv.setAdapter(new PostListAdapter(this.getActivity(), posts));

        lv.setOnItemClickListener(
           new AdapterView.OnItemClickListener(){
               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   Intent intent = new Intent(getActivity(), PostComment.class);
                   startActivity(intent);
               }
           }
        );

    //When Post is clicked
        addPost.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), AddPost.class);
                        startActivity(intent);
                    }
                }
        );

        return rootView;
    }

    //The PRIVATE METHOD SHOULD BE DELETED AND THE METHOD THAT
    // RETURNS THE POSTS ARRAY LIST SHOULD BE CALLED IN THIS CLASS
    private ArrayList<Post> GetPosts(){
        ArrayList<Post> posts = new ArrayList<Post>();

        Post post = new Post(new User("Ruba"), "Vicinity is a great way to communicate with people around you!");
        posts.add(post);

        post = new Post(new User("Afnan"), "\"The best way to predict your future is to create it.\"");
        posts.add(post);

        post = new Post(new User("Amal"), "Cool app!");
        posts.add(post);

        post = new Post(new User("Sarah"), "\"Be nice to nerds. Chances are you'll end up working for one.\" - Bill gates");
        posts.add(post);

        post = new Post(new User("Lama"), "On my way to KSU...");
        posts.add(post);

        post = new Post(new User("Amjad"), "\"My advice is to never do tomorrow what you can do today. Procrastination is the thief of time.\" - Charles Dickens");
        posts.add(post);

        return posts;
    }

}

