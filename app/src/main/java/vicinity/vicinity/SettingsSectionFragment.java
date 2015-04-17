package vicinity.vicinity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;
import vicinity.model.DBHandler;
import vicinity.model.Globals;


public class SettingsSectionFragment extends Fragment {

    Button deleteAccount, clearChat, clearComments;
    Switch notificationSwitch;
    public final String TAG = "Settings";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        /*
         * Delete account:
         * Displays an alert dialog
         * if user clicked yes, the database will be deleted
         * and the app will be restarted
         */
        deleteAccount = (Button) rootView.findViewById(R.id.deleteAccount);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Delete account clicked. ");
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete your account?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               Log.i(TAG,"YES");
                                DBHandler.deleteDatabase();
                                CharSequence text = "Sad to see you leave the vicinity!";
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(getActivity(), text, duration);
                                toast.show();
                                int secondsDelay = 2;
                                Timer timer = new Timer();
                                timer.schedule(new TimerTask() {

                                    public void run() {
                                                Intent intent = new Intent(getActivity(), LaunchActivity.class);
                                                startActivity(intent);
                                    }


                                }, secondsDelay * 2000);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "no");
                                CharSequence text = "Phew!!";
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(getActivity(), text, duration);
                                toast.show();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        /*
         * Notification switch, on to receive notifications
         * off to turn them off
         */
        notificationSwitch = (Switch) rootView.findViewById(R.id.notificationSwitch);
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){
                   Log.i(TAG,"Switch is on");
                   Globals.Notification=true;
               }
                else{
                   Log.i(TAG,"Switch is off");
                   Globals.Notification=false;

               }
            }
        });
        /*
         * Clear chat button event
         * clears all messages from the database
         */
        clearChat = (Button) rootView.findViewById(R.id.clearHistory);
        clearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clear chat history");
                new AlertDialog.Builder(getActivity())
                        .setTitle("Clear Chat History")
                        .setMessage("Are you sure you want to delete all your chat history?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "YES");
                                DBHandler.deleteDatabase();
                                CharSequence text = "Deleted chat history";
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(getActivity(), text, duration);
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
         * Clear comments button event
         * clears all comments
         */
        clearComments = (Button) rootView.findViewById(R.id.clearComments);
        clearComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clear all comments");
                new AlertDialog.Builder(getActivity())
                        .setTitle("Clear Timeline Comments")
                        .setMessage("Are you sure you want to delete all Timeline comments?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "YES");
                                DBHandler.deleteDatabase();
                                CharSequence text = "Cleared Timeline comments";
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(getActivity(), text, duration);
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
        return rootView;
    }




}
