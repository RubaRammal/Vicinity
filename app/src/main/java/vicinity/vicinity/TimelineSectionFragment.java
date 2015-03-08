package vicinity.vicinity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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


        ArrayList<Post> posts = GetPosts();


        final ListView lv = (ListView) rootView.findViewById(R.id.timlineList);
        //ListAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, testPosts);
        //lv.setAdapter(adapter);

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



        return rootView;
    }

    private ArrayList<Post> GetPosts(){
        ArrayList<Post> posts = new ArrayList<Post>();


        //We should get these from the db and create a loop
        Post post = new Post(new User("Ruba"), "Hello World");
        posts.add(post);

        post = new Post(new User("Afnan"), "Hi World");
        posts.add(post);

        post = new Post(new User("Amal"), "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. ");
        posts.add(post);

        return posts;
    }

}

