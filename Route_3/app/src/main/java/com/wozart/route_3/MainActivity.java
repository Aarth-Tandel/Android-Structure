package com.wozart.route_3;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.wozart.route_3.data.DeviceDbHelper;
import com.wozart.route_3.data.DeviceDbOperations;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Menu HomeMenu;
    private int HOME_ID = 1;
    private int MAX_HOME = 5;
    public static String SelectedHome;

    private DeviceDbOperations db = new DeviceDbOperations();
    private SQLiteDatabase mDb;

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeFab();
        initializeTabs();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DeviceDbHelper dbHelper = new DeviceDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
//        TestData.insertDummyData(mDb);
        db.InsertBasicData(mDb);

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
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
        floatingActionButton3 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked

            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked

            }
        });
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
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
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    final EditText input = new EditText(MainActivity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    alert.setView(input);
                    alert.setMessage("Add a new home");
                    alert.setTitle("Name");
                    alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            createMenuItem(input.getText().toString());
                            db.InsertHome(mDb, input.getText().toString());
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // what ever you want to do with No option.
                        }
                    });
                    alert.show();
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

    public String GetSelectedHome(){
        return SelectedHome;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    private FragmentRefreshListener fragmentRefreshListener;

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public interface FragmentRefreshListener{
        void onRefresh();
    }
}
