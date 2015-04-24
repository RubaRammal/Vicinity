package vicinity.vicinity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.widget.Toast;


import java.sql.SQLException;
import java.util.ArrayList;

import vicinity.ConnectionManager.WiFiP2pService;
import vicinity.model.DBHandler;
import vicinity.model.Friend;


public class FriendListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    ArrayList<WiFiP2pService> services;
    private ImageButton deleteFriend, editFriendName;

    private static final String TAG ="FriendsListAdpt";
    public FriendListAdapter(Context context, ArrayList<WiFiP2pService> services){
        this.services = services;
        mInflater = LayoutInflater.from(context);

    }

    public void setServices(ArrayList<WiFiP2pService> s){
        for (int i = 0; i < s.size(); i++) {
            services.add(s.get(i));
        }
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
                final WiFiP2pService deleteFriend = (WiFiP2pService)getItem(position);
                Log.i("FriendsListAdpt","Clicked delete for: "+deleteFriend.getInstanceName());
                new AlertDialog.Builder(TabsActivity.ctx)
                        .setTitle("Delete Friend")
                        .setMessage("Are you sure you want to delete "+deleteFriend.getInstanceName()+" from your friends list?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "YES");
                                TabsActivity.controller.deleteFriend(deleteFriend.getDeviceAddress());
                                NeighborSectionFragment.updateDeletedFriend(deleteFriend);
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
                final WiFiP2pService edit = (WiFiP2pService)getItem(position);
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
                                        CharSequence text = edit.getInstanceName() + " is now " + value + "!";
                                        int duration = Toast.LENGTH_LONG;
                                        Toast toast = Toast.makeText(TabsActivity.ctx, text, duration);
                                        toast.show();
                                        ((WiFiP2pService) getItem(position)).setAliasName(value.toString());
                                        ((WiFiP2pService) getItem(position)).setInstanceName(value.toString());


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