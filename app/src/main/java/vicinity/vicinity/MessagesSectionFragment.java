package vicinity.vicinity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import vicinity.ConnectionManager.WiFiP2pService;
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
        ArrayList<VicinityMessage> vicinityMessages = GetMessages();
        adapter = new MessageListAdapter(this.getActivity(), vicinityMessages);

        controller = new MainController(ctx);

        // Log.i(TAG, controller.viewAllMessages().get(0).getMessageBody());

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


    //The PRIVATE METHOD SHOULD BE DELETED AND THE METHOD THAT
    // RETURNS THE MESSAGES ARRAY LIST SHOULD BE CALLED IN THIS CLASS
    public ArrayList<VicinityMessage> GetMessages(){
        ArrayList<VicinityMessage> vicinityMessages = new ArrayList<VicinityMessage>();

        VicinityMessage vicinityMessage = new VicinityMessage(this.getActivity(),  "30-123943", 1, true, "Hey");
        vicinityMessages.add(vicinityMessage);

        vicinityMessage = new VicinityMessage(this.getActivity(),  "30-123943", 1, true, "Wassap");
        vicinityMessages.add(vicinityMessage);

        vicinityMessage = new VicinityMessage(this.getActivity(), "50-123943", 1, false, "Hola");
        vicinityMessages.add(vicinityMessage);

        return vicinityMessages;
    }
}