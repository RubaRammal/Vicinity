package vicinity.vicinity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import vicinity.Controller.MainController;
import vicinity.model.VicinityMessage;

/**
 * This class is where a list of all the current chats a user has is displayed.
 */

public class MessagesSectionFragment extends Fragment {

    private Context ctx;
    private String TAG = "MessagesSectionFragment";
    private MainController controller;
    private MessageListAdapter adapter;
    public MessagesSectionFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);

        ctx = this.getActivity();

        controller = new MainController(ctx);

        ArrayList<VicinityMessage> vicinityMessages = controller.viewAllMessages();
        ArrayList<VicinityMessage> chatMsgs = new ArrayList<>();

        //Returns the chat IDs of all messages
        int[] ids = controller.viewChatIds();


        //Make a case for 1 later
        try{
        //Fills an ArrayList with the last message of every chat to send it to the adapter for display
        for (int i=0; i<ids.length; i++){
            ArrayList<VicinityMessage> temp = controller.getChatMessages(ids[i]);
            //Log.i(TAG, "From id: "+temp.get(i).getMessageBody());

            chatMsgs.add(temp.get(temp.size()-1));
            if(ids[i]==0)
                break;
        }
        }
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        for (int i=0; i<chatMsgs.size() ;i++){
        Log.i(TAG, "last msg: "+chatMsgs.get(i).getMessageBody());
            Log.i(TAG, "last id: "+chatMsgs.get(i).getChatId());
        }

        adapter = new MessageListAdapter(this.getActivity(), chatMsgs);


        //Get the fragment_messages layout ListView
        final ListView lv = (ListView) rootView.findViewById(R.id.messagesList);

        //Create the message rows (message_row_view) in the ListView
        lv.setAdapter(adapter);

        //List item click listener
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int msgId = ((VicinityMessage) adapter.getItem(position)).getChatId();
                        Log.i(TAG, ((VicinityMessage) adapter.getItem(position)).getMessageBody());

                        Intent intent = new Intent(ctx, ChatActivity.class);
                        intent.putExtra("MSG_ID", msgId);
                        startActivity(intent);
                        Log.i(TAG, "ItemClicked");
                    }
                }
        );

        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        adapter.notifyDataSetChanged();

    }


}
