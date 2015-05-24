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
import android.widget.ListView;

import java.util.ArrayList;

import vicinity.Controller.MainController;
import vicinity.model.VicinityMessage;

/**
 * This class is where a list of all
 * the current chats a user has is displayed.
 */

public class MessagesSectionFragment extends Fragment {

    private String TAG = "MessagesSectionFragment";
    private Context ctx; // The parent's activity's context
    private MainController controller;
    private MessageListAdapter adapter; // Adapter that binds the ListView with the last message of every chat
    private ArrayList<VicinityMessage> chatMsgs; //The last message in every chat
    private ListView lv;

    public MessagesSectionFragment(){}



        /*---------Overridden Methods------------*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);

        ctx = this.getActivity();

        controller = new MainController(ctx);

        chatMsgs = new ArrayList<>();

        //Returns the chat IDs of all messages
        ArrayList<VicinityMessage> vicinityMessages = controller.viewAllMessages();

        ArrayList<String> stringIPs = controller.viewChatIps();


        ArrayList<VicinityMessage> temp;
        try{
        //Fills an ArrayList with the last message of every chat to send it to the adapter for display
        for (int i=0; i<stringIPs.size(); i++){
            temp = controller.getChatMessages(stringIPs.get(i));
            //Log.i(TAG, temp.get(i).toString());

            if(temp.size()==0)
                break;

                chatMsgs.add(temp.get(temp.size() - 1));
        }
        }
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }


        //Get the fragment_messages layout ListView
        lv = (ListView) rootView.findViewById(R.id.messagesList);
        adapter = new MessageListAdapter(this.getActivity(), chatMsgs);

        // Create the message rows (message_row_view) in the ListView
        lv.setAdapter(adapter);

        // When the item is clicked message bound to it is obtained
        // and added to an Intent that calls the ChatActivity
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        VicinityMessage msg = ((VicinityMessage) adapter.getItem(position));
                        Log.i(TAG, ((VicinityMessage) adapter.getItem(position)).getMessageBody());

                        Intent intent = new Intent(ctx, ChatActivity.class);
                        intent.putExtra("MSG", msg);
                        startActivity(intent);
                    }
                }
        );

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){

            adapter.clearAdapter();
            adapter.notifyDataSetChanged();
            Log.i(TAG, "Message fragment is visible");


            //Returns the chat IDs of all messages
            ArrayList<VicinityMessage> vicinityMessages = controller.viewAllMessages();
            ArrayList<String> stringIPs = controller.viewChatIps();


            ArrayList<VicinityMessage> temp;
            try{
                //Fills an ArrayList with the last message of every chat to send it to the adapter for display
                for (int i=0; i<stringIPs.size(); i++){
                    temp = controller.getChatMessages(stringIPs.get(i));
                    if(temp.size()==0)
                        break;

                    chatMsgs.add(temp.get(temp.size() - 1));
                }
            }
            catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }

            adapter = new MessageListAdapter(this.getActivity(), chatMsgs);

            // Create the message rows (message_row_view) in the ListView
            lv.setAdapter(adapter);

            // When the item is clicked message bound to it is obtained
            // and added to an Intent that calls the ChatActivity
            lv.setOnItemClickListener(
                    new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            VicinityMessage msg = ((VicinityMessage) adapter.getItem(position));
                            Log.i(TAG, ((VicinityMessage) adapter.getItem(position)).getMessageBody());

                                Intent intent = new Intent(ctx, ChatActivity.class);
                                intent.putExtra("MSG", msg);
                                startActivity(intent);
                        }
                    }
            );



        }
    }

}



