package com.example.wozart.structure;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wozart.structure.data.DeviceAdapter;
import com.example.wozart.structure.data.DeviceContract;
import com.example.wozart.structure.data.DeviceDbHelper;
import com.example.wozart.structure.data.DeviceDbOperations;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Menu HomeMenu;
    private TextView screen;
    private int HOME_ID = 1;
    private int MAX_HOME = 5;

    private DeviceDbOperations db = new DeviceDbOperations();

    private SQLiteDatabase mDb;
    private DeviceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        screen = (TextView) findViewById(R.id.tv_devices);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RecyclerView deviceRecyclerView;
        deviceRecyclerView = (RecyclerView) this.findViewById(R.id.all_device_list_view);
        deviceRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        DeviceDbHelper dbHelper = new DeviceDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        db.InsertBasicData(mDb);

//        TestData.insertDummyData(mDb);
//        Cursor cursor = getAllDevice();
//        mAdapter = new DeviceAdapter(this, cursor);
//        deviceRecyclerView.setAdapter(mAdapter);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        int count = HomeMenu.size();

        switch (item.getItemId()){
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
                            db.InsertHome(mDb, input.getText().toString().trim());
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

                return true;

            case 1:
                item.setChecked(true);
                return true;

            case 2:
                item.setChecked(true);
                return true;

            case 3:
                item.setChecked(true);
                return true;

            case 4:
                item.setChecked(true);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createMenuItem(String name) {
        if(!name.equals("Home")) {
            HomeMenu.add(R.id.gp_home, HOME_ID, Menu.NONE, name);
            HomeMenu.setGroupCheckable(R.id.gp_home, true, true);
            HOME_ID++;
        }
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

    private Cursor getAllDevice() {
        return mDb.query(
                DeviceContract.DeviceEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DeviceContract.DeviceEntry.DEVICE_NAME,
                null
        );
    }
}
