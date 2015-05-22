package vicinity.vicinity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.widget.Toast;


import java.sql.SQLException;
import java.util.ArrayList;

import vicinity.ConnectionManager.RequestsManager;
import vicinity.Controller.MainController;
import vicinity.model.Neighbor;


public class FriendListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    ArrayList<Neighbor> services;
    private ImageButton deleteFriend, editFriendName;
    private MainController controller;

    private static final String TAG ="FriendsListAdpt";
    public FriendListAdapter(Context context, ArrayList<Neighbor> services){
        this.services = services;
        mInflater = LayoutInflater.from(context);
        controller = new MainController(context);

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
            convertView = mInflater.inflate(R.layout.friend_row_view, null);
            holder = new ViewHolder();
            holder.textName = (TextView) convertView.findViewById(R.id.friend_row);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textName.setText(services.get(position).getInstanceName());

        /*
        * Delete Friend button
         */
        deleteFriend= (ImageButton)convertView.findViewById(R.id.deleteButton);
        deleteFriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final Neighbor deleteFriend = (Neighbor)getItem(position);
                Log.i("FriendsListAdpt","Clicked delete for: "+deleteFriend.getInstanceName());
                new AlertDialog.Builder(TabsActivity.ctx)
                        .setTitle("Delete Friend")
                        .setMessage("Are you sure you want to delete "+deleteFriend.getInstanceName()+" from your friends list?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "YES");

                                new RequestsManager().execute(deleteFriend);

                                NeighborSectionFragment.updateDeletedFriend(deleteFriend);
                                // I added this to delete messages when friend is deleted - Ruba
                                controller.deleteMessages(deleteFriend.getIpAddress().getHostAddress());
                                CharSequence text = deleteFriend.getInstanceName()+" is deleted.";
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(TabsActivity.ctx, text, duration);
                                toast.show();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "no");

                            }
                        })//
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        /*
         * Edit friend's alias name
         */
        editFriendName = (ImageButton)convertView.findViewById(R.id.changeName);
        editFriendName.setOnClickListener(new View.OnClickListener() {
            final EditText input = new EditText(TabsActivity.ctx);
            @Override
            public void onClick(View arg0) {
                final Neighbor edit = (Neighbor)getItem(position);
                Log.i("FriendsListAdpt","Clicked changeName for: "+edit.getInstanceName());
                new AlertDialog.Builder(TabsActivity.ctx)
                        .setTitle("Edit Name")
                        .setMessage("Choose a new name for "+edit.getInstanceName())
                        .setView(input)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Editable value = input.getText();
                                Log.i(TAG, "New name: " + value);
                                try {
                                    if (TabsActivity.controller.changeName(value.toString(), edit.getDeviceAddress())) {
                                        CharSequence text = edit.getInstanceName() + " is now called" + value + "!";
                                        int duration = Toast.LENGTH_LONG;
                                        Toast toast = Toast.makeText(TabsActivity.ctx, text, duration);
                                        toast.show();
                                        ((Neighbor) getItem(position)).setAliasName(value.toString());
                                        ((Neighbor) getItem(position)).setInstanceName(value.toString());


                                    }
                                } catch (SQLException e) {

                                }

                                if (input.getParent() != null)
                                    ((ViewGroup) input.getParent()).removeView(input);


                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (input.getParent() != null)
                            ((ViewGroup) input.getParent()).removeView(input);
                    }
                }).show();

            }
        });
        this.notifyDataSetChanged();

        return convertView;

    }

    static class ViewHolder{
        TextView textName;
    }
}