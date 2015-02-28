package vicinity.vicinity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import vicinity.model.Post;
import vicinity.model.PostListAdapter;
import vicinity.model.User;



/**
 * Created by macproretina on 2/13/15.
 */

public class TimelineSectionFragment extends Fragment {

    public TimelineSectionFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);

        String[] testPosts = {"Hello", "Wassap", "Hey"};

        ArrayList<Post> posts = GetPosts();

        ListView lv = (ListView)rootView.findViewById(R.id.timlineList);
        ListAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, testPosts);
        lv.setAdapter(adapter);

        //lv.setAdapter(new PostListAdapter(getActivity(), posts));

        lv.setOnItemClickListener(
           new AdapterView.OnItemClickListener(){
               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   Intent intent = new Intent(getActivity(), PostComment.class);
                   startActivity(intent);
               }
           }
        );



        return rootView;
    }

    private ArrayList<Post> GetPosts(){
        ArrayList<Post> posts = new ArrayList<Post>();

        Post post = new Post(new User("Ruba"), "Hello World");
        posts.add(post);

        post = new Post(new User("Afnan"), "Hi World");
        posts.add(post);


        return posts;
    }

}

