package vicinity.vicinity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.Fragment;
import android.widget.ProgressBar;


import java.util.ArrayList;

import vicinity.ConnectionManager.ConnectAndDiscoverService;
import vicinity.ConnectionManager.WiFiP2pService;


public class NeighborSectionFragment extends Fragment {

    public final String TAG = "Neighbors";

    private Context ctx;
    private ArrayList<WiFiP2pService> listOfServices;
    private ListView lv;
    private NeighborListAdapter neighborListAdapter;
    private ProgressBar progress;


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

        //progress = (ProgressBar) rootView.findViewById(R.id.temp);
        lv = (ListView) rootView.findViewById(android.R.id.list);
        neighborListAdapter = new NeighborListAdapter(ctx, listOfServices);
        neighborListAdapter.setPB(progress);

        //progress.setVisibility(View.VISIBLE);
        //mRelativeLayout.setVisibility(View.GONE);

        ConnectAndDiscoverService.setNAdapter(neighborListAdapter);


        lv.setAdapter(neighborListAdapter);
        //neighborListAdapter.notifyDataSetChanged();



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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