package com.wozart.route_3;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.Constant;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.AWSConfiguration;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobilehelper.auth.IdentityManager;
import com.amazonaws.mobilehelper.auth.IdentityProvider;
import com.amazonaws.mobilehelper.auth.user.IdentityProfile;
import com.amazonaws.models.nosql.UsersDO;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.wozart.route_3.data.DeviceDbHelper;
import com.wozart.route_3.data.DeviceDbOperations;
import com.wozart.route_3.model.AuraSwitch;
import com.wozart.route_3.network.AwsPubSub;
import com.wozart.route_3.network.NsdClient;
import com.wozart.route_3.network.TcpClient;
import com.wozart.route_3.network.TcpServer;
import com.wozart.route_3.noSql.SqlOperationUserTable;
import com.wozart.route_3.utilities.DeviceUtils;
import com.wozart.route_3.utilities.Encryption;
import com.wozart.route_3.utilities.JsonUtils;

import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private IdentityManager identityManager;
    public static String NETWORK_SSID = "Aura";
    public static String URL = "http://192.168.10.1/";
    private String IP;

    private Menu HomeMenu;
    private int HOME_ID = 1;
    private int MAX_HOME = 5;
    public static String SelectedHome;
    private static String AddNewDeviceTo = null;

    private DeviceDbOperations db = new DeviceDbOperations();
    private SQLiteDatabase mDb;
    private UsersDO UserData = new UsersDO();

    private TcpClient mTcpClient;
    private NsdClient Nsd;
    private DeviceUtils mDeviceUtils;
    private AwsPubSub awsPubSub;
    private AWSCredentials awsCredentials;
    private CognitoCachingCredentialsProvider credentialsProvider;
    boolean mBounded;

    private Toast mtoast;
    private CoordinatorLayout coordinatorLayout;

    private NavigationView NavigationView;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton AddDevice, ConfigureDevice, AddRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView = (NavigationView) findViewById(R.id.nav_view);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.mCordinateLayout);

        AWSMobileClient.initializeMobileClientIfNecessary(this);
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();

        // Obtain a reference to the identity manager.
        identityManager = awsMobileClient.getIdentityManager();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        startService(new Intent(this, TcpServer.class));
        startService(new Intent(this, AwsPubSub.class));
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(
                mMessageReceiver, new IntentFilter("AwsShadow"));

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // context
                AWSConfiguration.AMAZON_COGNITO_IDENTITY_POOL_ID,// Identity Pool ID
                AWSConfiguration.AMAZON_COGNITO_REGION // Region
        );

        new Thread(new Runnable() {
            @Override
            public void run() {
                awsCredentials = credentialsProvider.getCredentials();
                GetUserData();
            }
        }).start();
        initializeFab();
        initializeTabs();
        updateUserDetails();
        convertIP();
        initializeDiscovery();
    }

    private void initializeDiscovery() {
        DeviceDbHelper dbHelper = new DeviceDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        db.InsertBasicData(mDb);
        mDeviceUtils = new DeviceUtils();

        Nsd = new NsdClient(this);
        Nsd.initializeNsd();
        nsdDiscovery();
    }

    public void nsdDiscovery() {
        Nsd.discoverServices();
        final Handler NsdDiscoveryHandler = new Handler();
        NsdDiscoveryHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (final NsdServiceInfo service : Nsd.GetServiceInfo()) {
                    JsonUtils mJsonUtils = new JsonUtils();
                    String data = null;
                    try {
                        data = mJsonUtils.InitialData(IP);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }

                    new ConnectTask(data, service.getHost().getHostAddress()).execute("");
                    Log.d(LOG_TAG, "Initial data: " + data + " to " + service.getServiceName());
                }
            }
        }, 1000);

        NsdDiscoveryHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Nsd.stopDiscovery();
            }
        }, 1000);
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

    public void GetUserData() {
        final SqlOperationUserTable user = new SqlOperationUserTable();
        Runnable runnable = new Runnable() {
            public void run() {
                UserData = user.GetData(credentialsProvider.getIdentityId());
                if (UserData == null)
                    return;
                db.ThingFromAws(mDb, UserData);
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
        AddDevice = (FloatingActionButton) findViewById(R.id.add_device);
        ConfigureDevice = (FloatingActionButton) findViewById(R.id.configure_device);
        AddRooms = (FloatingActionButton) findViewById(R.id.add_room);

        AddRooms.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addRooms();
            }
        });
        AddDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
        ConfigureDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openWebPage(URL);
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
        for (String home : db.GetAllHome(mDb)) {
            if (home != null)
                createMenuItem(home);
        }
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
                    Snackbar.make(materialDesignFAM, "Only five Home can be added :(", Snackbar.LENGTH_LONG)
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

    private class ConfigureListener implements View.OnClickListener {

        private AuraSwitch deviceToPair;

        private ConfigureListener(AuraSwitch device) {
            deviceToPair = device;
        }

        @Override
        public void onClick(View v) {
            addDeviceToWhatRoomPopUp(deviceToPair);
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
        alert.setTitle("Loads");
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
                    Snackbar.make(materialDesignFAM, "Room added", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    Snackbar.make(materialDesignFAM, "Room with same name already exists", Snackbar.LENGTH_LONG)
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

    private void addDeviceToWhatRoomPopUp(final AuraSwitch deviceToPair) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Select room to add the device");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_device_to_home_dialog, null);
        dialog.setView(dialogView);
        ArrayList<String> rooms = db.GetRooms(mDb, SelectedHome);
        final RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.radio_group);

        for (String x : rooms) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(x);
            radioGroup.addView(radioButton);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                for (int i = 0; i < rg.getChildCount(); i++) {
                    RadioButton btn = (RadioButton) rg.getChildAt(i);
                    if (btn.getId() == checkedId) {
                        AddNewDeviceTo = btn.getText().toString();
                        // do something with text
                        return;
                    }
                }
            }
        });

        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!(AddNewDeviceTo == null)) {
                    pairingPopUp(deviceToPair);
                }
            }
        });
        dialog.show();
    }

    private void pairingPopUp(final AuraSwitch deviceToPair) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alert.setView(input);
        alert.setMessage("Enter pairing pin of  " + deviceToPair.getName());
        alert.setTitle("Pin");
        alert.setPositiveButton("Pair", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                if (input.length() == 8) {
                    try {
                        String encryptedPin = Encryption.SHA256(input.getText().toString());
                        String Mac = Encryption.MAC(MainActivity.this);
                        String data = JsonUtils.PairingData(Mac, encryptedPin);
                        new ConnectTask(data, deviceToPair.getIP()).execute("");
                    } catch (NoSuchAlgorithmException e) {
                        Log.e(LOG_TAG, "Failed Pairing: " + e);
                    }
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

    private void convertIP() {
        WifiManager mWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifi.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        IP = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
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

    private void openWebPage(String url) {

        WifiConfiguration mWifiConfig = new WifiConfiguration();
        mWifiConfig.SSID = "\"" + NETWORK_SSID + "\"";
        mWifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        WifiManager mWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifi.addNetwork(mWifiConfig);

        WifiInfo mWifiInfo = mWifi.getConnectionInfo();
        mWifiInfo.getSSID();
        if (mWifiInfo.getSSID().contains(NETWORK_SSID)) {
            Uri webpage = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            Toast.makeText(this, "Please Connect to Aura Device", Toast.LENGTH_LONG).show();
        }
    }

    public String GetSelectedHome() {
        return SelectedHome;
    }

    /**
     * AWS IoT Subscribe to shadow
     */

    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, AwsPubSub.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    }

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            awsPubSub = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            AwsPubSub.LocalAwsBinder mLocalBinder = (AwsPubSub.LocalAwsBinder) service;
            awsPubSub = mLocalBinder.getServerInstance();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String shadow = intent.getStringExtra("data");
            String segments[] = shadow.split("/");
            if (shadow.equals("Connected")) {
                ArrayList<String> things = db.GetThingName(mDb);
                for (String x : things) {
                    awsPubSub.AwsGet(x);
                    awsPubSub.AwsGetPublish(x);
                    awsPubSub.AwsSubscribe(x);
                }
            } else{
                String device = db.GetDevice(mDb, segments[1]);
                mDeviceUtils.CloudDevices(JsonUtils.DeserializeAwsData(segments[0]),segments[1], device);
            }
        }
    };


    /**
     * Send data to the device TCP
     */

    private class ConnectTask extends AsyncTask<String, String, TcpClient> {

        private String data = null;
        private String ip = null;

        private ConnectTask(String message, String address) {
            super();
            data = message;
            ip = address;
        }

        @Override
        protected TcpClient doInBackground(String... message) {

            //we create a TCPClient object and
            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    publishProgress(message);
                }
            });
            mTcpClient.run(data, ip);

            return null;
        }

        protected void onProgressUpdate(String... message) {
            if (message[0].equals(Constant.SERVER_NOT_REACHABLE)) {
                if (mtoast != null)
                    mtoast = null;
                Context context = getApplicationContext();
                CharSequence text = "Device Offline";
                int duration = Toast.LENGTH_SHORT;

                mtoast = Toast.makeText(context, text, duration);
                mtoast.show();
            } else {
                JsonUtils mJsonUtils = new JsonUtils();
                AuraSwitch dummyDevice = mJsonUtils.DeserializeTcp(message[0]);

                if (dummyDevice.getType() == 1 && dummyDevice.getCode().equals(Constant.UNPAIRED)) {
                    dummyDevice.setIP(Nsd.GetIP(dummyDevice.getName()));
                    Snackbar.make(findViewById(R.id.mCordinateLayout), "New device " + dummyDevice.getName(), Snackbar.LENGTH_INDEFINITE)
                            .setAction("ADD", new ConfigureListener(dummyDevice)).show();
                }

                if (dummyDevice.getCode().equals(Encryption.MAC(MainActivity.this)) && dummyDevice.getType() == 2) {
                    if (mtoast != null)
                        mtoast = null;
                    Context context = getApplicationContext();
                    CharSequence text = "Device Paired Successfully";
                    int duration = Toast.LENGTH_SHORT;
                    mtoast = Toast.makeText(context, text, duration);
                    mtoast.show();
                    db.AddDevice(mDb, AddNewDeviceTo, SelectedHome, dummyDevice.getName());
                }

                if (dummyDevice.getType() == 1 && dummyDevice.getCode().equals(Encryption.MAC(MainActivity.this))) {
                    for (NsdServiceInfo x : Nsd.GetAllServices()) {
                        //Find the match in services found and data received
                        if (x.getServiceName().contains(dummyDevice.getName())) {
                            mDeviceUtils.RegisterDevice(dummyDevice, x.getHost().getHostAddress());
                        }
                    }
                }
            }
        }

    }

    /**
     * Updating favourite fragment from main
     */

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
