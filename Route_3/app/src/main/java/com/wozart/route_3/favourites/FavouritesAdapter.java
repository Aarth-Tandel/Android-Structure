package com.wozart.route_3.favourites;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.wozart.route_3.MainActivity;
import com.wozart.route_3.R;
import com.wozart.route_3.RoomActivity;

import java.util.List;

/**
 * Created by wozart on 24/10/17.
 */



public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.MyViewHolder> {

    private Context mContext;
    private List<Favourites> roomsList;
    MainActivity activity = new MainActivity();

//    private DeviceDbOperations db = new DeviceDbOperations();
//    private SQLiteDatabase mDb;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail1, overflow;

        public MyViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail1 = (ImageView) view.findViewById(R.id.thumbnail1);
            overflow = (ImageView) view.findViewById(R.id.overflow);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, RoomActivity.class);
                    intent.putExtra("room", title.getText());
                    mContext.startActivity(intent);
                }
            });
        }
    }


    public FavouritesAdapter(Context mContext, List<Favourites> roomsList) {
        this.mContext = mContext;
        this.roomsList = roomsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

      //  DeviceDbHelper dbHelper = new DeviceDbHelper(mContext);
//        mDb = dbHelper.getWritableDatabase();
//        db.InsertBasicData(mDb);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,final int position) {
        Favourites rooms = roomsList.get(position);
        holder.title.setText(rooms.getName());
        holder.count.setText(rooms.getNumOfDevices() + " Devices");

        // loading rooms cover using Glide library
        Glide.with(mContext).load(rooms.getThumbnail()).into(holder.thumbnail1);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, holder.title.getText().toString(), position);
            }
        });
    }

//    @Override
//    public void onBindViewHolder(final com.wozart.route_3.AlbumsAdapter.MyViewHolder holder, final int position) {
//        Rooms rooms = roomsList.get(position);
//        holder.title.setText(rooms.getName());
//        holder.count.setText(rooms.getNumOfDevices() + " Devices");
//
//        // loading rooms cover using Glide library
//        Glide.with(mContext).load(rooms.getThumbnail()).into(holder.thumbnail1);
//        Glide.with(mContext).load(rooms.getThumbnail()).into(holder.thumbnail2);
//        Glide.with(mContext).load(rooms.getThumbnail()).into(holder.thumbnail3);
//        Glide.with(mContext).load(rooms.getThumbnail()).into(holder.thumbnail4);
//
//        holder.overflow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showPopupMenu(holder.overflow, holder.title.getText().toString(), position);
//            }
//        });
//    }

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
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

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
                    Favourites previousDevice = roomsList.get(Position);
                    editBoxPopUp(previousDevice.getName());
                    return true;

                case R.id.action_play_next:
                    deleteItem(Position);
//                    db.DeleteRoom(mDb, activity.GetSelectedHome(), RoomSelected);
                    return true;
                default:
            }
            return false;
        }

        private void deleteItem(int position) {
            roomsList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, roomsList.size());
        }

        private void updateCard(String roomName) {
            Favourites previousDevice = roomsList.get(Position);
            Favourites device = new Favourites(roomName, previousDevice.getNumOfDevices(), previousDevice.getThumbnail());
            roomsList.set(Position, device);
            notifyItemChanged(Position);
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
//                    for (String x : db.GetRooms(mDb, activity.GetSelectedHome())) {
//                        if (input.getText().toString().equals(x)) {
//                            flag[0] = false;
//                        }
//                    }
//                    if (flag[0]) {
//                        db.UpdateRoom(mDb, activity.GetSelectedHome(), previousDevice, input.getText().toString().trim());
//                        Toast.makeText(mContext, "Room name edited", Toast.LENGTH_SHORT).show();
//                        updateCard(input.getText().toString().trim());
//                    } else {
//                        Toast.makeText(mContext, "Edit failed", Toast.LENGTH_SHORT).show();
//                    }
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
        return roomsList.size();
    }
}
