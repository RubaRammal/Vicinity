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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import vicinity.Controller.MainController;
import vicinity.model.Neighbor;
import vicinity.model.VicinityMessage;

/**
 * This class is where a list of all the current chats a user has is displayed.
 */

public class MessagesSectionFragment extends Fragment {

    private Context ctx;
    private String TAG = "MessagesSectionFragment";
    private MainController controller;
    private MessageListAdapter adapter;
    private ArrayList<VicinityMessage> history;
    private ArrayList<VicinityMessage> chatMsgs;
    private ListView lv;
    private Neighbor chatFriend;

    public MessagesSectionFragment(){}


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
        Log.i(TAG, "OnDetach");
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putSerializable("MESSAGES_LIST", chatMsgs);

        Log.i(TAG,"onSaveInstanceState");

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG,"onActivityCreated");
        setRetainInstance(true);
        if (savedInstanceState != null) {
            chatMsgs = (ArrayList<VicinityMessage>) savedInstanceState.getSerializable("MESSAGES_LIST");
            adapter = new MessageListAdapter(this.getActivity(), chatMsgs);

        }

    }
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


        adapter = new MessageListAdapter(this.getActivity(), chatMsgs);


        //Get the fragment_messages layout ListView
        lv = (ListView) rootView.findViewById(R.id.messagesList);

        //Create the message rows (message_row_view) in the ListView
        lv.setAdapter(adapter);

        //List item click listener
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        VicinityMessage msg = ((VicinityMessage) adapter.getItem(position));
                        Log.i(TAG, ((VicinityMessage) adapter.getItem(position)).getMessageBody());
                        setMsgs(msg.getFrom());
                        chatFriend = controller.getFriend(msg.getFrom());

                        if(!(chatFriend == null))
                        {
                            Intent intent = new Intent(ctx, ChatActivity.class);
                            intent.putExtra("MSG", msg);
                            startActivity(intent);
                            Log.i(TAG, "ItemClicked");
                        }
                    }
                }
        );


        return rootView;
    }


    public void setMsgs(String id){
        history = controller.getChatMessages(id);
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
                    //Log.i(TAG, temp.get(i).toString());

                    if(temp.size()==0)
                        break;

                    chatMsgs.add(temp.get(temp.size() - 1));

                }
            }
            catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }

            for (int i=0; i<chatMsgs.size() ;i++){
                Log.i(TAG, "last msg: "+chatMsgs.get(i).getMessageBody());
                Log.i(TAG, "last ip: "+chatMsgs.get(i).getFrom());
            }

            adapter = new MessageListAdapter(this.getActivity(), chatMsgs);

            //Create the message rows (message_row_view) in the ListView
            lv.setAdapter(adapter);

            //List item click listener
            lv.setOnItemClickListener(
                    new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            VicinityMessage msg = ((VicinityMessage) adapter.getItem(position));
                            Log.i(TAG, ((VicinityMessage) adapter.getItem(position)).getMessageBody());
                            setMsgs(msg.getFrom());
                            chatFriend = controller.getFriend(msg.getFrom());

                                Intent intent = new Intent(ctx, ChatActivity.class);
                                intent.putExtra("MSG", msg);
                                startActivity(intent);
                                Log.i(TAG, "ItemClicked");

                        }
                    }
            );



        }
    }

}



