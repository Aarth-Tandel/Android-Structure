package com.wozart.route_3.rooms;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Constant;
import com.wozart.route_3.R;
import com.wozart.route_3.data.DeviceDbHelper;
import com.wozart.route_3.data.DeviceDbOperations;
import com.wozart.route_3.model.AuraSwitch;
import com.wozart.route_3.model.AwsState;
import com.wozart.route_3.network.TcpClient;
import com.wozart.route_3.utilities.DeviceUtils;
import com.wozart.route_3.utilities.JsonUtils;

import java.net.UnknownHostException;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by wozart on 26/10/17.
 */

public class LoadAdapter extends RecyclerView.Adapter<LoadAdapter.MyViewHolder> {

    private Context mContext;
    private List<Loads> LoadList;

    private TcpClient mTcpClient;
    private Toast mtoast;
    private DeviceUtils mDeviceUtils;
    private boolean flag = true;
    private int count = 0;

    private DeviceDbOperations db = new DeviceDbOperations();
    private SQLiteDatabase mDb;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, device;
        private ImageView thumbnail, overflow;


        private MyViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            device = (TextView) view.findViewById(R.id.tv_state);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail1);
            overflow = (ImageView) view.findViewById(R.id.overflow);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();
                    AuraSwitch mDevice;
                    JsonUtils serialize = new JsonUtils();
                    String data = null;

                    // check if item still exists
                    if (pos != RecyclerView.NO_POSITION) {
                        Loads loads = LoadList.get(pos);
                        mDevice = mDeviceUtils.UpdateSwitchState(loads.getDevice(), loads.getLoadNumber());

                        if (mDeviceUtils.GetIP(loads.getDevice()) != null) {
                            try {
                                data = serialize.Serialize(mDevice);
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                            new ConnectTask(data, mDeviceUtils.GetIP(loads.getDevice())).execute("");

                        } else if (isNetworkAvailable()) {
                            if (mContext instanceof RoomActivity) {
                                ((RoomActivity) mContext).PusblishDataToShadow(mDevice.getThing(), JsonUtils.SerializeDataToAws(mDevice));
                            }
                        } else
                            Toast.makeText(v.getContext(), "Device offline", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    public LoadAdapter(Context mContext, List<Loads> loadsList) {
        this.mContext = mContext;
        this.LoadList = loadsList;
        DeviceDbHelper dbHelper = new DeviceDbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(
                mMessageReceiver, new IntentFilter("intentKey"));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(
                mReceiverAws, new IntentFilter("AwsShadow"));
        mDeviceUtils = new DeviceUtils();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.load_cards2, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        count++;
        Loads loads = LoadList.get(position);
        loads.setPostion(position);

        if (flag) {
            updateLoads(loads);
        }

        holder.title.setText(loads.getName());
        if (loads.getState() == 0)
            holder.device.setText("OFF");
        else
            holder.device.setText("ON");
//        holder.count.setText(loads.getNumOfDevices() + " Devices");
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, holder.title.getText().toString(), position);
            }
        });
        if (count == LoadList.size())
            flag = false;
    }

    private void updateStates(int[] states, String device) {
        for (Loads x : LoadList) {
            if (x.getDevice().equals(device)) {
                x.setState(states[x.getLoadNumber()]);
                notifyItemChanged(x.getPostion(), x);
            }
        }
    }

    private void updateLoads(Loads load) {
        AuraSwitch dummyDevice = mDeviceUtils.GetInfo(load.getDevice());
        load.setIP(dummyDevice.getIP());
        int[] state = dummyDevice.getStates();
        load.setState(state[load.getLoadNumber() % 4]);
    }

    /**
     * To check is there internet connection
     */

    private boolean isNetworkAvailable() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    /**
     * Send data to the Aura Device
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

                if (dummyDevice.getType() == 4) {
                    updateStates(dummyDevice.getStates(), dummyDevice.getName());
                }
            }
        }
    }

    /**
     * Data from the TcpServer
     */

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String data = intent.getStringExtra("key");
            JsonUtils deserialize = new JsonUtils();
            AuraSwitch dummyDevice = deserialize.Deserialize(data);
            updateStates(dummyDevice.getStates(), dummyDevice.getName());
        }
    };

    /**
     * Data from the AwsShadowUpdate
     */

    private BroadcastReceiver mReceiverAws = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String shadow = intent.getStringExtra("data");
            String segments[] = shadow.split("/");
            String device = db.GetDevice(mDb, segments[1]);
            AwsState dummyShadow = JsonUtils.DeserializeAwsData(segments[0]);
            mDeviceUtils.CloudDevices(dummyShadow, segments[1], device);
            updateStates(dummyShadow.getStates(), device);
        }
    };

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, String room, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(room, position));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private String RoomSelected;
        private int Position;

        private MyMenuItemClickListener(String room, int position) {
            RoomSelected = room;
            Position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Loads previousDevice = LoadList.get(Position);
                    editBoxPopUp(previousDevice.getName());
                    return true;

                case R.id.action_play_next:
                    return true;
                default:
            }
            return false;
        }

        private void editBoxPopUp(final String previousDevice) {
            final Boolean[] flag = {true};
            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            final EditText input = new EditText(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alert.setView(input);
            alert.setMessage("Change name of " + previousDevice);
            alert.setTitle("Edit Room");
            alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // what ever you want to do with No option.
                }
            });
            alert.show();
        }
    }

    @Override
    public int getItemCount() {
        return LoadList.size();
    }

}

