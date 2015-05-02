package vicinity.vicinity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.support.v4.app.Fragment;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.sql.SQLException;
import java.util.ArrayList;

import vicinity.ConnectionManager.ConnectAndDiscoverService;
import vicinity.model.WiFiP2pService;
import vicinity.Controller.MainController;


public class NeighborSectionFragment extends Fragment {

    public final String TAG = "Neighbors";

    private Context ctx;
    private static ArrayList<WiFiP2pService> listOfServices;
    private static ArrayList<WiFiP2pService> friendServices;
    private MainController controller;
    private ListView lvn;
    private ListView lvf;
    private static NeighborListAdapter neighborListAdapter;
    private static FriendListAdapter friendListAdapter;
    private ProgressBar progress;
    private TextView header;
    private Button deleteFriend;


    public interface DeviceClickListener {
        public void connectP2p(WiFiP2pService wifiP2pService);
    }

    public NeighborSectionFragment(){}

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
        Log.i(TAG,"onSaveInstanceState");
        //outState.putInt("jj",0);
        //if(listOfServices.size()!=0)
         //   outState.putSerializable("Users", listOfServices);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG,"onActivityCreated");
        setRetainInstance(true);
        if (savedInstanceState != null) {
            Log.i(TAG,"SavedInstance!=null");
            //listOfServices = (ArrayList<WiFiP2pService>)savedInstanceState.getSerializable("Users");
           // neighborListAdapter.notifyDataSetChanged();

            // Restore last state for checked position.
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_neighbor, container, false);


        controller = new MainController(getActivity());
        listOfServices = new ArrayList<WiFiP2pService>();
        friendServices = new ArrayList<WiFiP2pService>();
        ctx = this.getActivity();

        //progress = (ProgressBar) rootView.findViewById(R.id.temp);
        lvn = (ListView) rootView.findViewById(R.id.listNeighbors);
        lvf = (ListView) rootView.findViewById(R.id.listFriends);

        neighborListAdapter = new NeighborListAdapter(ctx, listOfServices);
        friendListAdapter = new FriendListAdapter(ctx, friendServices);

        //progress.setVisibility(View.VISIBLE);
        //mRelativeLayout.setVisibility(View.GONE);

        ConnectAndDiscoverService.setNAdapter(neighborListAdapter);
        ConnectAndDiscoverService.setFAdapter(friendListAdapter);
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
                final WiFiP2pService neighbor = (WiFiP2pService) neighborListAdapter.getItem(position);
                final int p = position;
                new AlertDialog.Builder(getActivity())
                        .setTitle("Friendship Request")
                        .setMessage("Do you want to add "+neighbor.getInstanceName()+" as a friend?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try{
                                boolean isAdded = controller.addFriend(neighbor.getInstanceName(),neighbor.getDeviceAddress());
                                    if(isAdded){
                                ((DeviceClickListener) ConnectAndDiscoverService.ctx).connectP2p((WiFiP2pService) neighborListAdapter
                                        .getItem(p));
                                    friendServices.add(neighbor);
                                    friendListAdapter.notifyDataSetChanged();
                                    listOfServices.remove(neighbor);
                                    neighborListAdapter.notifyDataSetChanged();
                                    }
                                }
                                catch(SQLException e){
                                    e.printStackTrace();
                                }

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "no");

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });



        return rootView;
    }

        public static void updateDeletedFriend(WiFiP2pService deletedFriend){
                friendServices.remove(deletedFriend);
                listOfServices.add(deletedFriend);
                friendListAdapter.notifyDataSetChanged();
                neighborListAdapter.notifyDataSetChanged();
            }

    public static void addToFriendsList(WiFiP2pService friend){
        friendServices.add(friend);
        friendListAdapter.notifyDataSetChanged();
    }
    public static void addToNeighborssList(WiFiP2pService neighbor){
        listOfServices.add(neighbor);
        neighborListAdapter.notifyDataSetChanged();
    }

}