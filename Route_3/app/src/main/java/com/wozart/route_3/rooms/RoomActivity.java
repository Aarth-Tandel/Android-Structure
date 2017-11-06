package com.wozart.route_3.rooms;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.Constant;
import com.wozart.route_3.R;
import com.wozart.route_3.data.DeviceDbHelper;
import com.wozart.route_3.data.DeviceDbOperations;
import com.wozart.route_3.network.TcpClient;

import java.util.ArrayList;
import java.util.List;

public class RoomActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LoadAdapter adapter;
    private List<Loads> LoadList;
    String RoomSelected;
    String HomeSelected;

    private DeviceDbOperations db = new DeviceDbOperations();
    private SQLiteDatabase mDb;

    private TcpClient mTcpClient;

    private Toast mtoast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Intent intent = getIntent();
        RoomSelected = intent.getStringExtra("room");
        HomeSelected = intent.getStringExtra("home");

        TextView textView = (TextView) findViewById(R.id.tv_message);
        textView.setText(RoomSelected);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        LoadList = new ArrayList<>();
        adapter = new LoadAdapter(this, LoadList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new RoomActivity.GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        ArrayList<String> devices;
        DeviceDbHelper dbHelper = new DeviceDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        devices = db.GetDevicesInRoom(mDb, RoomSelected, HomeSelected);

//        LocalBroadcastManager.getInstance(this).registerReceiver(
//                mMessageReceiver, new IntentFilter("intentKey"));
        prepareLoad(devices);
    }

//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // Get extra data included in the Intent
//            String data = intent.getStringExtra("key");
//        }
//    };

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

            }
        }

    }

    /**
     * Adding few albums for testing
     */
    private void prepareLoad(ArrayList<String> devices) {

        LoadList.clear();
        int[] covers = new int[]{
                R.drawable.album1,
                R.drawable.album2,
                R.drawable.album3,
                R.drawable.album4,
                R.drawable.album5,
                R.drawable.album6,
                R.drawable.album7
        };

        for (String deviceName : devices) {
            int i = 0;
            if (deviceName != null) {
                ArrayList<String> loads;
                loads = db.GetLoads(mDb, deviceName);

                for (String loadName : loads) {
                    Loads a = new Loads(loadName, deviceName, null, covers[2], i);
                    LoadList.add(a);
                    i++;
                }
            }

        }

        adapter.notifyDataSetChanged();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        private GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
