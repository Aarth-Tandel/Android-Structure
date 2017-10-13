package com.wozart.route_3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Constant;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobilehelper.auth.IdentityHandler;
import com.amazonaws.mobilehelper.auth.IdentityManager;
import com.amazonaws.mobilehelper.auth.IdentityProvider;
import com.amazonaws.mobilehelper.auth.user.IdentityProfile;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.wozart.route_3.data.DeviceDbHelper;
import com.wozart.route_3.data.DeviceDbOperations;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String BUNDLE_KEY_TOOLBAR_TITLE = "title";
    private IdentityManager identityManager;

    private Menu HomeMenu;
    private int HOME_ID = 1;
    private int MAX_HOME = 5;
    public static String SelectedHome;

    private DeviceDbOperations db = new DeviceDbOperations();
    private SQLiteDatabase mDb;

    private NavigationView NavigationView;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton AddDevice, AddScenes, AddRooms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView = (NavigationView) findViewById(R.id.nav_view);

        AWSMobileClient.initializeMobileClientIfNecessary(this);
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();

        initializeFab();
        initializeTabs();
        updateUserDetails();

        // Obtain a reference to the identity manager.
        identityManager = awsMobileClient.getIdentityManager();
        UserInfo();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DeviceDbHelper dbHelper = new DeviceDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        db.InsertBasicData(mDb);
    }

    private void updateUserDetails() {

        View headerView = NavigationView.getHeaderView(0);
        TextView drawerUsername = (TextView) headerView.findViewById(R.id.tv_username);
        ImageView drawerImage = (ImageView) headerView.findViewById(R.id.imageView);

        final IdentityManager identityManager =
                AWSMobileClient.defaultMobileClient().getIdentityManager();
        final IdentityProvider identityProvider =
                identityManager.getCurrentIdentityProvider();

        final IdentityProfile identityProfile = identityManager.getIdentityProfile();

        if (identityProfile != null && identityProfile.getUserName() != null) {
            drawerUsername.setText(identityProfile.getUserName());
            drawerImage.setImageBitmap(identityProfile.getUserImage());
        }
    }

    private void UserInfo() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final IdentityManager identityManager = AWSMobileClient.defaultMobileClient().getIdentityManager();

                Log.d(LOG_TAG, "fetchUserIdentity");
                AWSMobileClient.defaultMobileClient()
                        .getIdentityManager()
                        .getUserID(new IdentityHandler() {

                            @Override
                            public void onIdentityId(String identityId) {
                                Constant.IDENTITY_ID = identityId;
                                if (identityManager.isUserSignedIn()) {
                                    final IdentityProfile identityProfile = identityManager.getIdentityProfile();

                                    if (identityProfile != null) {
//                                        getUserData(identityId);
                                    }
                                }
                            }

                            @Override
                            public void handleError(Exception exception) {
                                Log.e(LOG_TAG, " " + exception);
                            }
                        });

            }
        };
        Thread saveUserId = new Thread(runnable);
        saveUserId.start();
    }

    private void initializeTabs() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Favourites"));
        tabLayout.addTab(tabLayout.newTab().setText("Rooms"));
        tabLayout.addTab(tabLayout.newTab().setText("Scenes"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(1);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initializeFab() {
        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        AddDevice = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        AddScenes = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
        AddRooms = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);

        AddDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addRooms();

            }
        });
        AddScenes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked

            }
        });
        AddRooms.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu third item clicked

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.HomeMenu = menu;
        for (String home : db.GetAllHome(mDb))
            createMenuItem(home);
        onOptionsItemSelected(menu.findItem(R.id.home));
        return true;
    }

    private void createMenuItem(String name) {
        if (!name.equals("Home")) {
            HomeMenu.add(R.id.gp_home, HOME_ID, Menu.NONE, name);
            HomeMenu.setGroupCheckable(R.id.gp_home, true, true);
            HOME_ID++;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // int id = item.getItemId();
        int count = HomeMenu.size();

        switch (item.getItemId()) {
            case R.id.add_home:
                if (count <= MAX_HOME) {
                    addHomeDialog();
                } else {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Only five Home can be added :(", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                return true;

            case R.id.home:
                item.setChecked(true);
                SelectedHome = item.getTitle().toString();
                getFragmentRefreshListener().onRefresh();
                return true;

            case 1:
                item.setChecked(true);
                SelectedHome = item.getTitle().toString();
                getFragmentRefreshListener().onRefresh();
                return true;

            case 2:
                item.setChecked(true);
                SelectedHome = item.getTitle().toString();
                getFragmentRefreshListener().onRefresh();
                return true;

            case 3:
                item.setChecked(true);
                SelectedHome = item.getTitle().toString();
                getFragmentRefreshListener().onRefresh();
                return true;

            case 4:
                item.setChecked(true);
                SelectedHome = item.getTitle().toString();
                getFragmentRefreshListener().onRefresh();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addRooms() {
        final Boolean[] flag = {true};
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alert.setView(input);
        alert.setMessage("Adding new room to " + SelectedHome);
        alert.setTitle("Rooms");
        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                for (String x : db.GetRooms(mDb, SelectedHome)) {
                    if (input.getText().toString().equals(x)) {
                        flag[0] = false;
                    }
                }
                if (flag[0]) {
                    db.InsertRoom(mDb, SelectedHome, input.getText().toString().trim());
                    getFragmentRefreshListener().onRefresh();
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Room added", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Room with same name already exists", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!AWSMobileClient.defaultMobileClient().getIdentityManager().isUserSignedIn()) {
            // In the case that the activity is restarted by the OS after the application
            // is killed we must redirect to the splash activity to handle the sign-in flow.
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return;
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        // Save the title so it will be restored properly to match the view loaded when rotation
        // was changed or in case the activity was destroyed.
    }

    private void addHomeDialog() {
        final Boolean[] flag = {true};
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alert.setView(input);
        alert.setMessage("Add a new home");
        alert.setTitle("Homes");
        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                for (String x : db.GetAllHome(mDb)) {
                    if (input.getText().toString().equals(x)) {
                        flag[0] = false;
                    }
                }
                if (flag[0]) {
                    createMenuItem(input.getText().toString());
                    db.InsertHome(mDb, input.getText().toString());
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Home added", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Room with same name already exists", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });
        alert.show();
    }

    public String GetSelectedHome() {
        return SelectedHome;
    }

    public void UpdateFragment() {
        onOptionsItemSelected(HomeMenu.findItem(R.id.home));
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    private FragmentRefreshListener fragmentRefreshListener;

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public interface FragmentRefreshListener {
        void onRefresh();
    }
}
