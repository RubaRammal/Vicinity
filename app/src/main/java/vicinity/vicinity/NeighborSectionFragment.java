package vicinity.vicinity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.support.v4.app.Fragment;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.ArrayList;

import vicinity.ConnectionManager.ConnectAndDiscoverService;
import vicinity.ConnectionManager.WiFiP2pService;


public class NeighborSectionFragment extends Fragment {

    public final String TAG = "Neighbors";

    private Context ctx;
    private ArrayList<WiFiP2pService> listOfServices;
    private ArrayList<WiFiP2pService> friendServices;

    private ListView lvn;
    private ListView lvf;
    private NeighborListAdapter neighborListAdapter;
    private FriendListAdapter friendListAdapter;
    private ProgressBar progress;
    private TextView header;


    public interface DeviceClickListener {
        public void connectP2p(WiFiP2pService wifiP2pService);
    }

    public NeighborSectionFragment(){}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_neighbor, container, false);



        listOfServices = new ArrayList<WiFiP2pService>();
        friendServices = new ArrayList<WiFiP2pService>();
        friendServices.add(new WiFiP2pService(new WifiP2pDevice(), "ghjgjh", "jgjkgk"));


        ctx = this.getActivity();

        //progress = (ProgressBar) rootView.findViewById(R.id.temp);
        lvn = (ListView) rootView.findViewById(R.id.listNeighbors);
        lvf = (ListView) rootView.findViewById(R.id.listFriends);

        neighborListAdapter = new NeighborListAdapter(ctx, listOfServices);
        friendListAdapter = new FriendListAdapter(ctx, friendServices);

        //progress.setVisibility(View.VISIBLE);
        //mRelativeLayout.setVisibility(View.GONE);

        ConnectAndDiscoverService.setNAdapter(neighborListAdapter);

        View nHeader = inflater.inflate(R.layout.neighbor_header, null);
        View fHeader = inflater.inflate(R.layout.friend_header, null);

        lvn.addHeaderView(nHeader);
        lvf.addHeaderView(fHeader);

        lvn.setAdapter(neighborListAdapter);
        lvf.setAdapter(friendListAdapter);

        //Utility.setListViewHeightBasedOnChildren(lvn);
        //Utility.setListViewHeightBasedOnChildren(lvf);
        //neighborListAdapter.notifyDataSetChanged();


        lvn.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG,"Clicked: "+neighborListAdapter.getItem(position).toString()) ;
                ((DeviceClickListener) ConnectAndDiscoverService.ctx).connectP2p((WiFiP2pService) neighborListAdapter
                        .getItem(position));
            }
        });



        return rootView;
    }



}