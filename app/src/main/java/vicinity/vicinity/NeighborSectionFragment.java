package vicinity.vicinity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.Fragment;


import java.util.ArrayList;

import vicinity.ConnectionManager.ConnectAndDiscoverService;
import vicinity.ConnectionManager.WiFiP2pService;


public class NeighborSectionFragment extends Fragment {

    public final String TAG = "Neighbors";

    private Context ctx;
    private ArrayList<WiFiP2pService> listOfServices;
    private ListView lv;
    private NeighborListAdapter neighborListAdapter;


    public interface DeviceClickListener {
        public void connectP2p(WiFiP2pService wifiP2pService);
    }

    public NeighborSectionFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_neighbor, container, false);
        listOfServices = new ArrayList<WiFiP2pService>();
        ctx = this.getActivity();

        lv = (ListView) rootView.findViewById(R.id.neighborList);
        neighborListAdapter = new NeighborListAdapter(ctx, listOfServices);

        ConnectAndDiscoverService.setNAdapter(neighborListAdapter);


     /*   int secondsDelay = 10;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {

               //listOfServices.add(ConnectAndDiscoverService.neighbors.get(0));
                //listOfServices.add(ConnectAndDiscoverService.neighbors.get(1));
                if(!ConnectAndDiscoverService.neighbors.isEmpty()) {
                    for (int i = 0; i < ConnectAndDiscoverService.neighbors.size(); i++) {
                        listOfServices.add(ConnectAndDiscoverService.neighbors.get(i));
                    }
                }
                else
                listOfServices.set(0, "No neighbors");

                Log.i(TAG, listOfServices.get(0));

            }

        }, secondsDelay * 1000); */

        lv.setAdapter(neighborListAdapter);
        neighborListAdapter.notifyDataSetChanged();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Log.i(TAG,"Clicked: "+neighborListAdapter.getItem(position).toString()) ;
                ((DeviceClickListener) ConnectAndDiscoverService.ctx).connectP2p((WiFiP2pService) neighborListAdapter
                     .getItem(position))   ;
            }
        });

        return rootView;
    }



}