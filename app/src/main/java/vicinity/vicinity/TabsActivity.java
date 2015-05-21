package vicinity.vicinity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.content.Context;
import android.widget.Toast;


import java.sql.SQLException;

import vicinity.ConnectionManager.UDPpacketListner;
import vicinity.ConnectionManager.ConnectAndDiscoverService;
import vicinity.Controller.MainController;
import vicinity.model.Globals;
import vicinity.model.Neighbor;

/**
 * Implements the ActionBar to create a tabbed view.
 */
public class TabsActivity extends FragmentActivity implements ActionBar.TabListener {

    private final String TAG ="Tabs";
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;
    ViewGroup v;
    public static Context ctx;
    public static MainController controller;
    private static Fragment timeline = new TimelineSectionFragment()
            , neighbors = new NeighborSectionFragment(), chat = new MessagesSectionFragment(),
            settings= new SettingsSectionFragment();

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        ctx=TabsActivity.this;
        controller = new MainController(ctx);

        try {
            if (Globals.isNewUser==false)
                Toast.makeText(TabsActivity.ctx, "Welcome back " + controller.retrieveCurrentUsername(), Toast.LENGTH_LONG).show();
        }
        catch(SQLException e){
            e.printStackTrace();
        }

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

        //Starting the service
        startService(new Intent(this, ConnectAndDiscoverService.class));
        startService(new Intent(this, UDPpacketListner.class));

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_tabs);


        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
        final ActionBar actionBar = getActionBar();// Set up the action bar.


        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        // Specify that we will be displaying tab_timeline_layout in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        v = (ViewGroup) findViewById(android.R.id.content);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });
        mViewPager.setOffscreenPageLimit(4);

        //TabsActivity layout (icons + text)
        final int[] LAYOUTS = new int[] {
                R.layout.tab_timeline_layout,
                R.layout.tab_neighbor_layout,
                R.layout.tab_chat_layout,
                R.layout.tab_settings_layout
        };


        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()

                            .setCustomView(LAYOUTS[i])
                            .setTabListener(this));

        }


    }//end onCreate


    /********************Overridden Activity methods****************************/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //getSupportFragmentManager().putFragment(outState, "NeighborsFragment", neighbors);
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        Log.i(TAG,"TabsActivity are stopped");
        super.onStop();

    }
    @Override
    protected void onDestroy(){
        Log.i(TAG,"TabsActivity are destroyed");
        super.onDestroy();
        //Destroying ConnectAndDiscover service
        //This means the service is only stopped when the user shuts down the app completely
        stopService(new Intent(this, ConnectAndDiscoverService.class));
        stopService(new Intent(this, UDPpacketListner.class));


    }
    /************************************************/


    /********************TabsActivity Methods****************************/
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


    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        String TAG = "adapter";
        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Returns a fragment according to the chosen tab.
         */
        @Override
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

        @Override
        public CharSequence getPageTitle(int position) {
            return "section "+ (position + 1);
        }


    }






}