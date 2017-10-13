package com.wozart.route_3;

/**
 * Created by wozart on 28/09/17.
 */


import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wozart.route_3.data.DeviceDbHelper;
import com.wozart.route_3.data.DeviceDbOperations;

import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    private Context mContext;
    private List<Album> albumList;
    MainActivity activity = new MainActivity();

    private DeviceDbOperations db = new DeviceDbOperations();
    private SQLiteDatabase mDb;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail1, thumbnail2, thumbnail3, thumbnail4, overflow;

        public MyViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail1 = (ImageView) view.findViewById(R.id.thumbnail1);
            thumbnail2 = (ImageView) view.findViewById(R.id.thumbnail2);
            thumbnail3 = (ImageView) view.findViewById(R.id.thumbnail3);
            thumbnail4 = (ImageView) view.findViewById(R.id.thumbnail4);
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


    public AlbumsAdapter(Context mContext, List<Album> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        DeviceDbHelper dbHelper = new DeviceDbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        db.InsertBasicData(mDb);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Album album = albumList.get(position);
        holder.title.setText(album.getName());
        holder.count.setText(album.getNumOfSongs() + " Devices");

        // loading album cover using Glide library
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail1);
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail2);
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail3);
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail4);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, holder.title.getText().toString(), position);
            }
        });
    }

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
                    Toast.makeText(mContext, "Edit", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.action_play_next:
                    Toast.makeText(mContext, "Remove", Toast.LENGTH_SHORT).show();
                    deleteItem(Position);
                    db.DeleteRoom(mDb, activity.GetSelectedHome(), RoomSelected);
                    return true;
                default:
            }
            return false;
        }

        private void deleteItem(int position){
            albumList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, albumList.size());
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
