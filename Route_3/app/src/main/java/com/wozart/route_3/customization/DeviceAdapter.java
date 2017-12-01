package com.wozart.route_3.customization;

/**
 * Created by wozart on 29/11/17.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.wozart.route_3.R;
import com.wozart.route_3.data.DeviceDbHelper;
import com.wozart.route_3.data.DeviceDbOperations;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.MyViewHolder> {

    private Context mContext;
    private List<Device> DeviceList;

    private DeviceDbOperations db = new DeviceDbOperations();
    private SQLiteDatabase mDb;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDevice, textViewHome, textViewRoom;
        public ImageView thumbnail1, overflow;
        public Switch AwsConnectSwitch;

        public MyViewHolder(View view) {
            super(view);

            textViewDevice = (TextView) view.findViewById(R.id.tv_device);
            textViewHome = (TextView) view.findViewById(R.id.tv_home);
            textViewRoom = (TextView) view.findViewById(R.id.tv_room);
            thumbnail1 = (ImageView) view.findViewById(R.id.thumbnail1);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            AwsConnectSwitch = (Switch) view.findViewById(R.id.switch_aws_connect);
        }
    }


    public DeviceAdapter(Context mContext, List<Device> roomsList) {
        this.mContext = mContext;
        this.DeviceList = roomsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customization_cards, parent, false);

        DeviceDbHelper dbHelper = new DeviceDbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        db.InsertBasicData(mDb);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Device device = DeviceList.get(position);

        holder.textViewDevice.setText(device.getDevice());
        holder.textViewHome.setText(device.getHome());
        holder.textViewRoom.setText(device.getRoom());
        // loading rooms cover using Glide library
//        Glide.with(mContext).load(rooms.getThumbnail()).into(holder.thumbnail1);
//        Glide.with(mContext).load(rooms.getThumbnail()).into(holder.thumbnail2);
//        Glide.with(mContext).load(rooms.getThumbnail()).into(holder.thumbnail3);
//        Glide.with(mContext).load(rooms.getThumbnail()).into(holder.thumbnail4);

        if (device.getThing() != null)
            holder.AwsConnectSwitch.setChecked(true);
        else
            holder.AwsConnectSwitch.setChecked(false);

        holder.AwsConnectSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, device.getDevice() + " Selected", Toast.LENGTH_SHORT).show();
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, holder.textViewDevice.getText().toString(), position);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, String device, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(device, position));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private String DeviceSelected;
        private int Position;

        private MyMenuItemClickListener(String device, int position) {
            DeviceSelected = device;
            Position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    editBoxPopUp(DeviceSelected);
                    return true;
                default:
            }
            return false;
        }


        private void updateCard(String roomName) {
//            roomsList.set(Position, device);
            notifyItemChanged(Position);
        }

        private void editBoxPopUp(final String deviceSelected) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle("Customization Device");
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.customization_edit_dialog, null);
            dialog.setView(dialogView);

            final Spinner homeSpinner = (Spinner) dialogView.findViewById(R.id.spinner_home);
            ArrayList<String> homes = db.GetAllHome(mDb);
            ArrayAdapter<String> adapterHome = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, homes);
            homeSpinner.setAdapter(adapterHome);

            final Spinner roomSpinner = (Spinner) dialogView.findViewById(R.id.spinner_room);
            homeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    ArrayList<String> rooms = db.GetRooms(mDb, homeSpinner.getSelectedItem().toString());
                    ArrayAdapter<String> adapterRoom = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, rooms);
                    roomSpinner.setAdapter(adapterRoom);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (homeSpinner.getSelectedItem().toString() != null && roomSpinner.getSelectedItem().toString() != null) {
                        db.UpdateRoomAndHome(mDb, homeSpinner.getSelectedItem().toString(), roomSpinner.getSelectedItem().toString(), deviceSelected);
                    } else {
                        Toast.makeText(mContext, "Please make appropriate selection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
        }
    }

    @Override
    public int getItemCount() {
        return DeviceList.size();
    }
}

