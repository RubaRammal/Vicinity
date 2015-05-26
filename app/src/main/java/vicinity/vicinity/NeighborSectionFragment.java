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
import java.util.Iterator;

import vicinity.ConnectionManager.ConnectAndDiscoverService;
import vicinity.model.Globals;
import vicinity.model.Neighbor;

/**
 * Displays neighbors and friends in the device's range.
 * Provides connecting to/muting  devices,
 * adding/deleting/changing name of friends.
 */
public class NeighborSectionFragment extends Fragment {

    public final String TAG = "Neighbors";

    private Context ctx;
    private static ArrayList<Neighbor> listOfServices;
    private static ArrayList<Neighbor> friendServices;
    private static NeighborListAdapter neighborListAdapter;
    private static FriendListAdapter friendListAdapter;



    /**
     * This Interface provides the method that connects peers
     * it is implemented in ConnectAndDiscoverService
     */
    public interface DeviceClickListener {
        public void connectP2p(Neighbor wifiP2pService);

    }

    public NeighborSectionFragment(){}


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG,"onActivityCreated");
        setRetainInstance(true);
        if (savedInstanceState != null) {
            Log.i(TAG,"SavedInstance!=null");

            neighborListAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_neighbor, container, false);


        // Initialization
        listOfServices = new ArrayList<Neighbor>();
        friendServices = new ArrayList<Neighbor>();
        ctx = this.getActivity();

        ListView lvn = (ListView) rootView.findViewById(R.id.listNeighbors);
        ListView lvf = (ListView) rootView.findViewById(R.id.listFriends);

        neighborListAdapter = new NeighborListAdapter(ctx, listOfServices);
        friendListAdapter = new FriendListAdapter(ctx, friendServices);

        ConnectAndDiscoverService.setNAdapter(neighborListAdapter);
        ConnectAndDiscoverService.setFAdapter(friendListAdapter);

        View nHeader = inflater.inflate(R.layout.neighbor_header, null);
        View fHeader = inflater.inflate(R.layout.friend_header, null);

        lvn.addHeaderView(nHeader);
        lvf.addHeaderView(fHeader);

        lvn.setAdapter(neighborListAdapter);
        lvf.setAdapter(friendListAdapter);

        // When the item is clicked a connection permission
        // is sent to the neighbor bound to the list item
        lvn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Clicked: " + neighborListAdapter.getItem(position - 1).toString());
                final Neighbor neighbor = (Neighbor) neighborListAdapter.getItem(position - 1);
                ((DeviceClickListener) ConnectAndDiscoverService.ctx).connectP2p(neighbor);
            }
        });

        // When the item is clicked the Neighbor object bound to it is obtained
        // and id added to an Intent that calls the ChatActivity if the friends were
        // connected, if they were not connected the item click sends a connection
        // permission to the obtained neighbor.
        lvf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Clicked: " + friendListAdapter.getItem(position - 1).toString());
                final Neighbor friend = (Neighbor) friendListAdapter.getItem(position - 1);
                if (Globals.isConnectedToANetwork) {
                    Intent intent = new Intent(ctx, ChatActivity.class);
                    intent.putExtra("FRIEND", friend);
                    startActivity(intent);
                } else {
                    ((DeviceClickListener) ConnectAndDiscoverService.ctx).connectP2p(friend);
                }

            }
        });


        return rootView;
    }

    /**
     * This method is  Called after a friend deletes another
     * it updates friends list
     * @param deletedFriend A Neighbor object
     */

    public static void updateDeletedFriend(Neighbor deletedFriend){
        Iterator<Neighbor> it = friendServices.iterator();
        while (it.hasNext()) {
            Neighbor user = it.next();
            if (user.getDeviceAddress().equals(deletedFriend.getDeviceAddress())) {
                it.remove();
                friendListAdapter.notifyDataSetChanged();
            }
        }
        listOfServices.add(deletedFriend);
        neighborListAdapter.notifyDataSetChanged();
    }

    /**
     * This method is used after sending/accepting a friend request
     * it updates friends list with the new friend
     * and removes the peer from the neighbors list
     * @param neighbor A Neighbor object
     */
    public static void updateAddedFriend(Neighbor neighbor){
        friendServices.add(neighbor);
        friendListAdapter.notifyDataSetChanged();
        Iterator<Neighbor> it = listOfServices.iterator();
        while (it.hasNext()) {
            Neighbor user = it.next();
            if (user.getDeviceAddress().equals(neighbor.getDeviceAddress())) {
                it.remove();
            }
        }
        neighborListAdapter.notifyDataSetChanged();
    }

    /**
     * This method is used to update friends list
     * from the service dynamically
     * @param friend A Neighbor object that contains an online friend
     */
    public static void addToFriendsList(Neighbor friend){
        if(friendServices!=null)
            if(!friendServices.contains(friend)){
                friendServices.add(friend);
                friendListAdapter.notifyDataSetChanged();
            }
    }

    /**
     * This method is used to update neighbors list
     * from the service dynamically
     * @param neighbor A Neighbor object that contains an online peer
     */
    public static void addToNeighborsList (Neighbor neighbor)throws NullPointerException{
        if(listOfServices!=null)
            if(!listOfServices.contains(neighbor)){
                listOfServices.add(neighbor);
                neighborListAdapter.notifyDataSetChanged();
            }
    }

}