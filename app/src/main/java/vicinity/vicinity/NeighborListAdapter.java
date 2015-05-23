package vicinity.vicinity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import vicinity.ConnectionManager.RequestsManager;
import vicinity.Controller.MainController;
import vicinity.model.Globals;
import vicinity.model.Neighbor;


public class   NeighborListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    public static ArrayList<Neighbor> services;
    private static ImageButton mute, addFriend;
    private MainController controller;
    private static final String TAG = "NeighborListAdapter";

    /**
     * Public constructor
     * @param context Context
     * @param services2 ArrayList of Neighbor objects
     */
    public NeighborListAdapter(Context context, ArrayList<Neighbor> services2){
        services = services2;
        controller = new MainController(context);
        mInflater = LayoutInflater.from(context);
    }





    @Override
    public int getCount() {
        return services.size();
    }

    @Override
    public Object getItem(int position) {
        return services.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.neighbor_row_view, null);


            holder = new ViewHolder();
            holder.textName = (TextView) convertView.findViewById(R.id.neighbor_row);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textName.setText(services.get(position).getInstanceName());

        /*----Mute user button-----*/
        mute = (ImageButton) convertView.findViewById(R.id.muteButton);
        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Neighbor neighbor = (Neighbor) getItem(position);
                //First check if the user is connected to a network
                try{
                    if (Globals.isConnectedToANetwork) {


                        Log.i(TAG, "MUTE BUTTON CLICKED");

                        //Mute the neighbor if it was not muted already
                        if (!MainController.isUserMuted(neighbor)) {
                            Log.i(TAG, "MUTE");
                            MainController.muteNeighbor(neighbor);
                            mute.setImageResource(R.drawable.muteicon);

                        }
                        //Unmute a muted neighbor
                        else if (MainController.isUserMuted(neighbor)) {
                            Log.i(TAG, "UNMUTE!!!");
                            MainController.unmuteNeighbor(neighbor);
                            mute.setImageResource(R.drawable.muteicon2);
                        }

                    }
                    else{
                        Toast.makeText(TabsActivity.ctx, "You cannot mute this neighbor, you are not connected to a network.", Toast.LENGTH_LONG).show();
                    }}
                catch(Resources.NotFoundException e){
                    e.printStackTrace();
                }





            }


        });

        /*----Add neighbor as a friend  button-----*/
        addFriend = (ImageButton)convertView.findViewById(R.id.addAsFriend);
        addFriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i(TAG,"Add friend clicked");
                if(Globals.isConnectedToANetwork){


                    final Neighbor neighbor = (Neighbor) getItem(position);
                    if(MainController.isThisIPMuted(neighbor.getIpAddress())){
                        Toast.makeText(TabsActivity.ctx, "You have to unmute this neighbor first", Toast.LENGTH_LONG).show();

                    }
                    else{
                        new AlertDialog.Builder(TabsActivity.ctx)
                                .setTitle("Add a friend")
                                .setMessage("Do you want to add " + neighbor.getInstanceName() + " as a friend?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.i(TAG, "Neighbor status: " + neighbor.getStatus());
                                        //Sending the request through RequestsManager AsyncTask
                                        new RequestsManager().execute(neighbor);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.i(TAG, "no");

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();}
                }
                else {

                    Toast.makeText(TabsActivity.ctx, "You cannot add this neighbor, you are not connected to a network.", Toast.LENGTH_LONG).show();
                }

            }

        });

        return convertView;
    }

    static class ViewHolder{
        TextView textName;
    }

    public static void updateNeighborsList(Neighbor neighbor){
        NeighborSectionFragment.updateAddedFriend(neighbor);
    }
    public static void addToNeighbors(Neighbor neighbor){
        NeighborSectionFragment.updateDeletedFriend(neighbor);
    }


}