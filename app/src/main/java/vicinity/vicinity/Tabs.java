package vicinity.vicinity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;

import java.sql.SQLException;

import vicinity.ConnectionManager.ConnectAndDiscoverService;
import vicinity.Controller.MainController;
import vicinity.exceptionHandler.UnhandledExceptionHandler;

/**
 * Implements the ActionBar to create a tabbed view.
 */
public class Tabs extends FragmentActivity implements ActionBar.TabListener {

    private final String TAG ="Tabs";
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;
    static NeighborSectionFragment neighborFragment;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /****TEST****/
        try{
        MainController c = new MainController(Tabs.this);
        Log.i(TAG,"There is a user in the database: "+ c.retrieveCurrentUsername());
        c.isThisMyFriend("12312312");
        c.isThisMyFriend("12302312");}
        catch(SQLException e){
            e.printStackTrace();
        }
        //Handling unhandled exception
        Thread.setDefaultUncaughtExceptionHandler(new UnhandledExceptionHandler(this));
        //Starting the service
        startService(new Intent(this, ConnectAndDiscoverService.class));

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

        //Tabs layout (icons + text)
        final int[] LAYOUTS = new int[] {
                R.layout.tab_timeline_layout,
                R.layout.tab_neighbor_layout,
                R.layout.tab_chat_layout,
                R.layout.tab_request_layout
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
    protected void onStop() {
        Log.i(TAG,"Tabs are stopped");
        super.onStop();



    }
    @Override
    protected void onDestroy(){
        Log.i(TAG,"Tabs are destroyed");
        //Destroying ConnectAndDiscover service
        //This means the service is only stopped when the user shuts down the app completely
        stopService(new Intent(this, ConnectAndDiscoverService.class));
        super.onDestroy();

    }
    /************************************************/


    /********************Tabs Methods****************************/
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
                    return new TimelineSectionFragment();

                case 1:
                    return new NeighborSectionFragment();

                case 2:
                    return new MessagesSectionFragment();

                case 3:
                    return new RequestsSectionFragment();

                default:
                    // The other sections of the app are dummy placeholders.
                    return new TimelineSectionFragment();
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