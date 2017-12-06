package com.wozart.route_3.favouriteSqlLite;

import android.provider.BaseColumns;

/**
 * Created by wozart on 05/12/17.
 */

public class FavouriteContract {
    public static final class FavouriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favourite";
        public static final String DEVICE_NAME = "device";
        public static final String LOAD_NAME = "load";
        public static final String HOME_NAME = "home";
        public static final String ROOM_NAME = "room";
    }
}
