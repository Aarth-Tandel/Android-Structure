package com.wozart.route_3.fragment;

/**
 * Created by wozart on 05/10/17.
 */

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wozart.route_3.R;
import com.wozart.route_3.favourites.FavouritesAdapter;
import com.wozart.route_3.favourites.Favourites;

import java.util.ArrayList;
import java.util.List;

public class FavTab extends Fragment {

    private RecyclerView FAV_recyclerView;
    private FavouritesAdapter FAV_adapter;
    private List<Favourites> FAV_roomsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.favourite_fragment, container, false);

        FAV_recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        FAV_roomsList = new ArrayList<>();
        FAV_adapter = new FavouritesAdapter(getActivity(), FAV_roomsList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        FAV_recyclerView.setLayoutManager(mLayoutManager);
        FAV_recyclerView.addItemDecoration(new FavTab.GridSpacingItemDecoration(2, dpToPx(10), true));
        FAV_recyclerView.setItemAnimator(new DefaultItemAnimator());
        FAV_recyclerView.setAdapter(FAV_adapter);

//        ((MainActivity) getActivity()).setFragmentRefreshListener(new MainActivity.FragmentRefreshListener() {
//            @Override
//            public void onRefresh() {
//                MainActivity activity = new MainActivity();
//                String home = activity.GetSelectedHome();
//                if (home != null) {
//                    ArrayList<String> rooms = db.GetRooms(mDb, home);
//                    prepareRooms(rooms);
//                } else {
//                    ArrayList<String> rooms = db.GetRooms(mDb, "Home");
//                    prepareRooms(rooms);
//                }
//
//            }
//        });
//
//        DeviceDbHelper dbHelper = new DeviceDbHelper(getActivity());
//        mDb = dbHelper.getWritableDatabase();
//
        ArrayList<String> rooms = new ArrayList<>();
        rooms.add("Lamp");
        rooms.add("Fan");
        rooms.add("AC");
        rooms.add("TV");
        prepareRooms(rooms);

        return rootView;
    }

    /**
     * Adding few albums for testing
     */
    private void prepareRooms(ArrayList<String> rooms) {

        FAV_roomsList.clear();
        int[] covers = new int[]{
                R.drawable.album1,
                R.drawable.album2,
                R.drawable.album3,
                R.drawable.album4,
                R.drawable.album5,
                R.drawable.album6,
                R.drawable.album7
        };

        for (String x : rooms) {
            Favourites a = new Favourites(x, 0, covers[2]);
            FAV_roomsList.add(a);
        }

        FAV_adapter.notifyDataSetChanged();
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
