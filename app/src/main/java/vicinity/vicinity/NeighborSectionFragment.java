package vicinity.vicinity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.Fragment;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import vicinity.vicinity.ChatActivity;
import vicinity.vicinity.ConnectAndDiscoverService;
import vicinity.vicinity.MessageListAdapter;
import vicinity.vicinity.NeighborListAdapter;
import vicinity.vicinity.R;
import vicinity.vicinity.Tabs;

/**
 * Created by macproretina on 3/23/15.
 */
public class NeighborSectionFragment extends Fragment {

    public final String TAG = "Neighbors";

    private Context ctx;
    private ArrayList<String> listOfServices;
    private ListView lv;
    private NeighborListAdapter neighborListAdapter;
    public NeighborSectionFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_neighbor, container, false);
        listOfServices = new ArrayList<String>();
        ctx = this.getActivity();
        listOfServices.add("No neighbors");

        lv = (ListView) rootView.findViewById(R.id.neighborList);
        neighborListAdapter = new NeighborListAdapter(ctx, listOfServices );


        int secondsDelay = 10;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {

               //listOfServices.add(ConnectAndDiscoverService.neighbors.get(0));
                //listOfServices.add(ConnectAndDiscoverService.neighbors.get(1));
                if(!ConnectAndDiscoverService.neighbors.isEmpty()) {
                    //listOfServices.remove(0);
                    for (int i = 0; i < ConnectAndDiscoverService.neighbors.size(); i++) {
                        listOfServices.add(ConnectAndDiscoverService.neighbors.get(i));
                    }
                }
                else
                listOfServices.set(0, "No neighbors");

                Log.i(TAG, listOfServices.get(0));

            }

        }, secondsDelay * 1000);

        lv.setAdapter(neighborListAdapter);

        //listOfServices = c.getNeighbors();
        //listOfServices = tab.getServices();

        if(!ConnectAndDiscoverService.neighbors.isEmpty()) {
            lv.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(ctx, ChatActivity.class);
                            startActivity(intent);
                        }
                    }
            );

        }



        return rootView;
    }
    /**
     * setters
     */


    public ListView getListView(){
        return lv;
    }


}