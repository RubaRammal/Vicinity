package vicinity.vicinity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import vicinity.ConnectionManager.ConnectAndDiscoverService;
import vicinity.Controller.MainController;

/**
 * Implements the ActionBar to create a tabbed view.
 */
public class TabsActivity extends FragmentActivity implements ActionBar.TabListener {

    private final String TAG ="TabsActivity";
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;
    private ImageButton muteButton;
    public static Context ctx;
    public static MainController controller;


    static NeighborSectionFragment neighborFragment;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ctx=TabsActivity.this;
        controller = new MainController(ctx);
        //Handling unhandled exception
       // Thread.setDefaultUncaughtExceptionHandler(new UnhandledExceptionHandler(this));
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
        muteButton = (ImageButton) findViewById(R.id.muteButton);
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
        Log.i(TAG,"TabsActivity are stopped");
        super.onStop();



    }
    @Override
    protected void onDestroy(){
        Log.i(TAG,"TabsActivity are destroyed");
        //Destroying ConnectAndDiscover service
        //This means the service is only stopped when the user shuts down the app completely
        stopService(new Intent(this, ConnectAndDiscoverService.class));
        super.onDestroy();

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
                    return new SettingsSectionFragment();

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

    /**
     * Mute button
     */
    public void muteUser(View v){
        muteButton.setImageResource(R.drawable.muteicon);

    }


}