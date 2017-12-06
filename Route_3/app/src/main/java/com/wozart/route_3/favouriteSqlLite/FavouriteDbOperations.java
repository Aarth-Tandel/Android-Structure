package com.wozart.route_3.favouriteSqlLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.Constant;
import com.wozart.route_3.favouritesTab.Favourites;

import java.util.ArrayList;

import static com.Constant.CRUD_FAVOURITE;
import static com.wozart.route_3.favouriteSqlLite.FavouriteContract.FavouriteEntry.DEVICE_NAME;
import static com.wozart.route_3.favouriteSqlLite.FavouriteContract.FavouriteEntry.HOME_NAME;
import static com.wozart.route_3.favouriteSqlLite.FavouriteContract.FavouriteEntry.LOAD_NAME;
import static com.wozart.route_3.favouriteSqlLite.FavouriteContract.FavouriteEntry.ROOM_NAME;
import static com.wozart.route_3.favouriteSqlLite.FavouriteContract.FavouriteEntry.TABLE_NAME;

/**
 * Created by wozart on 05/12/17.
 */

public class FavouriteDbOperations {

    public void insertFavourite(SQLiteDatabase db, String device, String load, String home, String room) {
        ContentValues values = new ContentValues();
        values.put(DEVICE_NAME, device);
        values.put(ROOM_NAME, room);
        values.put(LOAD_NAME, load);
        values.put(HOME_NAME, home);
        try {
            db.beginTransaction();
            db.insert(TABLE_NAME, null, values);

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            //Too bad :(
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<Favourites> getFavouriteDevice(SQLiteDatabase db, String home){
        if (home == null)
            home = "Home";
        String[] params = new String[]{home};
        ArrayList<Favourites> favourites = new ArrayList<>();
        Cursor cursor = db.rawQuery(Constant.GET_ALL_FAVOURITE, params);
        while(cursor.moveToNext()){
            Favourites fav = new Favourites();
            fav.setDevice(cursor.getString(1));
            fav.setName(cursor.getString(2));
            fav.setHome(cursor.getString(3));
            fav.setRoom(cursor.getString(4));
            favourites.add(fav);
        }
        cursor.close();
        return favourites;
    }

    public void removeFavourite(SQLiteDatabase db, String device, String load){
        String[] params = new String[]{device, load};
        db.delete(TABLE_NAME, CRUD_FAVOURITE, params);
    }
}
