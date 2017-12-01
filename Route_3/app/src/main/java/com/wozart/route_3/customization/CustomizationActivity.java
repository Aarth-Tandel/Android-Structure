package com.wozart.route_3.customization;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.wozart.route_3.R;
import com.wozart.route_3.data.DeviceDbHelper;
import com.wozart.route_3.data.DeviceDbOperations;
import com.wozart.route_3.rooms.Loads;

import java.util.ArrayList;
import java.util.List;

public class CustomizationActivity extends AppCompatActivity {

    private DeviceAdapter adapter;
    private List<Device> DeviceList;

    private DeviceDbOperations db = new DeviceDbOperations();
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customization);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        DeviceList = new ArrayList<>();
        adapter = new DeviceAdapter(this, DeviceList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new CustomizationActivity.GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        List<Device> devices;
        DeviceDbHelper dbHelper = new DeviceDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        devices = db.GetAllDevices(mDb);
        prepareLoad(devices);
    }

    /**
     * Adding few albums for testing
     */
    private void prepareLoad(List<Device> devices) {

        DeviceList.clear();
        int[] covers = new int[]{
                R.drawable.album1,
                R.drawable.album2,
                R.drawable.album3,
                R.drawable.album4,
                R.drawable.album5,
                R.drawable.album6,
                R.drawable.album7
        };

        for (Device device : devices) {
            if (device.getDevice() != null) {
                    Device a = new Device(device.getHome(), device.getRoom(), device.getDevice(), device.getThing());
                    DeviceList.add(a);
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
