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

import vicinity.model.Friend;
import vicinity.model.Message;

/**
 * Created by macproretina on 2/13/15.
 */

public class MessagesSectionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);

        ArrayList<Message> messages = GetMessages();


        final ListView lv = (ListView) rootView.findViewById(R.id.messagesList);

        lv.setAdapter(new MessageListAdapter(this.getActivity(), messages));

        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        startActivity(intent);
                    }
                }
        );

        return rootView;
    }

    private ArrayList<Message> GetMessages(){
        ArrayList<Message> messages = new ArrayList<Message>();

        Message message = new Message(this.getActivity(), "1", true, "Hey");
        messages.add(message);

        message = new Message(this.getActivity(), "2", true, "Hello...");
        messages.add(message);


        //THIS PRIVATE METHOD SHOULD BE DELETED AND THE METHOD THAT
        // RETURNS THE MESSAGES ARRAY LIST SHOULD BE CALLED IN THIS CLASS

        return messages;
    }
}