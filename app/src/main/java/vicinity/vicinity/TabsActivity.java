package vicinity.vicinity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import java.sql.SQLException;

import vicinity.ConnectionManager.ConnectAndDiscoverService;
import vicinity.ConnectionManager.UDPpacketListner;
import vicinity.Controller.MainController;
import vicinity.model.Globals;
import vicinity.model.Neighbor;

/**
 * Applies the Theme.Holo.Light to get the ActionBar
 * and implements the ActionBar.TabListener to get tabs events.
 * Uses a ViewPager to swipe between fragments using the tabbed ActionBar.
 */
public class TabsActivity extends FragmentActivity implements ActionBar.TabListener {

    private final String TAG = "TabsActivity";

    // Allows swiping left and right to switch between pages
    private ViewPager mViewPager;
    // The Activity's context
    public static Context ctx;
    public static MainController controller;
    private static Fragment timeline = new TimelineSectionFragment()
            , neighbors = new NeighborSectionFragment(), chat = new MessagesSectionFragment(),
            settings= new SettingsSectionFragment();



            /*----------Overridden Methods------------*/

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        ctx = TabsActivity.this;
        controller = new MainController(ctx);

        try {
            if (!Globals.isNewUser)
                Toast.makeText(TabsActivity.ctx, "Welcome back " + controller.retrieveCurrentUsername(), Toast.LENGTH_LONG).show();
            Toast.makeText(TabsActivity.ctx, "To save battery, make sure you turn off Wi-Fi Direct after you finish using Vicinity", Toast.LENGTH_LONG).show();

        }
        catch(SQLException e){
            e.printStackTrace();
        }

        //Starting the services
        startService(new Intent(this, ConnectAndDiscoverService.class));
        startService(new Intent(this, UDPpacketListner.class));
        final LocalBroadcastManager replyToRequest = LocalBroadcastManager.getInstance(this);


        //BroadcastReceiver to receive friends requests
        final BroadcastReceiver requestsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                final Bundle bundle = intent.getExtras();
                final Neighbor receivedRequest = (Neighbor) bundle.getSerializable("NEW_REQUEST");
                Log.i(TAG, "Received a new request: " + receivedRequest.toString());
                final Intent intent1 = new Intent("REPLY");
                //Check if the request came from a blocked user
                if(MainController.isThisIPMuted(receivedRequest.getIpAddress()))
                {
                    intent1.putExtra("REPLY_REQUEST",false);
                    replyToRequest.sendBroadcast(intent1);
                }
                else if(controller.isThisMyFriend(receivedRequest.getDeviceAddress())){
                    Toast.makeText(TabsActivity.ctx,"You are not friends with "+receivedRequest.getInstanceName()+" anymore!",Toast.LENGTH_LONG).show();
                    controller.deleteMessages(receivedRequest.getIpAddress().getHostAddress());
                    controller.deleteFriend(receivedRequest.getDeviceAddress());
                    NeighborListAdapter.addToNeighbors(receivedRequest);

                    intent1.putExtra("REPLY_REQUEST",false);
                    replyToRequest.sendBroadcast(intent1);
                }
                //Display a dialog to the user
                else if (!controller.isThisMyFriend(receivedRequest.getDeviceAddress())){
                    new AlertDialog.Builder(ctx)
                            .setTitle("Friend's Request")
                            .setMessage(receivedRequest.getInstanceName() + " wants to add you as a friend")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "YES");

                                    //Send BC to RequestServer
                                    intent1.putExtra("REPLY_REQUEST",true);
                                    replyToRequest.sendBroadcast(intent1);


                                    CharSequence text = receivedRequest.getInstanceName() + " is now your friend!";
                                    int duration = Toast.LENGTH_LONG;
                                    Toast toast = Toast.makeText(ctx, text, duration);
                                    toast.show();
                                    //Add new friend to database
                                    controller.addFriend(receivedRequest);
                                    NeighborListAdapter.updateNeighborsList(receivedRequest);


                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "no");
                                    CharSequence text = "Why!!";
                                    int duration = Toast.LENGTH_LONG;
                                    Toast toast = Toast.makeText(ctx, text, duration);
                                    toast.show();
                                    intent1.putExtra("REPLY_REQUEST",false);
                                    replyToRequest.sendBroadcast(intent1);

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                }}
        };
        LocalBroadcastManager.getInstance(this).registerReceiver((requestsReceiver),
                new IntentFilter("REQUEST")
        );


        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        // Specify that we will be displaying activity_tabs in the action bar.
        setContentView(R.layout.activity_tabs);

        // Initialize adapter
        AppSectionsPagerAdapter mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        mViewPager.setOffscreenPageLimit(4);

        // An Array of each tab's custom layout (icon + text)
        final int[] LAYOUTS = new int[] {
                R.layout.tab_timeline_layout,
                R.layout.tab_neighbor_layout,
                R.layout.tab_chat_layout,
                R.layout.tab_settings_layout
        };


        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with the custom layout specified in the LAYOUTS array.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setCustomView(LAYOUTS[i])
                            .setTabListener(this));

        }


    } //end onCreate


    @Override
    protected void onDestroy(){
        super.onDestroy();
        stopService(new Intent(ctx,ConnectAndDiscoverService.class));
        stopService(new Intent(ctx,UDPpacketListner.class));

    }

        /*----------Implementation of ActionBar.TabListener methods------------*/

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


        /*----------Inner Class------------*/

    /**
     * Adapter that keeps the fragments inside the ViewPager.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        final static String TAG = "adapter";


        /**
         * Public constructor
         * @param fm the fragments manager
         */
        public AppSectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        /**
         * Returns a fragment according to the chosen tab.
         */
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return timeline;

                case 1:
                    return neighbors;

                case 2:
                    return chat;

                case 3:
                    return settings;

                default:
                    // The other sections of the app are dummy placeholders.
                    return timeline;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

    }






}